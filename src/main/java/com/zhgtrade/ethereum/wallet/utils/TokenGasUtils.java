package com.zhgtrade.ethereum.wallet.utils;

import com.zhgtrade.ethereum.wallet.model.Account;
import com.zhgtrade.ethereum.wallet.model.Token;
import org.apache.log4j.Logger;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigInteger;

/**
 * @author xxp
 * @version 2017- 09- 14 10:16
 * @description
 * @copyright www.zhgtrade.com
 */
public class TokenGasUtils {
    private static Logger log = Logger.getLogger(TokenGasUtils.class.getName());

    public static boolean checkTransactionGas(Token token,String fromAddress) throws Exception{
        if (AccountUtils.ETHER_TYPE.equals(token.getType())) {
            return true;
        }
        Account account = AccountUtils.getAccount(fromAddress);
        log.info("send transaction gas to " + token.getName() + " address");
        Token ethToken = TokenUtils.getToken(AccountUtils.getDefaultIdentify());
        Web3j web3j = Web3jUtils.getWeb3j();
        BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
        //token表中的limitGas包含了eth向token转入的手续费
        BigInteger limitGas = new BigInteger(token.getLimitGas()+"");
        if(limitGas.compareTo(Transfer.GAS_LIMIT) <= 0){
            log.error("token fee too small:fromAddress"+account.getAddress());
            throw new Exception("token fee too small");
        }
        limitGas = limitGas.subtract(Transfer.GAS_LIMIT);
        BigInteger ethBalance = web3j.ethGetBalance(account.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
        BigInteger fee = gasPrice.multiply(limitGas);
        if (ethBalance.compareTo(fee) == -1) {
            BigInteger ethBalancePending = web3j.ethGetBalance(account.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get().getBalance();
            if(ethBalancePending.compareTo(fee) > -1)return false;
            ethBalance = ethBalance .add(ethBalancePending);
            if(ethBalance.compareTo(fee) >=0) return false;

            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(account.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            Transaction mockTx = Transaction.createContractTransaction(account.getAddress(), nonce, gasPrice, "");
            EthEstimateGas estimateGas = web3j.ethEstimateGas(mockTx).sendAsync().get();
            BigInteger gas = estimateGas.getAmountUsed();
            if(limitGas.compareTo(gas) == -1){
                fee = gasPrice.multiply(gas);
            }
            BigInteger requireFee = fee.subtract(ethBalance);
            String requireFeeStr = Convert.fromWei(requireFee.toString(), Convert.Unit.ETHER).toPlainString();
            log.info("send transaction gas requireFee"+ requireFeeStr +" to " + token.getName() + " address");
            AccountUtils.unLockAccount(ethToken.getMainAccount(), token.getMainPassword(), 30);
            AccountUtils.sendTransaction(ethToken.getId() + "",ethToken.getMainAccount(), account.getAddress(), requireFeeStr, "transaction gas fee");
            AccountUtils.lockAccount(ethToken.getMainAccount());
            return false;
        }
        return true;
    }
}
