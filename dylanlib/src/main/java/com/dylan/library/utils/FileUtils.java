package com.dylan.library.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {
	public static boolean copyFile(String srcFilePath,String desFilePath){
		boolean success=false;
		try {
			FileInputStream fis=new FileInputStream(srcFilePath);
			ByteArrayOutputStream  bos=new ByteArrayOutputStream();
			int len=0;
			byte[] bytes=new byte[1024];
			while((len=fis.read(bytes))!=-1){
				bos.write(bytes, 0, len);
			}
			FileOutputStream fos=new FileOutputStream(desFilePath);
			fos.write(bos.toByteArray());
			bos.close();
			fos.close();
			fis.close();
			success=true;
			System.out.println(desFilePath+" �����ɹ�����");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	
	
	 public static void modifySuffixName(String filePath, String oldSuffix, String newSuffix) {
		File file = new File(filePath);
		String desFilePath = filePath.replace(oldSuffix, newSuffix);
		file.renameTo(new File(desFilePath));
		System.out.println(filePath + "重命名成功！！");
	}
	 
	 
	 public static String readFile(String filePath) {
			File file = new File(filePath);
			String result = "";
			try {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] bytes = new byte[1024];
				int len;
				while ((len = fis.read(bytes)) != -1) {
					bos.write(bytes, 0, len);
				}
				bos.close();
				fis.close();
				result = bos.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
}
