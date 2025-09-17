package com.paradisecloud.fcm.terminal.fs.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import com.paradisecloud.fcm.terminal.fs.constant.FcmConfig;


public abstract class FileConvert 
{

	private static final FileConvert INSTANCE = new FileConvert() 
	{

	};

	public static FileConvert getInstance() 
	{
		return INSTANCE;
	}

	/**
	 * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
	 * 
	 * @param res      原字符串
	 * @param filePath 文件路径
	 * @return 成功标记
	 */
	public boolean string2File(String res, String filePath, String fileName) 
	{
		boolean flag = true;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try 
		{
			File distFile = new File(filePath, fileName);
			if (!distFile.getParentFile().exists()) 
			{
				distFile.getParentFile().mkdirs();
			}

			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));

			// 字符缓冲区
			char buf[] = new char[1024];
			int len;
			while ((len = bufferedReader.read(buf)) != -1) 
			{
				bufferedWriter.write(buf, 0, len);
			}
			
			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();

		}
		catch (IOException e) 
		{
			e.printStackTrace();
			flag = false;
			return flag;
		} 
		finally 
		{
			if (bufferedReader != null) 
			{
				try 
				{
					bufferedReader.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 文本文件转换为指定编码的字符串
	 * 
	 * @param file     文本文件
	 * @param encoding 编码类型
	 * @return 转换后的字符串
	 * @throws IOException
	 */
	public String file2String(File file, String encoding) 
	{
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try 
		{
			if (encoding == null || "".equals(encoding.trim())) 
			{
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			} 
			else 
			{
				reader = new InputStreamReader(new FileInputStream(file));
			}

			// 将输入流写入输出流
			char[] buffer = new char[1024];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) 
			{
				writer.write(buffer, 0, n);
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		} 
		finally 
		{
			if (reader != null)
				try 
			    {
					reader.close();
				} 
				catch (IOException e) 
			    {
					e.printStackTrace();
				}
		}

		return writer.toString();
	}

	public void deleteAllFiles(File root) 
	{
        File[] files = root.listFiles();
        if (files != null) 
        {
            for (File file : files) 
            {
                // 如果是目录，递归删除
                if (file.isDirectory()) 
                {
                    deleteAllFiles(file);
                    deleteFile(file);
                } 
                else 
                {
                    if (file.exists()) 
                    {
                        deleteFile(file);
                    }
                }
            }
        }

    }
	
	public File[] getAllFile(String path) {
		File file = new File(path);
		File[] listFiles = file.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String fileName = name.toLowerCase();
				String[] filePrefix = fileName.split("\\" + FcmConfig.DOTTED);
				String regex="^[1-9]+[0-9]*$";
				if(filePrefix.length > 1) {
					if(filePrefix[0].matches(regex)) {
						return true;
					}
				}
				return false;
			}
		});
		return listFiles;
	}


    public void deleteFile (File file) {
        try 
        {
            file.delete();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
		
}
