package WordCount;

import static java.lang.System.out;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA.portable.OutputStream;

public class Main {

	static int charNum = 0,lineNum = 0,wordNum = 0;
	
	static boolean charFlag = false, lineFlag = false, wordFlag = false, outFlag=false, recurFlag = false;
	
	static String outFile = null, filePath = null;
	
	//重置参数
	static void resetStatus() {
		charNum = lineNum = wordNum = 0;
		charFlag = lineFlag = wordFlag = outFlag = recurFlag = false;
		outFile = filePath = null;
	}
	
	//读取路径，获取文件内容函数
	static String getContext(String filePath) {
		Path path = null;
		try {
			path = Paths.get(filePath);//获取路径，文本转成路径
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(1);
		}
		
		StringBuilder sb = new StringBuilder();
		
		try(InputStream in = Files.newInputStream(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
		 {
			int c;
			while((c = reader.read()) != -1) {
				sb.append((char)c);
			}
			String context = sb.toString();
			return context;
		}
		catch(IOException x) {
			System.err.println(x);
			return null;
		}
	}
	
	//计数函数
	static boolean count(String file) {
		String context;
		if((context = getContext(file)) == null) {
			System.err.println("读取文件错误 " + file);
			return false;
		}
		
		charNum = context.length();
		//?
		String[] lines = context.split("\r\n", -1);
		lineNum = lines.length;
		
		//消除空格
		String[] words = context.trim().split("[\\s,]+");
		wordNum = 0;
		//String 转换为 word
		for(String word: words) {
			if(!word.equals(""))
				++wordNum;
		}
		return true;
	}
	
	//写入文件函数
	static void writeInfo(String filePath,String info) {
		byte data[] = info.getBytes();
		Path path = Paths.get(filePath);
		
		//输出并创建文件
		try(BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(path,CREATE,TRUNCATE_EXISTING))){
			out.write(data,0,data.length);
		}
		catch (IOException x) {
			System.err.println(x);
		}
	}
	
	//获取输入函数
	static void argsInfo(String[] args) {
		String argErr = new String();
		
		for(int i = 0; i < args.length; ++ i) {
			switch(args[i]) {
			case "-c": charFlag = true;break;
			case "-w": wordFlag = true;break;
			case "-l": lineFlag = true;break;
			case "-o": 
				if(outFlag == true) {
					argErr += "您已经使用过参数“-o”\r\n";
					break;
				}
				//?
				if(i == args.length - 1) {
					argErr += "Miss the output file\r\n";
					break;
				}
				if(!args[i + 1].endsWith(".txt")) {
					argErr += "无效的输出文件名，需以“.txt”为后缀\r\n";
					break;
				}
				outFlag = true;
				outFile = args[i + 1];
				//???
				++i;//跳过下一个参数
				break;
			//???
			default:
				if(filePath != null) {
					argErr += "参数错误！\r\n";
					break;	
				}
				filePath = args[i];
				break;
			}
		}
		
		if(!charFlag && !wordFlag && !lineFlag)
			argErr += "请使用 -c, -w, -l, -a -x\r\n";
		
		if(!argErr.isEmpty())
		{
			System.err.println(argErr);
			System.exit(1);
		}
		return;
	}
	
	// Get file path recursively which matched the glob wildcard,
	// Find class's method is called
	static ArrayList<String> getRecurFiles()
	{
		ArrayList<String> fileLst = new ArrayList<String>();
		try
		{
			fileLst = Find.getFileNames(filePath);
		}
		catch (IOException e) 
		{
			System.err.println(e);
		}
		if(fileLst.isEmpty())
		{
			System.err.println("No such file as "+ filePath);
			System.exit(1);
		}
		return fileLst;
	}
	
	// The function that contains the main operations and generates the complete output, 
	// both GUI and console call this function 
	static String genOutput()
	{
		String output = new String();
		StringBuilder osb = new StringBuilder();
		
		ArrayList<String> fileLst = new ArrayList<String>();
		
		if(recurFlag == true)
			fileLst = getRecurFiles();
		else
			fileLst.add(filePath);
		
		for(String fileName: fileLst)
		{
			if(!count(fileName))
				System.exit(1);
			osb.append(constrOutput(fileName));
		}
		output = osb.toString();
		
		return output;
	}
	
	
	//构造输出信息
	static String constrOutput(String file) {
		StringBuilder output = new StringBuilder();
		if(charFlag == true)
			output.append(file + ", 字符数： " + charNum + "\r\n");
		if(wordFlag == true)
			output.append(file + ", 单词数： " + wordNum + "\r\n");
		if(lineFlag == true)
			output.append(file + ", 行数： " + lineNum + "\r\n");
		return output.toString();
	}
	
	static String[] initTest() {
		//Use case 1: use various characters to test -c argument
//		String[] args = {"-c", "testcase/test1.txt"};
//		String[] args = {"-w", "testcase/test2.txt"};
//		String[] args = {"-l", "testcase/test3.txt"};
		String[] args = {"-c", "-w", "-l", "testcase/test4.txt", "-o", "testcase/result.txt"};
		return args;
	}
	
	public static void main(String[] args) {
		args = initTest();

		argsInfo(args);
		
		String context = getContext(filePath);
		String output = genOutput();
		
		if(outFlag == true)
		{
			writeInfo(outFile, output);
			out.println("The information has been written to " + outFile + "\r\n");
		}
		out.println("您输入的是：");
		
		StringBuilder input = new StringBuilder();
		
		for(int i=0;i<args.length;++i)
			input.append(args[i] + " ");
		out.println(input + "\r\n");
		out.println("检测结果：\r\n" + output);
		out.println("文本内容：\r\n" + context);
	}
	
}
