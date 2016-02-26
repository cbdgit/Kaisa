package com.login.operation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.login.network.ConnNet;

public class Operaton 
{

	public String login(String url,String username,String password) //��½     url=login
	{    
		String result = null;     //result�ǵ�½�ɹ�������ʾ
		ConnNet connNet=new ConnNet();
		List<NameValuePair> params=new ArrayList<NameValuePair>();//TODO NameValuePair����ֵ�ԣ��÷�?
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		try {
			HttpEntity entity=new UrlEncodedFormEntity(params, HTTP.UTF_8);//��List����paramsת��Ϊentity
			HttpPost httpPost=connNet.gethttPost(url);
			System.out.println(httpPost.toString());
			//Ӧ�����System.out: http://10.131.141.214:8080/LoginRegister_ser/Login
			httpPost.setEntity(entity);//entityΪҪ���͵�����
			HttpClient client=new DefaultHttpClient();
			HttpResponse httpResponse=client.execute(httpPost);
			//����HttpCLient��ȡ��HttpResponse��excute����ֵΪhttpResponse����
			if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) //�������ɹ�
			{
				result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");	//���������ķ���ֵ����result		
			}
			else
			{
				result="����ʧ��";
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;
	}
	public String checkusername(String url,String username)  //����û����Ƿ��Ѿ�����
	{
		String result=null;
		ConnNet connNet=new ConnNet();
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		try {
			HttpEntity entity=new UrlEncodedFormEntity(params, HTTP.UTF_8);
			HttpPost httpPost=connNet.gethttPost(url);
			System.out.println(httpPost.toString());
			httpPost.setEntity(entity);
			HttpClient client=new DefaultHttpClient();
			HttpResponse httpResponse=client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) 
			{
				result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");	
				System.out.println("resu"+result);
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;  
	}

	public String UpData(String uripath,String jsonString)  //ע��
	{ 
		String result = null;
		List<NameValuePair> list=new ArrayList<NameValuePair>();
//		NameValuePair nvp=new BasicNameValuePair("jsonstring", jsonString);
//		list.add(nvp);
		list.add(new BasicNameValuePair("jsonstring", jsonString));
		ConnNet connNet=new ConnNet();
		HttpPost httpPost=connNet.gethttPost(uripath);
		try {
			HttpEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
			//�˾������Ϸ��򴫵��ͻ��˵����Ľ�������
			httpPost.setEntity(entity);
			HttpClient client=new DefaultHttpClient();
			HttpResponse httpResponse=client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode()==200) //TODO
			{
				result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");	
				System.out.println("resu"+result);
			}
			else {
				result="ע��ʧ��";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {			
			e.printStackTrace();
		} catch (ParseException e) {		
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return result;  
	}
	
	
	public String uploadFile(File file,String urlString) //�����ļ�
	{
		final String TAG = "uploadFile";
		final int TIME_OUT = 10*1000;   //��ʱʱ��
		final String CHARSET = "utf-8"; //���ñ���
		String result = null;
		String  BOUNDARY =  UUID.randomUUID().toString();  //�߽��ʶ   �������
		String PREFIX = "--" , LINE_END = "\r\n"; 
		String CONTENT_TYPE = "multipart/form-data";   //��������

		try {
			ConnNet connNet=new ConnNet();
		    HttpURLConnection conn	=connNet.getConn(urlString);
			conn.setReadTimeout(TIME_OUT);
//			conn.setConnectTimeout(TIME_OUT);
//			conn.setDoInput(true);  //����������
//			conn.setDoOutput(true); //���������
//			conn.setUseCaches(false);  //������ʹ�û���
//			conn.setRequestMethod("POST");  //����ʽ
			conn.setRequestProperty("Charset", CHARSET);  //���ñ���
			conn.setRequestProperty("connection", "keep-alive");   
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY); 

			if(file!=null)
			{
				/**
				 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
				 */
				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * �����ص�ע�⣺
				 * name�����ֵΪ����������Ҫkey   ֻ�����key �ſ��Եõ���Ӧ���ļ�
				 * filename���ļ������֣�������׺����   ����:abc.png  
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END); 
				sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while((len=is.read(bytes))!=-1)
				{
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * ��ȡ��Ӧ��  200=�ɹ�
				 * ����Ӧ�ɹ�����ȡ��Ӧ����  
				 */
				int res = conn.getResponseCode();  
				Log.e(TAG, "response code:"+res);
				//                if(res==200)
				//                {
				Log.e(TAG, "request success");
				InputStream input =  conn.getInputStream();
				StringBuffer sb1= new StringBuffer();
				int ss ;
				while((ss=input.read())!=-1)
				{
					sb1.append((char)ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : "+ result);
				//                }
				//                else{
				//                    Log.e(TAG, "request error");
				//                }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
 
}

	
	
	