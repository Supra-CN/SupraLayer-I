package cn.supra.supralayer_i.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public final class FileUtils {

	/**
	 * 删除一个目录（可以是非空目录）
	 * 
	 * @param dir
	 */
	public static boolean deleteDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile()) {
			return false;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				deleteDir(file);// 递归
			}
		}
		return dir.delete();
	}

	public static boolean fileExists(String filename) {
		boolean flag=false;
		if(filename!=null) {
			File file = new File(filename);
			flag=file.exists();
		}
		return flag;
	}
	
	
	public static boolean fileRename(String src,String dest) {
		File file=new File(src);
		File destFile=new File(dest);
		File destDir=destFile.getParentFile();
		if(!destDir.exists()) {
			destDir.mkdirs();
		}
		return file.renameTo(destFile);
	}
	/**
	 * 去除linux中的非法文件名，请勿包含路径名称
	 * @param badFileName 包含非法字符的文件名
	 * @return 去除掉非法字符的文件名
	 * @author from stackOverflow
	 */
	public static String cleanFileName(String badFileName) {
		final int[] illegalChars = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 34, 42, 47, 58, 60, 62, 63, 92, 124};
	    StringBuilder cleanName = new StringBuilder();
	    for (int i = 0; i < badFileName.length(); i++) {
	        int c = (int)badFileName.charAt(i);
	        if (Arrays.binarySearch(illegalChars, c) < 0) {
	            cleanName.append((char)c);
	        }
	    }
	    return cleanName.toString();
	}

	/**
	 * 
	 * @param content
	 * @param filename
	 * @return 返回false 保存文件失败 返回ture 保存文件成功
	 */

	public static boolean saveFile(String content, String filename) {
		boolean flag = true;
		File f = new File(filename);
		if (f.exists()) {
			f.delete();
		}

		try {
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			flag = false;
		}
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(f))));
			pw.write(content);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = false;
		}

		return flag;
	}
	
	public static boolean saveFile(byte[] data, String filename) {
		boolean flag = true;
		File f = new File(filename);
		if (f.exists()) {
			f.delete();
		}

		try {
			f.mkdirs();
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			flag = false;
		}
		try {
			FileOutputStream out = new FileOutputStream(
					filename);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}
	
	public static boolean saveFile(InputStream in, String filename) {
		boolean flag = true;
		File f = new File(filename);
		if (f.exists()) {
			f.delete();
		}

		try {
			f.mkdirs();
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			flag = false;
		}
		try {
			FileOutputStream out = new FileOutputStream(
					filename);
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = in.read(buf)) != -1){
				out.write(buf, 0, len);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}
	
	/**
	 * 如果文件不存在，则创建新文件
	 * @param path
	 * @return 如果文件已存在或成功创建返回true,否则返回false
	 */
	public static boolean createFileIfNotExist(String path){
		boolean flag = true;
		if(path != null){
			File file = new File(path);
			try {
				if(!file.exists()){
					if(file.getParentFile()!=null){
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
				}else{
					
				}
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
			
		}else{
			flag = false;
		}
		return flag;
	}

	public static String readFileToString(String fileName) {
		File file = new File(fileName);
		return readFileToString(file);

	}

	public static String readFileToString(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuffer buff = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buff.append(line);
			}
			return buff.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}



    /**
     * 得到目录的大小，单位为KB    
     * @param file
     * @return
     */
 	public static long getDirSize(File file) { 
 		 long size = 0;     
         //判断文件是否存在     
         if (file.exists()) {     
             //如果是目录则递归计算其内容的总大小    
             if (file.isDirectory()) {     
                 File[] children = file.listFiles();     
                 for (File f : children)     
                     size += getDirSize(f);     
                 return size;     
             } else {//如果是文件则直接返回其大小,以“兆”为单位   
                 size = (long) file.length();        
                 return size;     
             }     
         } else {     
             return 0;     
         }     
     }
 	
 	
 	public static boolean createTmpFile(String strFileName,int nFileSize) {
		ArrayList<String> vec = new ArrayList<String>();
		vec.add("test1");
		vec.add("test2");
		vec.add("test3");
		vec.add("test4");
		vec.add("test5");
		vec.add("test6");
		vec.add("test7");
		vec.add("test8");

		StringBuffer stringBuilder = new StringBuffer();

		Random random = new Random();
		try {
			FileOutputStream file = new FileOutputStream(strFileName);

			while (true) {
				int n = random.nextInt(vec.size()) % vec.size();

				stringBuilder.append(vec.get(n).getBytes());

				if (stringBuilder.length() > nFileSize)
					break;
			}

			file.write(stringBuilder.toString().getBytes(), 0, nFileSize);
			file.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

 	}
 	
 	public  static String createTmpData(int nBufferSize) {
		ArrayList<String> vec = new ArrayList<String>();
		vec.add("test1");
		vec.add("test2");
		vec.add("test3");
		vec.add("test4");
		vec.add("test5");
		vec.add("test6");
		vec.add("test7");
		vec.add("test8");
		
		byte[] buffer=new byte[nBufferSize];

		Random random = new Random();
		int leftSize = nBufferSize;
		int nCurrPos = 0;

		while(leftSize > 0)
		{
			int n = random.nextInt(vec.size()) % vec.size();

			int nWSize = vec.get(n).length() > leftSize ? leftSize : vec.get(n).length();
			
			System.arraycopy(vec.get(n).getBytes(), 0, buffer, nCurrPos, nWSize);

			leftSize -= nWSize;
			nCurrPos += nWSize;
		}
		
		return new String(buffer);
 	}

}
