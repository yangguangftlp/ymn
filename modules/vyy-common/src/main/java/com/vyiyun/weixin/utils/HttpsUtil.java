package com.vyiyun.weixin.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.MessageFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.exception.VyiyunException;

/**
 * @ClassName: HttpsUtil
 * @Description: HTTPS请求辅助类
 * @author CCLIU
 * @date 2015-6-24 下午5:42:18 v1.0
 */
public class HttpsUtil {
	private static final Logger LOGGER = Logger.getLogger(HttpsUtil.class);
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
	private static final int DEFAULT_READ_TIMEOUT = 10000;

	public static void main(String[] args) {
		try {
			JSONObject jo = post("https://qyapi.weixin.qq.com/cgi-bin/gettoken", "corpid=wx7d7e4794f9070be4"
					+ "&corpsecret=kfjz6G5qj0CaCE48ylCU-fyQFg1SG4mNTUSSZsd4kGphWw28V6Qx4wjYpYzgfLR-");
			System.out.print(jo.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject get(String url, String params) throws Exception {
		return JSON.parseObject(sendRequest(url, params, METHOD_GET, DEFAULT_CHARSET, DEFAULT_CONNECT_TIMEOUT,
				DEFAULT_READ_TIMEOUT));
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject get(String url) throws Exception {
		return JSON.parseObject(sendRequest(url, null, METHOD_GET, DEFAULT_CHARSET, DEFAULT_CONNECT_TIMEOUT,
				DEFAULT_READ_TIMEOUT));
	}

	/**
	 * 获取媒体资源
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static JSONObject downMediaResources(String url, String params) throws Exception {
		String ctype = "application/json;charset=" + DEFAULT_CHARSET;
		byte[] content = {};
		if (params != null) {
			content = params.getBytes(DEFAULT_CHARSET);
		}
		HttpsURLConnection conn = null;
		OutputStream out = null;
		JSONObject jsonObject = new JSONObject();
		try {
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
				SSLContext.setDefault(ctx);
				conn = getConnection(new URL(url), METHOD_GET, ctype);
				conn.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
				conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
				out = conn.getOutputStream();
				out.write(content);
				jsonObject.put("Content-Length", conn.getHeaderField("Content-Length"));
				jsonObject.put("Content-Type", conn.getHeaderField("Content-Type"));
				String contentDisposition = conn.getHeaderField("Content-disposition");
				if (StringUtils.isNotEmpty(contentDisposition)) {
					for (String key : contentDisposition.split(";")) {
						if (key.contains("filename")) {
							jsonObject.put("filename", key.split("=")[1].replace("\"", ""));
						}
					}
				}
				if (jsonObject.getString("Content-Type").contains("image")) {
					jsonObject.put("buffer", getResponseAsByte(conn));
				} else {
					jsonObject.putAll(JSON.parseObject(getResponseAsString(conn)));
				}
			} catch (IOException e) {
				throw new VyiyunException("获取响应失败！", e);
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return jsonObject;
	}

	private static byte[] getResponseAsByte(HttpsURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsByte(conn.getInputStream());
		} else {
			String msg = getStreamAsString(es, charset);
			if (msg == null || "".equals(msg)) {
				throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static byte[] getStreamAsByte(InputStream inputStream) throws IOException {
		try {
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			byte[] chars = new byte[4 * 256];
			int count = 0;
			while ((count = inputStream.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}
			return writer.toByteArray();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject post(String url, String params) throws Exception {
		return JSON.parseObject(sendRequest(url, params, METHOD_POST, DEFAULT_CHARSET, DEFAULT_CONNECT_TIMEOUT,
				DEFAULT_READ_TIMEOUT));
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject get(String url, JSONObject params) throws Exception {
		return JSON.parseObject(sendRequest(url, params.toJSONString(), METHOD_GET, DEFAULT_CHARSET,
				DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT));
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject post(String url, JSONObject params) throws Exception {
		return JSON.parseObject(sendRequest(url, params.toJSONString(), METHOD_POST, DEFAULT_CHARSET,
				DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT));
	}

	/**
	 * 发送请求
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param charset
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws Exception
	 */
	public static String sendRequest(String url, String params, String method, String charset, int connectTimeout,
			int readTimeout) throws Exception {
		LOGGER.debug(MessageFormat.format(
				"发送请求参数url={0},params={1},method={2},charset={3},connectTimeout={4},readTimeout={5}", new Object[] {
						url, params, method, charset, connectTimeout }));
		String ctype = "application/json;charset=" + charset;
		byte[] content = {};
		if (params != null) {
			content = params.getBytes(charset);
		}

		HttpsURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
				SSLContext.setDefault(ctx);

				conn = getConnection(new URL(url), method, ctype);
				conn.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (Exception e) {
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static HttpsURLConnection getConnection(URL url, String method, String ctype) throws IOException {
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "stargate");
		conn.setRequestProperty("Content-Type", ctype);
		return conn;
	}

	private static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		} else {
			String msg = getStreamAsString(es, charset);
			if (msg == null || "".equals(msg)) {
				throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;
		if (ctype != null && !"".equals(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (pair[1] != null && !"".equals(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	/**
	 * 向指定 URL 发送GET方法的请求
	 * 
	 * @param url
	 *            ，发送请求的 URL
	 * @param param
	 *            ，请求参数，格式：name1=value1&name2=value2
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		LOGGER.debug(MessageFormat.format("发送请求参数url={0},params={1}", new Object[] { url, param }));
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("Content-Type", "utf-8");
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public static String sendGet(String url) {
		LOGGER.debug(MessageFormat.format("发送请求参数url={0}", new Object[] { url }));
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("Content-Type", "utf-8");
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            ，发送请求的 URL
	 * @param param
	 *            ，请求参数，格式：name1=value1&name2=value2
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		LOGGER.debug(MessageFormat.format("发送请求参数url={0},params={1}", new Object[] { url, param }));
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
