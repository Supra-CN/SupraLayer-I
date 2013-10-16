package cn.supra.supralayer_i.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;

public class Utils {
	private static final String LOGTAG = "Utils";
	
	//用于LogStart() ,LogEnd() 查看时间花销
	private static final String TAG_COSTTIME = "costtime";
	
	public static int NETWORK_STATE_NOT_CONNECTED = 0;
	public static int NETWORK_STATE_CONNECT_WITH_WIF = 1;
	public static int NETWORK_STATE_CONNECT_WITH_MOBILE = 2;
	public static HashMap<String,Long> timers = new HashMap<String,Long>();
	private static final String TAG_COSTTIME_ = "cost_time";
	
	/**
	 * 记录系统时钟开始时间
	 * @see #logStart()
	 */
	private static long startLogTim = 0;
	/**
	 * 记录当前内存使用
	 * @see #logStartMem(Context)
	 */
	private static long curentMemory = 0;

	private Utils() {
	}

	public static void startLog(String methodName) {
		long startTime = System.currentTimeMillis();
		timers.put(methodName, startTime);
	}
	
	public static void endLog(String methodName) {
		long endTime = System.currentTimeMillis();
		long startTime = timers.get(methodName);
		long delta = endTime - startTime;
		Log.v(TAG_COSTTIME_, methodName + "$>>>>>>>>>>>>>>" + delta + " (ms)");
	}
	
	/**
	 * 用于记录函数或方法的执行时间,在函数或方法的开头调用
	 * 
	 */
	public static void logStart()
	{
		startLogTim = System.currentTimeMillis();
	}
	
	/**
	 * 记录函数或方法的执行时间,在方法结尾调用
	 * @param tag
	 */
	public static void logEnd(String tag)
	{
		long curTim = System.currentTimeMillis();
		long cost = curTim - startLogTim;
		StringBuffer msg = new StringBuffer("[" +  tag + "]>>>>>>>>>>>> total cost ms:");
		msg.append(cost);
		Log.v(TAG_COSTTIME, msg.toString());
	}

	/**
	 * 记录函数或方法的执行时间,在方法结尾调用
	 * 在一个方法中，出现多次调用时，用tag1来标记
	 */
	public static void logEnd(String tag,String tag1)
	{
		long curTim = System.currentTimeMillis();
		long cost = curTim - startLogTim;
		StringBuffer msg = new StringBuffer("[" +  tag + "]>>>>>>>>>>>> total cost ms:");
		msg.insert(0, tag1);
		msg.append(cost);
		Log.v(TAG_COSTTIME, msg.toString());
	}


	/**
	 * return a guid as byte array. if param <code>guid</code> is null, then generate a new guid and return it's byte array.
	 * @param guid: 
	 * @return
	 */
	public static byte[] getGUIDAsByteArray(String guid){
		UUID uid = null;
		if(null == guid){
			uid = UUID.randomUUID();
		}else{
			uid = UUID.fromString(guid);
		}
		
		byte[] b = new byte[16];
		long msb = uid.getMostSignificantBits();
		long lsb = uid.getLeastSignificantBits();
		for(int i = 0; i < 8; ++i){
			b[i] = (byte)(msb >>> 8 * (7 - i));
		}
		for(int i = 8; i < 16; ++i){
			b[i] = (byte)(lsb >>> 8 * (7 - i));
		}
		
		return b;
	}
	
	/**
	 * sqlite中， sql语句中的单引号或者双引号需要转义, 比如： 双引号： select * from table where
	 * col="string1""string2"; //查询col的值是string1"string2 select * from table
	 * where col="string1'string2"; //查询col的值是string1'string2 单引号： select * from
	 * table where col='string1''string2'; //查询col的值是string1'string2 select *
	 * from table where col='string1"string2'; //查询col的值是string1"string2
	 * 
	 * 因此为了避免引号转义带来的问题， 建议使用sql时统一把字段值放到单引号中
	 * 
	 * @param text
	 * @return
	 */
	public static String sqliteEscape(String text) {

		if (text != null && text.indexOf("'") >= 0) {
			return text.replaceAll("'", "''"); // sqlite中， sql语句中的单引号'需要转义成俩''
		}
		return text;
	}
	
	
	
	public static int log2(int value) {
		return (int) (Math.log(value) / Math.log(2));
	}
	
	
	
	public static void unzip(String strZipFile) {

		try {
			/*
			 * STEP 1 : Create directory with the name of the zip file
			 * 
			 * For e.g. if we are going to extract c:/demo.zip create c:/demo
			 * directory where we can extract all the zip entries
			 */
			File fSourceZip = new File(strZipFile);
			String zipPath = strZipFile.substring(0, strZipFile.length() - 4);
			File temp = new File(zipPath);
			temp.mkdir();
			System.out.println(zipPath + " created");

			/*
			 * STEP 2 : Extract entries while creating required sub-directories
			 */
			ZipFile zipFile = new ZipFile(fSourceZip);
			Enumeration e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				File destinationFilePath = new File(zipPath, entry.getName());

				// create directories if required.
				destinationFilePath.getParentFile().mkdirs();

				// if the entry is directory, leave it. Otherwise extract it.
				if (entry.isDirectory()) {
					continue;
				} else {
					System.out.println("Extracting " + destinationFilePath);

					/*
					 * Get the InputStream for current entry of the zip file
					 * using
					 * 
					 * InputStream getInputStream(Entry entry) method.
					 */
					BufferedInputStream bis = new BufferedInputStream(
							zipFile.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					/*
					 * read the current entry from the zip file, extract it and
					 * write the extracted file.
					 */
					FileOutputStream fos = new FileOutputStream(
							destinationFilePath);
					BufferedOutputStream bos = new BufferedOutputStream(fos,
							1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					// flush the output stream and close it.
					bos.flush();
					bos.close();

					// close the input stream.
					bis.close();
				}
			}
		} catch (IOException ioe) {
			System.out.println("IOError :" + ioe);
		}

	}
	
	    
	public static String getLocalIpAddress(boolean isIp4) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (isIp4) {
						if (!inetAddress.isLoopbackAddress()
								&& inetAddress.getAddress().length == 4 ) {
							return inetAddress.getHostAddress().toString();
						}
					} else {
						if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length!=4) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return "";
	}
    
    public static String getLocalIpAddress() {
    	String ip=getLocalIpAddress(true);
    	if(ip!=null&&ip.equals("")) {
    		ip=getLocalIpAddress(false);
    	}
    	return ip;
		
	}
    
    /**
     * 替换某些地址中出现的非法字符为,主要有'[',']','{','}'
     * @param url
     * @return
     */
    public static String rewriteUrl(String url) {
		if (null != url) {
			url = url.replace("[", "%5B");
			url = url.replace("]", "%5D");
			url = url.replace("{", "%7B");
			url = url.replace("}", "%7D");
		}
		return url;
	}
	
}
