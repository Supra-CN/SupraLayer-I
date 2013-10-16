package cn.supra.supralayer_i.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

public class HttpUtils {
	
	/**
	 * 解析出URL参数对
	 * @param query
	 * @param table
	 */
	public static void extractQuery(String query, Dictionary<String, String> table) {
		try {
			if (query == null) {
				return;
			}
			query = query.replace("+", " ");
			StringTokenizer st = new StringTokenizer(query, "&");
			while (st.hasMoreTokens()) {
				String field = st.nextToken();
				int index = field.indexOf("=");
				if (index < 0) {
					 table.put(URLDecoder.decode(field), "");
				} else {
					 table.put(URLDecoder.decode(field.substring(0, index)),
							 URLDecoder.decode(field.substring(index + 1)));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * 解析出URL参数对
	 * @param query
	 * @param table
	 */
	public static void extractQuery(String query, HashMap<String, String> table) {
		try {
			if (query == null) {
				return;
			}
			query = query.replace("+", " ");
			StringTokenizer st = new StringTokenizer(query, "&");
			while (st.hasMoreTokens()) {
				String field = st.nextToken();
				int index = field.indexOf("=");
				if (index < 0) {
					 table.put(URLDecoder.decode(field), "");
				} else {
					 table.put(URLDecoder.decode(field.substring(0, index)),
							 URLDecoder.decode(field.substring(index + 1)));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 打开一个url并另存为文件,如果指定的目录不存在则自动创建
	 * 注:本方法不适合大文件的下载
	 * @param url
	 * @param saveAs 可以是一个单独的文件名或则文件名加目录的形式(但必须有写入权限)
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	public static boolean downloadFile(String url, final String saveAs, final String referer)  {
//		boolean success = false;
//		try {
//			SuHttpClient httpClient = new SuHttpClient();
//			if (null != referer) {
//				httpClient.setReferrer(referer);
//			}
//			url = Utils.rewriteUrl(url);
//			byte[] body = httpClient.getBody(url,SuHttpClient.RETRY_COUNT);
//			if(SuBrowserProperties.enableLogFlag) {
//				if(url.startsWith(SuReaderDefine.CHANNEL_URL.substring(0,20))) {
//					Log.e("downloadFile","url="+url+",size="+body.length/1024+","+body.length/1024/1024);
//					SuReaderHelper.mNewsPicBytes+=body.length;
//				}
//			}
//			if(body != null){
//				String path = null;
//				String fileName = saveAs;
//				File file = null;
//				
//				if(saveAs.indexOf("/") >= 0) {
//					path = StringUtils.extractDirectoryPath(saveAs);
//					fileName = StringUtils.extractFileName(saveAs);
//					
//					if(!path.equals("")) {
//						File dir = new File(path);
//						if(!dir.exists()) {
//							dir.mkdirs();
//						}
//						file = new File(dir, fileName);
//						
//					} else {
//						file = new File(fileName);
//					}
//				} else {
//					file = new File(fileName);
//				}
//				
//				FileOutputStream output = new FileOutputStream(file);  
//				output.write(body);
//				output.close();
//				success = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return success;
		return false;
	}
	
	/**
	 * 打开一个url并另存为文件,如果指定的目录不存在则自动创建
	 * 注:本方法适合大文件的下载
	 * @param url
	 * @param saveAs 可以是一个单独的文件名或则文件名加目录的形式(但必须有写入权限)
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	public static boolean downloadBigFile(String url, final String saveAs, final String referer)  {
		boolean success = false;
		try {
			SuHttpClient httpClient = new SuHttpClient();
			if (null != referer) {
				httpClient.setReferrer(referer);
			}
			url = Utils.rewriteUrl(url);
			HttpResponse response = httpClient.get(url);
			if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				String path = null;
				String fileName = saveAs;
				File file = null;
				
				if(saveAs.indexOf("/") >= 0) {
					path = StringUtils.extractDirectoryPath(saveAs);
					fileName = StringUtils.extractFileName(saveAs);
					
					if(!path.equals("")) {
						File dir = new File(path);
						if(!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, fileName);
						
					} else {
						file = new File(fileName);
					}
				} else {
					file = new File(fileName);
				}
				
				InputStream in = response.getEntity().getContent();
				byte[] buf = new byte[1024];
				int len = 0;
				FileOutputStream output = new FileOutputStream(file);  
				while((len=in.read(buf)) != -1){
					output.write(buf, 0, len);
				}
				in.close();
				output.close();
				success = true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public static boolean downloadFile(String url, final String saveAs)  {
		return downloadFile(url, saveAs, null);
	}
	
	public static String getBodyString(String url) {
		String body = null;
		try {
			body = new SuHttpClient().getBodyString(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	
	public static String postString(String url,String content){
		String body = null;
		try {
			SuHttpClient client = new SuHttpClient();
			HttpResponse res = client.post(url,content);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				body = client.extractBodyString(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	
	public static String postParameters(String url,ArrayList<NameValuePair> params){
		String body = null;
		try {
			SuHttpClient client = new SuHttpClient();
//			client.setPostContentType("application/json; charset=utf-8");
			HttpResponse res = client.post(url,params);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				body = client.extractBodyString(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	
	/*
	 * 判断是否是手机页面
	 */
	public static boolean isMobilePage(String host) {
		String[] s = {
				"3g\\.163\\.com",
	            "book.*\\.sina\\.cn",
	            ".+\\.sina\\.cn",
	            "ebook.*\\.3g\\.qq\\.com",
	            ".+\\.3g\\.qq\\.com",
	            "wap\\.book\\.ifeng\\.com",
	            "i\\.ifeng\\.com",
	            ".+\\.3g\\.cn",
	            "read\\.3gycw\\.com",
	            "wap\\.sohu\\.com",
	            "m\\.sohu\\.com",
	            ".+\\.hongxiu\\.com",
	            "wap\\.people\\.com\\.cn",
	            "(3g\\.xinhuanet\\.com|m\\.news\\.cn)",
	            "m\\.tiexue\\.net",
	            "m\\.huanqiu\\.com",
	            "qidian\\.cn",
	            "wap\\.tadu\\.com",
	            "(wap\\.jjwxc\\.com|m\\.jjwxc\\.com)",
	            "m\\.yahoo\\.com",
	            "www\\.businessinsider\\.com",
	            "(mobile\\.washingtonpost\\.com|m\\.washingtonpost\\.com)",
	            "edition\\.cnn\\.com",
	            "marquee\\.blogs\\.cnn\\.com",
	            ".+\\.foxnews\\.mobi",
	            "www\\.huffingtonpost\\.com",
	            "m\\.bbc\\.co\\.uk",
	            "www\\.bbc\\.co\\.uk",
	            "m\\.techcrunch\\.com",
	            "gigaom\\.com",
	            "m\\.engadget\\.com",
	            "m\\.espn\\.go\\.com",
	            "www\\.macrumors\\.com",
	            "mobile\\.nytimes\\.com"
		};
		for(int i=0; i<s.length; i++) {
			if(host.matches(s[i])) {
				Log.v("jinwei", "host:"+host+" success");
				return true;
			}
		}
		Log.v("jinwei", "host:"+host+" fail");
		return false;
	}
	
	
}
