package com.zhgtrade.ethereum.wallet.model;

import java.util.List;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-08 11:50
 */
public class Tx {

    private String txid;
    private String account;
    private String address;
    private String contractAddress;
    private String category;
    private String amount;
    private String confirmations;
    private String blockhash;
    private String blockindex;
    private String blocktime;
    private String time;
    private String timereceived;
    private String fee;
    private String identify;
    private List details;
    private Integer unit;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public String getBlockindex() {
        return blockindex;
    }

    public void setBlockindex(String blockindex) {
        this.blockindex = blockindex;
    }

    public String getBlocktime() {
        return blocktime;
    }

    public void setBlocktime(String blocktime) {
        this.blocktime = blocktime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimereceived() {
        return timereceived;
    }

    public void setTimereceived(String timereceived) {
        this.timereceived = timereceived;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public List getDetails() {
        return details;
    }

    public void setDetails(List details) {
        this.details = details;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Tx{" +
                "txid='" + txid + '\'' +
                ", account='" + account + '\'' +
                ", address='" + address + '\'' +
                ", category='" + category + '\'' +
                ", amount='" + amount + '\'' +
                ", confirmations=" + confirmations +
                ", blockhash='" + blockhash + '\'' +
                ", blockindex='" + blockindex + '\'' +
                ", blocktime='" + blocktime + '\'' +
                ", time='" + time + '\'' +
                ", timereceived='" + timereceived + '\'' +
                ", fee='" + fee + '\'' +
                ", identify='" + identify + '\'' +
                '}';
    }
}
