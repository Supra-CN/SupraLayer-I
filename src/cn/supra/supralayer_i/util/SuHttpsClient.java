package cn.supra.supralayer_i.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * TODO  这个类和SuHttpClient的接口不一致，需要修改；同时没有提供get方法，没有支持类似cnwap的代理
 * @author androidyue
 *
 */
public class SuHttpsClient {
	private static final String LOGTAG = SuHttpsClient.class.getSimpleName();

	//TODO 下面的变量既然用于post，则名称不符，应增加get的contentType
	private String mContentType = "application/x-www-form-urlencoded";
	
	// Create a trust manager that does not validate certificate chains
	TrustManager[] trustAllCerts = new TrustManager[]{
	    new X509TrustManager() {
	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }
	        public void checkClientTrusted(
	            java.security.cert.X509Certificate[] certs, String authType) {
	        }
	        public void checkServerTrusted(
	            java.security.cert.X509Certificate[] certs, String authType) {
	        }
	    }
	};
	
	
	public SuHttpsClient() {
		SSLContext sc = null;
		try {
	        
	        try {
	            sc = SSLContext.getInstance("TLS");
	        } catch (NoSuchAlgorithmException ex) {
	            ex.printStackTrace();
	            sc = SSLContext.getInstance("SSL");
	        }
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setContentType(final String contentType) {
		mContentType = contentType;
	}
	
	public static class ResponseResult {
		public int responseCode;
		public String responseBody;
		public String maxthonCode;
		
		
		@Override
		public String toString() {
			return "ResponseResult [responseCode=" + responseCode
					+ ", responseBody=" + responseBody + "]";
		}
		
		
	}
	
	
	public String post(String url, final String data) throws SocketTimeoutException {
		ResponseResult result = postData(url, data);
		Log.i(LOGTAG, "post method result = " + result +",url="+url+",data="+data);
		if (null != result) {
			return result.responseBody;
		}
		return null;
	}
	
	public boolean doHttpsPost(final String url, final String data) throws SocketTimeoutException {
		return doHttpsPost(url, data, 0);
	}
	
	public boolean doHttpsPost(final String url, final String data,int retryCount) throws SocketTimeoutException {
		ResponseResult result = postData(url, data,retryCount);
		Log.i(LOGTAG, "post method result = " + result );
		if (null != result) {
			return 200 == result.responseCode;
		}
		return false;
	}
	
	public ResponseResult postData(String url, final String data) {
		return postData(url, data, 0);
	}
	
	public ResponseResult postData(String url, final String data,int retryCount) {
		ResponseResult result = null;
		boolean succeed = false;
		do{
			Log.d(LOGTAG, "https post url=" + url);
			result = doPost(url, data);
			succeed = result != null;
			if(!succeed){
				Log.d(LOGTAG, "fail to post url=" + url + ",retry times remain:" + retryCount);
			}
		}while(!succeed && retryCount-- > 0);
		return result;
	}

	private ResponseResult doPost(String url, final String data) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection)(new URL(url).openConnection());
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			connection.setUseCaches(false);
//			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", mContentType);
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
				
			});
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			ds.write(data.getBytes("UTF-8"));
			ds.flush();
			ds.close();
			String result = "";
			int responseCode = connection.getResponseCode();
			String code = connection.getHeaderField("X-Maxthon-Code");
			Log.i("usnd", "X-Maxthon-Code: "+code);
			Log.i("DoRequest ResponseCode",
					String.valueOf(responseCode));
			InputStream is = connection.getInputStream();
			InputStreamReader in = new InputStreamReader(is, "UTF-8");
			BufferedReader breader = new BufferedReader(in);

			String s = "";
			while ((s = breader.readLine()) != null) {
				result += s;
			}
			is.close();
			Log.i("DoRequest Result", result+" s: " + s);
			ResponseResult resResult = new ResponseResult();
			resResult.responseBody = result;
			resResult.responseCode = responseCode;
			resResult.maxthonCode = code;
			return resResult;
		} catch (MalformedURLException e) {
			Log.i("DoRequest", "MalformedURLException,postData  url = " + url + "; data = " + data);
			Log.i("usnd", "MalformedURLException: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("DoRequest", "IOException,postData  url = " + url + "; data = " + data);
			Log.i("usnd", "IOException: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	


}
