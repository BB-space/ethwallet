package com.zhgtrade.ethereum.wallet;

import com.zhgtrade.ethereum.wallet.constant.TokenConstant;
import com.zhgtrade.ethereum.wallet.model.Account;
import com.zhgtrade.ethereum.wallet.model.Token;
import com.zhgtrade.ethereum.wallet.model.Tx;
import com.zhgtrade.ethereum.wallet.utils.*;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Convert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.web3j.utils.Convert.fromWei;

/**
 * Author:xuelin
 * Company:招股金服
 * Date:2017/8/17
 * Desc:
 */
public class BlockSyncTask implements Runnable {
    private Logger log = Logger.getLogger(getClass().getName());

    // 一次性只能同步五个币，太多处理不了
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    // Token 每次同步300个区块，提高同步速度
    private static final int SYNC_BLOCK_COUNT = 30;

    private final Event event = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));

    private EventValues extractEventParameters(
            Event event, Log log) {

        List<String> topics = log.getTopics();
        if(topics == null){
            return null;
        }
        if(topics.isEmpty()){
            return null;
        }

        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        if(topics.size() <= indexedParameters.size()){
            return null;
        }
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    private Tx parseTx(Log log) {
        try {
            EventValues eventValues = extractEventParameters(event, log);
            if (eventValues == null) {
                return null;
            }
            Address from = (Address) eventValues.getIndexedValues().get(0);
            Address to = (Address) eventValues.getIndexedValues().get(1);
            String category = null;
            if (AccountUtils.hasAccount(to.toString())) {
                category = "receive";
            } else if (AccountUtils.hasAccount(from.toString())) {
                category = "send";
            }else{
                return null;
            }
            Tx tx = new Tx();
            Uint256 value = (Uint256) eventValues.getNonIndexedValues().get(0);
            tx.setTxid(log.getTransactionHash() + "-" + to);
            tx.setAccount(from.toString());
            tx.setAddress(to.toString());
            tx.setCategory(category);
            tx.setAmount(fromWei(value.getValue().toString(), Convert.Unit.ETHER).toString());
            tx.setConfirmations("0");
            tx.setBlockhash(log.getBlockHash());
            tx.setBlockindex(log.getBlockNumber().toString());
            tx.setContractAddress(log.getAddress());
            tx.setBlocktime("");
            tx.setTime("");
            tx.setTimereceived("");
            tx.setFee("");
            return tx;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Tx parseTx(EthBlock.TransactionObject txObj,EthBlock.Block block) {
        String category = null;
        if (AccountUtils.hasAccount(txObj.getTo())) {
            category = "receive";
        } else if (AccountUtils.hasAccount(txObj.getFrom())) {
            category = "send";
        }
        Tx tx = new Tx();
        tx.setTxid(txObj.getHash() + "-" + txObj.getTo());
        tx.setAccount(txObj.getFrom());
        tx.setAddress(txObj.getTo());
        tx.setCategory(category);
        tx.setAmount(fromWei(txObj.getValue().toString(), Convert.Unit.ETHER).toString());
        tx.setConfirmations("0");
        tx.setBlockhash(txObj.getBlockHash());
        tx.setBlockindex(txObj.getBlockNumber().toString());
        tx.setBlocktime("");
        tx.setTime("");
        tx.setTimereceived("");
        BigDecimal gasPrice = Convert.fromWei(txObj.getGasPrice().toString(), Convert.Unit.ETHER);
        BigInteger gas = block.getGasUsed();
        tx.setFee(gasPrice.multiply(new BigDecimal(gas)).toString());
        return tx;
    }

    private void updateAmount(String identify, ContractToken contractToken, String address) throws ExecutionException, InterruptedException, SQLException {
        BigInteger balance = contractToken.balanceOf(new Address(address)).get().getValue();
        BigDecimal amount = fromWei(balance.toString(), Convert.Unit.ETHER);
        AccountUtils.updateAccountAmount(identify, address, amount);
    }

    private void updateAmount(String identify, String address) throws ExecutionException, InterruptedException, SQLException {
        Web3j web3j = Web3jUtils.getWeb3j();
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigDecimal amount = fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER);
        AccountUtils.updateAccountAmount(identify, address, amount);
    }

    private void insertTxAndUpdateBalance(ContractToken contractToken, Tx tx) {
        try {
            if (TxUtils.getTx(tx.getTxid()) == null) {
                log.info("insert tx " + tx.getTxid());
                TxUtils.addTx(tx);
                if (AccountUtils.hasAccount(tx.getAccount())) {
                    updateAmount(tx.getIdentify(), contractToken, tx.getAccount());
                }
                if (AccountUtils.hasAccount(tx.getAddress())) {
                    updateAmount(tx.getIdentify(), contractToken, tx.getAddress());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertTxAndUpdateBalance(Tx tx) {
        try {
            if (TxUtils.getTx(tx.getTxid()) == null) {
                log.info("insert tx " + tx.getTxid());
                TxUtils.addTx(tx);
                if (AccountUtils.hasAccount(tx.getAccount())) {
                    updateAmount(tx.getIdentify(), tx.getAccount());
                }
                if (AccountUtils.hasAccount(tx.getAddress())) {
                    updateAmount(tx.getIdentify(), tx.getAddress());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncEther() {
        try {
            Token token = TokenUtils.getTokenById(AccountUtils.getDefaultIdentify());
            Web3j web3j = Web3jUtils.getWeb3j();
            int fromBlock = 0;
            String syncKey = "sync-height-" + token.getName();
            String syncHeight = SyncinfoUtils.get(syncKey);
            if (syncHeight != null) {
                fromBlock = Integer.valueOf(syncHeight);
            }
            EthBlockNumber lastestBLockHeight = web3j.ethBlockNumber().sendAsync().get();

            while (fromBlock < lastestBLockHeight.getBlockNumber().intValue()) {
                log.info(token.getName() + " sync block " + fromBlock);

                EthBlock ethBlock = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(fromBlock), true).sendAsync().get();
                List<EthBlock.TransactionResult> txs = ethBlock.getBlock().getTransactions();
                txs.forEach(tx -> {
                    EthBlock.Block block = ethBlock.getBlock();
                    EthBlock.TransactionObject txObj = (EthBlock.TransactionObject) tx;
                    if (Objects.nonNull(txObj.getFrom()) && Objects.nonNull(txObj.getTo())) {
                        Tx txLog = parseTx(txObj,block);
                        if (txLog.getCategory() != null) {
                            txLog.setIdentify(token.getId().toString());
                            insertTxAndUpdateBalance(txLog);
                            log.info(token.getName() + " tx: " + txLog.toString());
                        }
                    }
                });

                fromBlock++;
                SyncinfoUtils.set(syncKey, fromBlock);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void syncToken() {
        try {
            Web3j web3j = Web3jUtils.getWeb3j();
            EthBlockNumber lastestBLockHeight = web3j.ethBlockNumber().sendAsync().get();
            int fromBlock = 0;
            String syncKey = "sync-height-token";
            String syncHeight = SyncinfoUtils.get(syncKey);
            if (syncHeight != null) {
                fromBlock = Integer.valueOf(syncHeight);
            }
            int latestBlockHeight = lastestBLockHeight.getBlockNumber().intValue();
            while (fromBlock < latestBlockHeight) {
                log.info("token sync block " + fromBlock);
                EthFilter filter = new EthFilter(new DefaultBlockParameterNumber(fromBlock), new DefaultBlockParameterNumber(fromBlock + SYNC_BLOCK_COUNT), new ArrayList<>());
                List<EthLog.LogResult> logs = web3j.ethGetLogs(filter).sendAsync().get().getLogs();
                if (logs != null) {
                    logs.forEach((EthLog.LogResult logResult) -> {
                        Log log = (Log) logResult.get();
                        Tx tx = parseTx(log);
                        if (tx != null && tx.getCategory() != null && tx.getContractAddress() != null) {
                            checkTokenIsExist(checkTxAndAccount(tx));
                        }
                    });
                }
                fromBlock += SYNC_BLOCK_COUNT;
                if (fromBlock > latestBlockHeight) {
                    fromBlock = latestBlockHeight;
                }
                SyncinfoUtils.set(syncKey, fromBlock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTokenIsExist(Tx tx){
        try {
            String address = tx.getAddress();
            if("send".equals(tx.getCategory())){
                address = tx.getAccount();
            }
            Token token = TokenUtils.getToken(tx.getContractAddress());
            if(token == null){
                Token ethToken = TokenUtils.getTokenById(AccountUtils.getDefaultIdentify());
                token = new Token();
                token.setName("token");
                token.setType("token");
                token.setContractAddress(tx.getContractAddress());
                token.setAccessPassword(UUID.randomUUID().toString());
                token.setLimitGas(TokenConstant.token_gas_limit.intValue());
                token.setUnlockPassword(ethToken.getUnlockPassword());
                token.setUnit(tx.getUnit());
                TokenUtils.addToken(token);
                token = TokenUtils.getToken(token.getContractAddress());
            }
            Account account = AccountUtils.getAccount(token.getId().toString(),address);
            if(account == null){
                account = new Account();
                account.setAddress(address);
                account.setAmount(tx.getAmount());
                account.setUserId(System.currentTimeMillis()+"");
                account.setIdentify(token.getId()+"");
                account.setType("token");
                AccountUtils.addAccount(account);
            }
            Web3j web3j = Web3jUtils.getWeb3j();
            ContractToken contractToken = ContractToken.load(token.getContractAddress(), web3j, AccountUtils.getCredentials(address, token.getUnlockPassword()), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            tx.setIdentify(token.getId().toString());
            insertTxAndUpdateBalance(contractToken, tx);
        } catch (SQLException e) {
           log.error("write data error:"+e);
            e.printStackTrace();
        }catch (Exception e){
            log.error("write data error:"+e);
            e.printStackTrace();
        }
    }

    public Tx checkTxAndAccount(Tx tx){
        try {
            String response = HttpClientUtil.doGet(ConfigUtils.getConfig("request.token.url")+tx.getContractAddress());
            String startStr = "Token Decimals:&nbsp;\n</td>\n<td>\n";
            String endStr = "\n</td>\n</tr>";
            int start = response.indexOf(startStr);
            response = response.substring(start);
            int end = response.indexOf(endStr);
            response = response.substring(startStr.length(),end);
            Integer unit = Integer.parseInt(response.trim());
            BigDecimal amount = new BigDecimal(tx.getAmount());
            tx.setUnit(18);
            if(unit != 18){
                if(unit < 18){
                    Integer temp = 18-unit;
                    amount = amount.multiply(BigDecimal.TEN.pow(temp));
                }else{
                    Integer temp = unit-18;
                    amount = amount.divide(BigDecimal.TEN.pow(temp));
                }
                tx.setAmount(amount.toString());
                tx.setUnit(unit);
            }
            return tx;
        } catch (Exception e) {
            log.error("check tx and account error:"+e);
            return tx;
        }
    }

    public void sync() {
        try {
            CountDownLatch latch = new CountDownLatch(2);
            executorService.submit(() -> {
                try {
                    this.syncEther();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            });
            executorService.submit(() -> {
                try {
                    this.syncToken();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            });
            latch.await();
            log.info("sync orver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (;;) {
            sync();
            try {
                Thread.sleep(1000 * 3);
            } catch (Exception e) {

            }
        }
    }

    public static void main(String[] args) {
        try {
            Web3j web3j = Web3jUtils.getWeb3j();
            int fromnumber = 4216000;
            while (true){
                System.out.println(fromnumber);
                EthFilter filter = new EthFilter(new DefaultBlockParameterNumber(fromnumber), new DefaultBlockParameterNumber(fromnumber +=10), new ArrayList<>());
                List<EthLog.LogResult> logs = web3j.ethGetLogs(filter).sendAsync().get().getLogs();
                if (logs != null) {
                    Tx tx = null;
                    System.out.println("logs.size():"+logs.size());
                    for(EthLog.LogResult logResult:logs) {
                        Log log = (Log) logResult.get();
                        tx = new BlockSyncTask().parseTx(log);
                        if(tx != null){

                        }
//                        break;
                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
