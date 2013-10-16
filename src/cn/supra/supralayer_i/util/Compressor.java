package cn.supra.supralayer_i.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件
 * @author lijun
 */
public class Compressor {
	
	/**
	 * 压缩文件
	 * @param zipFileName 保存的压缩包文件路径
	 * @param inputFile   需要压缩的文件夹或文件路径
	 * @throws Exception
	 */
	public static void zip(String zipFileName, String inputFile) throws Exception {
	   zip(zipFileName, new File(inputFile));
	}

	private static void zip(String zipFileName, File inputFile) throws Exception {
	   ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
	   //递归压缩方法
	   zip(out, inputFile, "");
	   out.close();
	}
	
	/**
	 * 递归压缩方法
	 * @param out   压缩包输出流
	 * @param f     需要压缩的文件
	 * @param base 压缩的路径
	 * @throws Exception
	 */
	private static void zip(ZipOutputStream out, File f, String base) throws Exception {
	   if (f.isDirectory()) {   // 如果是文件夹，则获取下面的所有文件
		    File[] fl = f.listFiles();
		    out.putNextEntry(new ZipEntry(base + "/"));
		    base = base.length() == 0 ? "" : base + "/";
		    for (int i = 0; i < fl.length; i++) {
		     zip(out, fl[i], base + fl[i].getName());
		    }
	   } else {   // 如果是文件，则压缩
		    out.putNextEntry(new ZipEntry(base));          // 生成下一个压缩节点
		    FileInputStream in = new FileInputStream(f);   // 读取文件内容
		    int b;
		    while ((b = in.read()) != -1) {
		    	out.write(b);   // 写入到压缩包
		    }
		    in.close();
	   }
	}
}
