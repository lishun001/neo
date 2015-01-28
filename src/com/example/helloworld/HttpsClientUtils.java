package com.example.helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;


public class HttpsClientUtils {
	
	private static final String METHOD_POST = "POST";  
    private static final String DEFAULT_CHARSET = "utf-8";  
      
    public static String doPost(String url, String params, String charset, int connectTimeout, int readTimeout) throws Exception {  
        String ctype = "application/json;charset=" + charset;  
        byte[] content = {};  
        if(params != null){  
            content = params.getBytes(charset);  
        }  
        return doPost(url, ctype, content, connectTimeout, readTimeout);  
    }  
    public static String doPost(String url, String ctype, byte[] content,int connectTimeout,int readTimeout) throws Exception {  
        HttpsURLConnection conn = null;  
        OutputStream out = null;  
        String rsp = null;  
        try {  
            try{  
                SSLContext ctx = SSLContext.getInstance("TLS");  
                ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());  
                SSLContext.setDefault(ctx);  
  
                conn = getConnection(new URL(url), METHOD_POST, ctype);   
                conn.setHostnameVerifier(new HostnameVerifier() {  
                    @Override  
                    public boolean verify(String hostname, SSLSession session) {  
                        return true;  
                    }  
                });  
                conn.setConnectTimeout(connectTimeout);  
                conn.setReadTimeout(readTimeout);  
            }catch(Exception e){  
                Log.e("https","GET_CONNECTOIN_ERROR, URL = " + url, e);  
                throw e;  
            }  
            try{  
                out = conn.getOutputStream();  
                out.write(content);  
                rsp = getResponseAsString(conn);  
            }catch(IOException e){  
            	 Log.e("https","REQUEST_RESPONSE_ERROR, URL = " + url, e);  
                throw e;  
            }  
              
        }finally {  
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
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}  
  
    }  
      
    private static HttpsURLConnection getConnection(URL url, String method, String ctype)  
            throws IOException {  
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();  
        conn.setRequestMethod(method);  
        conn.setDoInput(true);  
        conn.setDoOutput(true);  
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");  
        conn.setRequestProperty("User-Agent", "stargate");  
        conn.setRequestProperty("Content-Type", ctype);  
        return conn;  
    }  
  
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {  
        String charset = getResponseCharset(conn.getContentType());  
        InputStream es = conn.getErrorStream();  
        if (es == null) {  
            return getStreamAsString(conn.getInputStream(), charset);  
        } else {  
            String msg = getStreamAsString(es, charset);  
            if (msg!=null && msg.isEmpty()) {  
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
        if (!(ctype!=null && ctype.isEmpty())) {  
            String[] params = ctype.split(";");  
            for (String param : params) {  
                param = param.trim();  
                if (param.startsWith("charset")) {  
                    String[] pair = param.split("=", 2);  
                    if (pair.length == 2) {  
                        if (!(pair[1]!=null && pair[1].isEmpty())) {  
                            charset = pair[1].trim();  
                        }  
                    }  
                    break;  
                }  
            }  
        }  
  
        return charset;  
    }  
    
    public static void main(String[] args) throws Exception {
//    	String params="<?xml version=\"1.0\" encoding=\"UTF-8\"?><req><license>22UZ-JDJE-587E-DFAB-DK43-38ZG</license></req>";
    	String charset=HttpsClientUtils.DEFAULT_CHARSET;
    	String str=HttpsClientUtils.doPost("https://license.dtri.com/WCMLicense/RegisterPlayer.do?key=xxxxx&mac=111111", null, charset, 5000, 0);
    	System.out.println(str);
    	
//    	Response response=Http.doGet("http://license.dtri.com/WCMLicense/RegisterPlayer.do?key=xxxxx&mac=111111");
//    	System.out.println(response.getResponseBodyString());
	}
}
