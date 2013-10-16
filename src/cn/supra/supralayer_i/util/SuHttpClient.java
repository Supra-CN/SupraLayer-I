package cn.supra.supralayer_i.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *  此类封装Http相关的一些交互
 */
public class SuHttpClient {
	
	private static final String LOGTAG = "SuHttpClient";
	
	private String mPostHeaderContentType = null;

	private String mReferer;
	
	public static final int RETRY_COUNT=3;
	
	private void buildRequestHeader(HttpRequestBase request)
	{
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");  
		request.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008101315 Ubuntu/8.10 (intrepid) Firefox/3.0.3");
		request.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		//Accept-Encoding: gzip,deflate,sdch
		request.setHeader("Accept-Encoding", "gzip,deflate,sdch");
	}
	
	public void setPostContentType(String contentType) {
		mPostHeaderContentType = contentType;
	}
	

	public void setReferer(final String referer) {
		mReferer = referer;
	}
	
	public HttpClient getSuHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 30000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient http = new DefaultHttpClient(httpParameters);
		if(AppUtils.isCmWap()) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80); 
			http.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return http;
		
	}
	
	public HttpClient getSuHttpClient(HttpParams httpParams) {
		HttpClient http = new DefaultHttpClient(httpParams);
		if(AppUtils.isCmWap()) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80); 
			http.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return http;
	}
	
	//RVL
	public  HttpURLConnection getSuURLConnection(URL url) {
		HttpURLConnection connection=null;
		try {
			
			if(AppUtils.isCmWap()) 
			{
			Proxy proxy=new Proxy(java.net.Proxy.Type.HTTP,new InetSocketAddress("10.0.0.172",80));
			connection=(HttpURLConnection) url.openConnection(proxy);
			}else {
				connection=(HttpURLConnection) url.openConnection();
			}
			
			return connection;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return connection;
	}
	
	public HttpResponse get(String url) throws IOException,ClientProtocolException,Exception{
		return get(url,0);
	}
	
	/**
	 *收到服务端的响应就认为成功
	 * @param url
	 * @param retryCount
	 */
	public HttpResponse get(String url,int retryCount) throws IOException,ClientProtocolException,Exception{
		return get(url, retryCount,null);
	}
	
	/**
	 *收到服务端的响应，并且状态码为200时才认为成功
	 * @param url
	 * @param retryCount
	 */
	public HttpResponse get4OKResponse(String url,int retryCount) throws IOException,ClientProtocolException,Exception{
		return get(url, retryCount,HttpStatus.SC_OK);
	}
	
	/**
	 * send a http GET request
	 * @param url
	 * @param retryCount 连结失败时的重试次数
	 * ＠param successCodes 认为成功的状态码数组，例如200,206,也可以组合各种异常状态码，如：404,
	 * 如果为null,则只要获得服务器的返回信息就认为成功
	 * @return HttpResponse
	 * @throws IOException,ClientProtocolException,Exception 
	 * @throws Exception 
	 */
	public HttpResponse get(String url, int retryCount, int... successCodes) throws IOException,ClientProtocolException,Exception{
		HttpResponse response = null;
		boolean succeed = false;
		// 对数组排序，以便在后面查找相应代码
		if(successCodes != null){
			Arrays.sort(successCodes);
		}
		do {
				try {
					response = doget(url);
				} catch (ClientProtocolException e) {
					Log.e(LOGTAG, e.getMessage() + "", e);
					throw e;
				} catch (IOException e) {
					Log.e(LOGTAG, e.getMessage() + "", e);
					if (retryCount <= 0) {
						throw e;
					}
				}catch(Exception e){
					Log.e(LOGTAG, e.getMessage() + "", e);
					if (retryCount <= 0) {
						throw e;
					}
				}
			if (response != null) {
				dumpResponseHeader(response, url);
				if(successCodes != null){
					int status = response.getStatusLine().getStatusCode();
					succeed = Arrays.binarySearch(successCodes, status) >= 0;
				}else{
					succeed = true;
				}
			}
			if(!succeed){
				Log.d(LOGTAG, "fail to get url=" + url + ",retry times remain:" + retryCount);
			}else{
				break;
			}
		}while(!succeed && retryCount-- > 0);
		return response;
	}

	private HttpResponse doget(String url) throws ClientProtocolException, IOException {
		HttpGet method = new HttpGet(url);
		buildRequestHeader(method);
		if (null != mReferer) {
			method.setHeader("Referer", mReferer);
		}
		dumpRequestHeader(method, url);
		HttpClient http = getSuHttpClient();
		HttpResponse response = http.execute(method);
		return response;
	}
	
	public byte[] getBody(String url) throws Exception {
		return getBody(url,0);
	}
	
	/**
	 * send a http GET request，并且状态成功才返回数据
	 * @param url
	 * @return byte[]
	 * @throws Exception 
	 */
	public byte[] getBody(String url,int retryCount) throws IOException,ClientProtocolException,Exception {
	
		HttpResponse response = get(url,retryCount);
		if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			byte[] rawData = extractBody(response);
			return rawData;
		}	
		return null;
	}

	public byte[] extractBody(HttpResponse response) throws IOException {
		byte[] rawData = EntityUtils.toByteArray(response.getEntity());
		if(isGzipContentEnc(response))  {
			return unzipData(rawData);
		}
		return rawData;
	}
	
	public String getBodyString(String url) throws IOException,ClientProtocolException,Exception{
		return getBodyString(url, 0);
	}
	
	/**
	 * 有返回，同时返回值为200时，才获取内容
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	public String getBodyString(String url,int retryCount) throws IOException,ClientProtocolException,Exception{
		HttpResponse response = get(url,retryCount);
		if(response != null&&response.getStatusLine().getStatusCode()==200){
			String bodyString = extractBodyString(response);
			return bodyString;
		}
		return null;
	}

	public String extractBodyString(HttpResponse response) throws IOException, UnsupportedEncodingException {
		byte[] body = EntityUtils.toByteArray(response.getEntity());
		if(isGzipContentEnc(response)){
			body = unzipData(body);
		}
		String charset = EntityUtils.getContentCharSet(response.getEntity());
		if(charset == null){
			charset = "UTF-8";
		}
		String bodyString= new String(body,charset);
		return bodyString;
	}
	
	public String postBodyString(String url,byte[] bytes) throws Exception {
		return postBodyString(url, bytes, 0);
	}
	
	/**
	 * 如果大小未知，返回值为负值
	 * @param response
	 */
	public long getLength(HttpResponse response){
		if(response != null){
			return response.getEntity().getContentLength();
		}
		return 0;
	}
	
	public String postBodyString(String url,byte[] bytes,int retryCount) throws IOException,ClientProtocolException,Exception {
		HttpResponse response = post(url,bytes,retryCount);
		if(response != null&&response.getStatusLine().getStatusCode()==200){
			byte[] body = EntityUtils.toByteArray(response.getEntity());
			if(isGzipContentEnc(response)){
				body = unzipData(body);
			}
			String charset = EntityUtils.getContentCharSet(response.getEntity());
			if(charset == null){
				charset = "UTF-8";
			}
			
			return new String(body, charset);
		}
		return null;
	}
	
	public HttpResponse post(String url,String content) throws IOException,ClientProtocolException,Exception {
		return post(url,content,0);
	}
	
	public HttpResponse post(String url,String content,int retryCount) throws IOException,ClientProtocolException,Exception {
		//Log.e(LOGTAG,"url="+url+",content="+content);
		return post(url,content.getBytes("utf-8"),retryCount);
	}
	
	public void setReferrer(final String referrer) {
		mReferer = referrer;
	}
	
	public HttpResponse post(String url,final byte[] data)  throws IOException,ClientProtocolException,Exception {
		return post(url,data,0);
	}
	
	public HttpResponse post(String url,final byte[] data,int retryCount)  throws IOException,ClientProtocolException,Exception {
		AbstractHttpEntity entity = genHttpEntity(data);
		return post(url,entity,retryCount);
	}

	private AbstractHttpEntity genHttpEntity(final byte[] data) {
		AbstractHttpEntity entity=new AbstractHttpEntity() {
			@Override
			public InputStream getContent() throws IOException,
					IllegalStateException {
				return new ByteArrayInputStream(data);
			}

			@Override
			public long getContentLength() {
				return data.length;
			}

			@Override
			public boolean isRepeatable() {
				return true;
			}

			@Override
			public boolean isStreaming() {
				return true;
			}

			@Override
			public void writeTo(OutputStream outstream) throws IOException {

				outstream.write(data);
				outstream.flush();
			}
			
		};
		return entity;
	}
	
	public HttpResponse post4OKResponse(String url,final byte[] data,int retryCount)  throws IOException,ClientProtocolException,Exception {
		AbstractHttpEntity entity = genHttpEntity(data);
		return post(url,entity,retryCount,HttpStatus.SC_OK);
	}
	
	public HttpResponse post(String url,List<NameValuePair> params) throws IOException,ClientProtocolException,Exception {
		return post(url,params,0);
	}
	
	public HttpResponse post(String url,List<NameValuePair> params,int retryCont) throws IOException,ClientProtocolException,Exception {
		return post(url,new UrlEncodedFormEntity(params, HTTP.UTF_8),retryCont);
	}
	
	public HttpResponse post(String url,HttpEntity entity)  throws Exception{
		return post(url, entity, 0);
	}

	/**
	 *收到服务端的响应就认为成功
	 * @param url
	 * @param retryCount
	 */
	public HttpResponse post(String url,HttpEntity entity,int retryCount)  throws IOException,ClientProtocolException,Exception{
		return post(url, entity, retryCount,null);
	}
	
	/**
	 *收到服务端的响应，并且状态码为200时才认为成功
	 * @param url
	 * @param retryCount
	 */
	public HttpResponse post4OKResponse(String url,HttpEntity entity,int retryCount)  throws IOException,ClientProtocolException,Exception{
		return post(url, entity, retryCount,HttpStatus.SC_OK);
	}
	
	/**
	 * send a http POST request
	 * @param url
	 * @param retryCount 连结失败时的重试次数
	 * ＠param successCodes 认为成功的状态码数组，例如200,206,也可以组合各种异常状态码，如：404,
	 * 如果为null,则只要获得服务器的返回信息就认为成功
	 * @return HttpResponse
	 * @throws IOException,ClientProtocolException,Exception 
	 * @throws Exception 
	 */
	public HttpResponse post(String url,HttpEntity entity,int retryCount,int... successCodes) throws IOException,ClientProtocolException,Exception{
		HttpResponse response = null;
		boolean succeed = false;
		if(successCodes != null){
			Arrays.sort(successCodes);
		}
		do{
				try {
					response = doPost(url, entity);
				} catch (ClientProtocolException e) {
					Log.e(LOGTAG, e.getMessage() + "", e);
					throw e;
				} catch (IOException e) {
					Log.e(LOGTAG, e.getMessage() + "", e);
					if (retryCount <= 0) {
						throw e;
					}
				}catch(Exception e){
					Log.e(LOGTAG, e.getMessage() + "", e);
					if (retryCount <= 0) {
						throw e;
					}
				}
			if (response != null) {
				if(successCodes != null){
					int status = response.getStatusLine().getStatusCode();
					succeed = Arrays.binarySearch(successCodes, status) >= 0;
				}else{
					succeed = true;
				}
				break;
			}
			if(!succeed){
				Log.d(LOGTAG, "fail to post url=" + url + ",retry times remain:" + retryCount);
			}else{
				break;
			}
		}while(!succeed && retryCount-- > 0);
		return response;
	}
	
	private HttpResponse doPost(String url, HttpEntity entity) throws IOException, ClientProtocolException {
		HttpPost method = new HttpPost(url);
		
		HttpClient http = getSuHttpClient();
		http.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		buildRequestHeader(method);
		if (null != mPostHeaderContentType) {
			method.addHeader("Content-Type", mPostHeaderContentType);
		}
		dumpRequestHeader(method, url);
		method.setEntity(entity);
		HttpResponse response = http.execute(method);
		if(response != null){
			dumpResponseHeader(response, url);
		}
		return response;
	}
	
	/**
	 * 检测是否是gzip压缩数据
	 * @param response
	 * @return
	 */
	private boolean isGzipContentEnc(HttpResponse response) {
		String contentEnc = null;
		Header header = response.getEntity().getContentEncoding();
		if (header != null) {
			contentEnc = header.getValue();
		}
		if (contentEnc != null && contentEnc.toLowerCase().indexOf("gzip") >= 0) {
			return true;
		}
		return false;
	}
	
	private void dumpResponseHeader(HttpResponse response, String url) {
		if (Log.flag) {
			Log.e(LOGTAG, "request url: " + url);
			Log.e(LOGTAG, "got response  (status line): " + response.getStatusLine());
			Header[] headers = response.getAllHeaders();
			Log.i(LOGTAG, "charset:" + EntityUtils.getContentCharSet(response.getEntity()));
			for (int i = 0; i < headers.length; i++) {
				Header header = headers[i];
				Log.i(LOGTAG, "response header " + header.getName() + " -> " + header.getValue());
			}
		}
	}
	
	private void dumpRequestHeader(HttpRequestBase request, String url) {
		if (Log.flag) {
			Log.e(LOGTAG, "request url: " + url);
			Header[] headers = request.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				Header header = headers[i];
				Log.i(LOGTAG, "request header " + header.getName() + " -> " + header.getValue());
			}
		}
	}
	
		
	/**
	 * 解压gzip格式数据
	 * @param in
	 * @return
	 */
	public static final byte[] unzipData(byte[] in) {
		ByteArrayInputStream bais = null;
		GZIPInputStream gis = null;
		ByteArrayOutputStream baos = null;
		byte[] buffer = new byte[1024];
		int loop = 0;
		try {
			bais = new ByteArrayInputStream(in);
			gis = new GZIPInputStream(bais);
			baos = new ByteArrayOutputStream();

			int read = 0;

			while ((read = gis.read(buffer)) != -1) {
				loop++;
				baos.write(buffer, 0, read);
				baos.flush();
			}
				
		}// of try
		catch (Exception ioe) {
			buffer = null;
			ioe.printStackTrace();
			baos = null;

		} finally {
			try {
				if (gis != null)
					gis.close();
				if (bais != null)
					bais.close();
				if (baos != null)
					baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	   
		return baos.toByteArray();
	}
			
	public void post(byte[] data,String mimeType) {
		
	}
	
	public void upload(String fileName,String url,String mimeType) {
		
	}
	
	
}
