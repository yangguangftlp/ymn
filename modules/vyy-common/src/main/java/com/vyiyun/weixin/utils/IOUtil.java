package com.vyiyun.weixin.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * IO工具
 */
public class IOUtil {

	/**
	 * 输入流转为输出流
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read = 0;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * 将InputStream转为ByteArrayInputStream
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static ByteArrayInputStream fromStream(InputStream stream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStream(stream, baos);
		baos.flush();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	/**
	 * 将InputStream转为String
	 * 
	 * @param in
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public static String getString(InputStream in, String code) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(in, code));
		// 最好在将字节流转换为字符流的时候 进行转码
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = bf.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	public static void close(OutputStream out) {
		try {
			if (null != out)
				out.close();
		} catch (Exception e) {
		}
	}

	public static void close(InputStream in) {
		try {
			if (null != in)
				in.close();
		} catch (Exception e) {
		}
	}
}