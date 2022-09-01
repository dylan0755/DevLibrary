package com.dylan.library.io;

import com.dylan.library.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

public class TextFileReader {

	// 输出本行内容及字符数
	public	static void readLineVarFile(String fileName, int lineNumber) throws Exception {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName))); // 使用缓冲区的方法将数据读入到缓冲区中
			String line = reader.readLine(); // 定义行数
			if (lineNumber <= 0 || lineNumber > getTotalLines(fileName)){ // 确定输入的行数是否有内容
				throw new Exception("不在文件的行数范围之内。");
			}
			int num = 1;
			while (line != null){ // 当行数不为空时，输出该行内容及字符数
				if (lineNumber == num) {
					System.out.println("第" + lineNumber + "行: " + line + "     字符数为：" + line.length());
				}
				line = reader.readLine();
				num++;
			}
			reader.close();
	}
	
	
	public static List<String>  readFileStartFromLine(String fileName, int startLine,List<String> list) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName))); // 使用缓冲区的方法将数据读入到缓冲区中
		String line = reader.readLine(); // 定义行数
		int totalLine=getTotalLines(fileName);
		if (startLine <= 0 || startLine >totalLine ){ // 确定输入的行数是否有内容
			throw new Exception("不在文件的行数范围之内。总行数："+totalLine+" 指定行数："+startLine);
		}
		int num =0;
		while (line != null){ // 当行数不为空时，输出该行内容及字符数
			if (num>=startLine) {
				if (!StringUtils.isEmpty(line)&&StringUtils.isNotBlank(line)) {
					list.add(line);
				}
			}
			num++;
			line = reader.readLine();
		}
		reader.close();
		return list;
}
	
	
	
	
	 
		// 文件内容的总行数
	public	static int getTotalLines(String fileName) throws IOException {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName))); //// 使用缓冲区的方法将数据读入到缓冲区中
			LineNumberReader reader = new LineNumberReader(br);
			String s = reader.readLine(); // 定义行数
			int lines = 0;
			while (s != null){ // 确定行数
				lines++;
				s = reader.readLine();
			}
			reader.close();
			br.close();
			return lines; // 返回行数
	}

}
