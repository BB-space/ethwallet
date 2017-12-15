package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhgtrade.ethereum.wallet.ContractToken;
import com.zhgtrade.ethereum.wallet.constant.TokenConstant;
import com.zhgtrade.ethereum.wallet.model.Account;
import com.zhgtrade.ethereum.wallet.model.Token;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-07 16:22
 */
public class AccountUtils {

    private static Logger log = Logger.getLogger(AccountUtils.class.getName());

    private static Map<String, String> cacheAccount = new HashMap<>();

    private static Map<String, String> unlockAccountPassMap = new ConcurrentHashMap<>();
    private static Map<String, Long> unlockAccountTimeoutMap = new ConcurrentHashMap<>();

    private static final String ETHER_IDENTIFY = "17";  // 以太坊ETH默认ID，跟token表的一致
    public static final String ETHER_TYPE = ConfigUtils.getConfig("ether.type");

    private static String getKeyStoreDir() {
        return "../keystore";
//        return "keystore-test";
    }

    public static String getDefaultIdentify() {
        return ETHER_IDENTIFY;
    }

    private static void autoLockAccount() {
        for (;;) {
            Set<String> addresses = unlockAccountTimeoutMap.keySet();
            for (String address : addresses) {
                if (unlockAccountTimeoutMap.get(address) < System.currentTimeMillis()) {
                    log.info("lockAddress " + address);
                    unlockAccountTimeoutMap.remove(address);
                    unlockAccountPassMap.remove(address);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    private static void startAccountManagerThread() {
        Thread accountManagerThread = new Thread(AccountUtils::autoLockAccount);
        accountManagerThread.setName("account-manager-thread");
        accountManagerThread.start();
    }

    static {
        loadAddressFromDisk();
        startAccountManagerThread();
    }

    public static String getBalance(String identify) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "SELECT IFNULL(SUM(amount), 0) amount FROM account where identify = ?", identify);
        String balance = "0";
        if (list.size() > 0) {
            balance = list.get(0).get("amount") + "";
        }
        return balance;
    }

    private static Account parseAccountRecord(Map<String, Object> map) {
        return ReflectUtils.convertToObject(map, Account.class);
    }

    public static String getBalanceById(String id) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "SELECT IFNULL(SUM(amount), 0) amount FROM account where id = ?", id);
        String balance = "0";
        if (list.size() > 0) {
            balance = list.get(0).get("amount") + "";
        }
        return balance;
    }

    public static String newAccount(String userId, String contractAddress) throws SQLException {
        Token token = TokenUtils.getToken(contractAddress);
        if (token == null) {
            return null;
        }
        return newAccount(userId, token.getUnlockPassword(), token.getId()+"");
    }

    public static String newAccount(String userId, String password, String identify) throws SQLException {
        String address = null;
        try {
            if (password == null || password.trim().length() == 0) {
                throw new IllegalArgumentException("password must be not null");
            }
            String fileName = WalletUtils.generateFullNewWalletFile(password, new File(getKeyStoreDir()));
            Credentials credentials = WalletUtils.loadCredentials(password, getKeyStoreDir() + File.separator + fileName);
            // 新增地址，放到内存，方便索引
            if (credentials.getAddress().startsWith("0x")) {
                String addr = credentials.getAddress().substring(2);
                cacheAccount.put(addr, fileName);
            }
            address = credentials.getAddress();
            Account account = new Account();
            account.setAddress(address);
            account.setAmount("0");
            account.setIdentify(identify);
            account.setUserId(userId);
            account.setType("eth");
            addAccount(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static void addAccount(Account account) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("address", account.getAddress());
        map.put("amount", account.getAmount());
        map.put("userId", account.getUserId());
        map.put("identify", account.getIdentify());
        map.put("type", account.getType());
        JdbcUtils.insertToTable(DBUtils.getDataSource(), "account", map);
    }

    public static String sendTransaction(Token token, String from, String to, String money, String fee) {
        String txid = null;
        try {
            if (token != null) {
                log.info("send transaction from "+from+ " money "+ money +" to " + token.getName() + " address");
                String password = getUnlockPassword(from);
                if (password == null || password.length() == 0) {
                    throw new IllegalStateException("Account require unlock.");
                }
                Credentials mainAccountCredentials = getCredentials(from, password);
                int unit = token.getUnit();
                BigDecimal relMoney = new BigDecimal(money);
                if(unit != 18){
                    if(unit < 18){
                        Integer temp = 18-unit;
                        relMoney = relMoney.divide(BigDecimal.TEN.pow(temp));
                    }else{
                        Integer temp = unit-18;
                        relMoney = relMoney.multiply(BigDecimal.TEN.pow(temp));
                    }
                }
                BigInteger amount = Convert.toWei(relMoney, Convert.Unit.ETHER).toBigInteger();

                Web3j web3 = Web3jUtils.getWeb3j();
                BigInteger gas = new BigInteger(token.getLimitGas().toString());

                BigInteger feeAmount = Convert.toWei(fee, Convert.Unit.ETHER).toBigInteger();
                BigInteger gasPrice = feeAmount.divide(gas);
                if(gasPrice.compareTo(TokenConstant.token_gas_price) == -1 ){
                    gasPrice = TokenConstant.token_gas_price;
                }
                // 转币分两种情况，一种是直接转以太币，一种是转Token
                if (ETHER_TYPE.equals(token.getType())) {
                    BigInteger ethBalance = web3.ethGetBalance(from, DefaultBlockParameterName.PENDING).sendAsync().get().getBalance();
                    if (ethBalance.compareTo(amount) == -1) {
                        throw new IllegalStateException("Insufficient funds");
                    }
//                    BigInteger  gasPrice1 = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).sendAsync().get();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    log.info("send " + token.getName() + " tx none: " + nonce);
                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gas, to, amount);
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, mainAccountCredentials);
                    String hexValue = Hex.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction("0x" + hexValue).sendAsync().get();
                    txid = ethSendTransaction.getTransactionHash();
                } else {
                    EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).sendAsync().get();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    log.info("send " + token.getName() + " tx none: " + nonce);
                    ContractToken contractToken = ContractToken.load(token.getContractAddress(), Web3jUtils.getWeb3j(), mainAccountCredentials, gasPrice, gas);
                    Uint256 fromBalance = contractToken.balanceOf(new Address(from)).get();
                    if (fromBalance.getValue().compareTo(amount) == -1) {
                        throw new IllegalStateException("Insufficient funds");
                    }
                    TransactionReceipt tx = contractToken.transfer(new Address(to), new Uint256(amount)).get();
                    txid = tx.getTransactionHash();
                }
                log.info("send " + token.getName() + "transaction " + money + " gas to " + to + ", txid = " + txid);
            } else {
                log.warn("call sendTransaction error, unknown token " + token.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendTransaction " + token.getName() + " from = " + money + " to = " + to + " money = " + money, e);
        }
        return txid;
    }

    public static String sendTransaction(Token token, String to, String money, String message) {
        String txid = null;
        /*try {
            String from = token.getContractAddress();
            if (token != null) {
                txid = sendTransaction(token.getId().toString(), from, to, money, message);
            } else {
                log.error("call sendTransaction error, unknown token " + identify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendTransaction " + identify + " to = " + to + " money = " + money, e);
        }*/
        return txid;
    }

    public static Credentials getCredentials(String address, String password) {
        if (address.startsWith("0x")) {
            address = address.substring(2);
        }
        String path = cacheAccount.get(address);
        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(password, getKeyStoreDir() + File.separator + path);
        } catch (IOException | CipherException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    private static String getUnlockPassword(String address) {
        if (address.startsWith("0x")) {
            address = address.substring(2);
        }
        return unlockAccountPassMap.get(address);
    }

    public static boolean unLockAccount(String address, String password, int timeout) {
        try {
            if (address.startsWith("0x")) {
                address = address.substring(2);
            }
            // 第二次解锁，会直接返回成功，并且在原来解锁的基础上加上本次解锁的时间
            if (password != null && password.equals(unlockAccountPassMap.get(address))) {
                Long timeoutTime = unlockAccountTimeoutMap.get(address);
                timeoutTime += (timeout * 1000);
                unlockAccountTimeoutMap.put(address, timeoutTime);
                return true;
            }
            String path = cacheAccount.get(address);
            if (path != null) {
                Credentials credentials = WalletUtils.loadCredentials(password, getKeyStoreDir() + File.separator + path);
                if (credentials != null) {
                    unlockAccountPassMap.put(address, password);
                    unlockAccountTimeoutMap.put(address, (System.currentTimeMillis() + (timeout * 1000)));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean lockAccount(String address) {
        // TODO
        if (address.startsWith("0x")) {
            address = address.substring(2);
        }
        return true;
    }

    public static boolean hasAccount(String address) {
        if (address.startsWith("0x")) {
            address = address.substring(2);
        }
        return cacheAccount.get(address) != null;
    }

    private static void loadAddressFromDisk() {
        File keyDir = new File(getKeyStoreDir());
        File[] files = keyDir.listFiles();
        for (File file : files) {
            try {
                ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
                WalletFile walletFile = objectMapper.readValue(file, WalletFile.class);
                cacheAccount.put(walletFile.getAddress(), file.getName());
            } catch (Exception e) {
                System.err.println("load " + file.getName() + " fails");
                e.printStackTrace();
            }
        }
    }

    public static void updateAccountAmount(String identify, String address, BigDecimal amount) throws SQLException {
        JdbcUtils.executeUpdate(DBUtils.getDataSource(), "update account set amount = ? where identify = ? and address = ?", amount, identify, address);
    }

    public static List<Account> listAccounts(String identify) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from account where identify = ? and amount > 0", identify);
        final List<Account> accounts = new ArrayList<>(list.size());
        list.forEach(row -> {
            Account account = new Account();
            account.setUserId(row.get("userId") + "");
            account.setIdentify(row.get("identify") + "");
            account.setAmount(row.get("amount") + "");
            account.setAddress(row.get("address") + "");
            account.setType(row.get("type") + "");
            accounts.add(account);
        });
        return accounts;
    }

    public static Account getAccount(String identify,String address) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from account where identify = ? and address=?", identify,address);
        Account account = null;
        if (list.size() > 0) {
            account = parseAccountRecord(list.get(0));
        }
        return account;
    }

    public static void main(String[] args) throws Exception{
        Token token = new Token();
        token.setType("ETH");
        token.setLimitGas(22000);
         System.out.println(unLockAccount("0xcd2bb5b5b2b47ca5e2cfb0637d74ffabbde382fa", "eth2017pass", 30));
        System.out.println(sendTransaction(token, "0xcd2bb5b5b2b47ca5e2cfb0637d74ffabbde382fa", "0x58ce162c140a185dbf4098c0caefd7425d94ccad", "0.000078", "0.000022"));
//        System.out.println(sendTransaction("17", "0x71345ee25ce7ee60d9d9d45a2e2ea88aa82c43f1", "0x62beac5807e78fc10a06ff42cfe30fd2905c6e3f", "1", "xxx"));
// try {
//            System.out.println(newAccount("3","17"));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }
}
