package com.zhgtrade.ethereum.wallet.constant;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author xxp
 * @version 2017- 09- 21 18:33
 * @description
 * @copyright www.zhgtrade.com
 */
public class TokenConstant {

    public static final BigInteger token_gas_limit = new BigInteger("60000");
    public static final BigInteger token_gas_price = new BigInteger("1000000000");
    public static final BigDecimal require_fee_ether = Convert.fromWei(token_gas_price.multiply(token_gas_limit).toString(), Convert.Unit.ETHER);
}
