package com.zhgtrade.ethereum.wallet.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

	protected static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	public static HttpClient httpClient = null;

	public static HttpClient getInstance() {
		if (httpClient == null) {
			httpClient = HttpClients.createDefault();
		}
		return httpClient;
	}

	public static void disconnect() {
		httpClient = null;
	}

	public static String doGet(String url) throws Exception {
		/* 建立HTTP get连线 */
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = HttpClientUtil.getInstance().execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(httpResponse.getEntity());
			}
			logger.error("doGet Error Response: " + httpResponse.getStatusLine().toString());
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public static String doPost(String url) throws Exception {
		return doPost(url, new HashMap<String, String>());
	}

	/**
	 * post方式提交请求
	 */
	public static String doPost(String url, Map<String, String> params) {
		String result = "";
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					formparams.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
		}
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		try {
			String tokenUrl = "https://etherscan.io/token/0xd9169c87effffb7adf4d078104637c115ebedbc6";
			String token = HttpClientUtil.doGet(tokenUrl);
			System.err.println(token);
			//0KIL4cjVikQy92v-797eAZ18ypJM_Z3Eybe-7v4L2dObHOaQuH2LqJb3neMlFKZBHyl9LUORTskflP9eNwL0kHFHt8DIyUBva1Nnp66hDyE
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String doPostJson(String url, String jsonData) {
		String result = "";
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		try {
			HttpPost method = new HttpPost(url);
			StringEntity entity = new StringEntity(jsonData, "utf-8");// 解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json;charset=UTF-8");
			method.setEntity(entity);

			CloseableHttpResponse response = httpclient.execute(method);
			try {
				HttpEntity resultEntity = response.getEntity();
				if (resultEntity != null) {
					result = EntityUtils.toString(resultEntity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}