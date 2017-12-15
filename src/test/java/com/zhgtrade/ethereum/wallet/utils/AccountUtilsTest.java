package com.zhgtrade.ethereum.wallet.utils;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.eclipse.jetty.util.HostMap;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock.Isolation.UR;
import static javafx.scene.input.KeyCode.H;
import static org.bouncycastle.asn1.ua.DSTU4145NamedCurves.params;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.c;

/**
 * Author:xuelin
 * Company:招股金服
 * Date:2017/8/16
 * Desc:
 */
public class AccountUtilsTest {

    @Test
    public void testSendToken() throws Exception {
        String param = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_estimateGas\",\"params\":[{\"from\":\"0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0\",\"to\":\"0x72edd2c74e04523cc06862c47c95d2e3ce062d10\"}],\"id\":1}";
        String response = HttpClientUtil.doPostJson("http://118.190.132.141:8545",param);
        System.out.println(response);
        // TODO 多个参数时使用例子
//      String[] temp = new String[]{"0x12341234"};
//      Object[] params = new Object[]{"0x1", "0x2", "0x8888f1f195afa192cfee860698584c030f4c9db1", temp};

        // 密码为123456
        Object[] params = new Object[]{};
        String methodName = "eth_gasPrice";
        try {
            JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://118.190.132.141:8545"));
            Object result = client.invoke(methodName, params, Object.class);
            System.out.println(result.toString());
            Web3j web3j = Web3jUtils.getWeb3j();
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
           System.out.println(gasPrice.toString());
//            caclFee("0x58ce162c140a185dbf4098c0caefd7425d94ccad");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    private Integer caclFee(String from) throws Exception{
        Web3j web3j = Web3jUtils.getWeb3j();
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
        org.web3j.protocol.core.methods.request.Transaction mockTx = org.web3j.protocol.core.methods.request.Transaction.createContractTransaction(from, nonce, gasPrice, null);
        EthEstimateGas estimateGas = web3j.ethEstimateGas(mockTx).sendAsync().get();
        BigInteger gas = estimateGas.getAmountUsed();
        return gas.intValue();
    }
}
