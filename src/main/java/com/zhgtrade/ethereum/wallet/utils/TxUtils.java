package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.zhgtrade.ethereum.wallet.model.Tx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-08 14:49
 */
public class TxUtils {

    public static void addTx(Tx tx) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("txid", tx.getTxid());
        map.put("account", tx.getAccount());
        map.put("address", tx.getAddress());
        map.put("category", tx.getCategory());
        map.put("amount", tx.getAmount());
        map.put("confirmations", tx.getConfirmations());
        map.put("blockhash", tx.getBlockhash());
        map.put("blockindex", tx.getBlockindex());
        map.put("blocktime", tx.getBlocktime());
        map.put("time", tx.getTime());
        map.put("timereceived", tx.getTimereceived());
        map.put("fee", tx.getFee());
        map.put("identify", tx.getIdentify());
        map.put("contractAddress", tx.getContractAddress());
        JdbcUtils.insertToTable(DBUtils.getDataSource(), "tx", map);
    }

    public static Tx getTx(String txid) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from tx where txid = ?", txid);
        Tx tx = null;
        if (list.size() > 0) {
            tx = ReflectUtils.convertToObject(list.get(0), Tx.class);
        }
        return tx;
    }

    private static Tx parseTxRecord(Map<String, Object> map) {
        return ReflectUtils.convertToObject(map, Tx.class);
    }

    public static List<Tx> listTransactions(String identify, Integer begin, Integer size) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select a.* from tx a, account b where a.address = b.address and a.identify = ? limit ?, ?", identify, begin, size);
        final List<Tx> txs = new ArrayList<>(list.size());
        list.forEach(row -> txs.add(parseTxRecord(row)));
        return txs;
    }
}
