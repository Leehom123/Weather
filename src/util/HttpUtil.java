package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtil {

	public static void sendHttpRequest(final String address,
			final HttpCallBackListener listener){
		
			
			
			new Thread(new Runnable() {
				
				

				@Override
				public void run() {
					// TODO Auto-generated method stub
				 HttpURLConnection connection=null;
					try {

						URL url = new URL(address);
						connection = (HttpURLConnection) url
								.openConnection();
						//给服务器发送get请求
						connection.setRequestMethod("GET");//如果不写这句也是发送get请求
						//设置请求超时时间
						connection.setConnectTimeout(8000);
						connection.setReadTimeout(8000);
						
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						StringBuilder response = new StringBuilder();
						String line;
						while((line=reader.readLine())!=null){
							response.append(line);
						}
						if (listener!=null) {
							listener.onFinish(response.toString());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if (listener!=null) {
							listener.onError(e);
						}
					}finally{
						if (connection!=null) {
							connection.disconnect();
						}
					}
				}
			}).start();
	}
}
