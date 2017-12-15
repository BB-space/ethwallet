package com.zhgtrade.ethereum.wallet;

import com.alibaba.druid.util.StringUtils;
import com.zhgtrade.ethereum.wallet.constant.TokenConstant;
import com.zhgtrade.ethereum.wallet.model.Account;
import com.zhgtrade.ethereum.wallet.model.Token;
import com.zhgtrade.ethereum.wallet.model.Tx;
import com.zhgtrade.ethereum.wallet.utils.*;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-09 14:12
 */
public class Api {

    public static Object getBalance(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        String balance = null;
        List<Object> rpcParams = (List<Object>) params.get("params");
        //This is for compatibility with bitcoin
        if(rpcParams != null && rpcParams.size() > 0){
            balance = AccountUtils.getBalanceById(rpcParams.get(0).toString());
        }else{
            balance = "0.0";
        }
        if (balance == null) {
            error = "getBalance error.";
        }
        ret.put("result", balance);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object getAccount(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        List<Object> rpcParams = (List<Object>) params.get("params");
        Account account = AccountUtils.getAccount(token.getId().toString(),rpcParams.get(0).toString());
        Integer id = null;
        if (account == null) {
            error = "getAccount error.";
        }else{
            id = account.getId();
        }
        ret.put("result", id);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }


    public static Object getTransaction(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        Tx tx = TxUtils.getTx(((List<String>) params.get("params")).get(0));
        if (tx == null) {
            error = "no such transaction.";
        } else {
            String txid = tx.getTxid().split("-")[0];
            EthTransaction ethTransaction = Web3jUtils.getWeb3j().ethGetTransactionByHash(txid).sendAsync().get();
            Transaction ethTx = ethTransaction.getResult();
            if (ethTx != null) {
                BigInteger blockHeight = Web3jUtils.getWeb3j().ethBlockNumber().sendAsync().get().getBlockNumber();
                BigInteger confirmations = blockHeight.subtract(ethTx.getBlockNumber());
                tx.setConfirmations(confirmations.toString());
                tx.setBlockindex(ethTx.getBlockNumber().toString());
                EthGetTransactionReceipt receipt = Web3jUtils.getWeb3j().ethGetTransactionReceipt(ethTx.getHash()).sendAsync().get();
                TransactionReceipt transactionReceipt = receipt.getTransactionReceipt().get();
                BigDecimal gasPrice = Convert.fromWei(ethTx.getGasPrice().toString(), Convert.Unit.ETHER);
                BigInteger gasUsed = transactionReceipt.getGasUsed();
                tx.setFee(gasPrice.multiply(new BigDecimal(gasUsed)).toString());
            }
            tx.setDetails(Collections.emptyList());
        }
        ret.put("result", tx);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object getNewAccount(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        String address = AccountUtils.newAccount(((List<String>) params.get("params")).get(0), token.getContractAddress());
        if (address == null) {
            error = "new address error.";
        }
        ret.put("result", address);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object lockAccount(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        boolean lockRet = AccountUtils.lockAccount(((List<String>) params.get("params")).get(0));
        if (!lockRet) {
            error = "walletlock fails.";
        }
        ret.put("result", lockRet);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object unLockAccount(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        List<Object> rpcParams = (List<Object>) params.get("params");
//        Token token = TokenUtils.getToken(identify);
        boolean lockRet = AccountUtils.unLockAccount(rpcParams.get(0).toString(), rpcParams.get(1).toString(), (Integer) rpcParams.get(2));
        if (!lockRet) {
            error = "walletpassphrase fails.";
        }
        ret.put("result", lockRet);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object sendTransaction(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        List<Object> rpcParams = (List<Object>) params.get("params");
        String txid = AccountUtils.sendTransaction(token, rpcParams.get(0).toString(), rpcParams.get(1).toString(), rpcParams.get(2).toString());
        if (txid == null || txid.trim().length() == 0) {
            error = "sendtoaddress fails.";
        }
        ret.put("result", txid);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object sendfrom(Token token, Map<String, Object> params) throws Exception {
        List<Object> rpcParams = (List<Object>) params.get("params");
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        String txid = null;
        boolean lockRet = false;
        lockRet = AccountUtils.unLockAccount(rpcParams.get(0).toString(),token.getUnlockPassword(),30);
        if (!lockRet) {
            error = "walletpassphrase fails.";
        }else{
            txid = AccountUtils.sendTransaction(token, rpcParams.get(0).toString(), rpcParams.get(1).toString(), rpcParams.get(2).toString(), rpcParams.get(3).toString());
            if (txid == null || txid.trim().length() == 0) {
                error = "sendtoaddress fails.";
            }
        }
        ret.put("result", txid);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object settxfee(Token token, Map<String, Object> params) throws Exception{
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        String result = null;
        List<Object> rpcParams = (List<Object>) params.get("params");
        String ethfee =  rpcParams.get(0).toString();
        if(StringUtils.isEmpty(ethfee)){
            error = "fee is null fails.";
        }else{
            BigInteger gasPrice = TokenConstant.token_gas_price;
            BigInteger amount = Convert.toWei(ethfee, Convert.Unit.ETHER).toBigInteger();
            if(amount.compareTo(gasPrice) == 1 ){
                Integer limitGas = amount.divide(gasPrice).intValue();
                TokenUtils.updateToken(token.getId()+"",limitGas);
                result = "true";
            }else{
                error = "fee too small.";
            }
        }
        ret.put("result", result);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }

    public static Object listTransactions(Token token, Map<String, Object> params) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        String error = null;
        List<Object> rpcParams = (List<Object>) params.get("params");
        Integer size = (Integer) rpcParams.get(1);
        Integer begin = (Integer) rpcParams.get(2);
        List<Tx> txs = TxUtils.listTransactions(token.getId()+"", begin, size);
        ret.put("result", txs);
        ret.put("id", params.get("id"));
        ret.put("error", error);
        return ret;
    }



}
