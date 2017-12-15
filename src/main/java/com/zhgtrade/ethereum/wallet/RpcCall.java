package com.zhgtrade.ethereum.wallet;

import com.zhgtrade.ethereum.wallet.model.Token;

import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-09 16:11
 */
public interface RpcCall {

    Object execute(Token token, Map<String, Object> params) throws Exception;

}
