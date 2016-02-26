package com.login;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.login.bean.User;
import com.login.json.WriteJson;
import com.login.operation.Operaton;
import com.qsc.main.R;

public class RegisterActivity extends Activity {
	private Button registerBn,gobackBn;
	private EditText username_1,password_1,repassword_1,age_1,email_1;
	private RadioButton ckman;
	String str;
	
	String jsonString=null;
	ProgressDialog dialog;
	private boolean isnetwork = false;
	
	String username=null;
	String password=null;
	String sex=null;
	String age=null;
	String email=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		init();
		initListener();
	}
	
	private boolean checknetwork(){
		ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting(); 
		isnetwork = wifi|internet;
		return isnetwork;
	}
	
	private void init() {
		
		registerBn = (Button) this.findViewById(R.id.regi_register);
		gobackBn = (Button) this.findViewById(R.id.regi_goback);
		username_1=(EditText) findViewById(R.id.regi_userName);
		password_1=(EditText) findViewById(R.id.regi_password);
		repassword_1=(EditText) findViewById(R.id.regi_repassword);
		ckman=(RadioButton) findViewById(R.id.regi_male);
		
		age_1=(EditText) findViewById(R.id.age);
		email_1=(EditText) findViewById(R.id.communication_content);
		dialog=new ProgressDialog(RegisterActivity.this);
		
		dialog.setTitle(R.string.loading);
		dialog.setMessage("���Ե�...");
		dialog.setCancelable(false);
		
	}
	
	private void initListener() {
		this.registerBn.setOnClickListener(new RegisterListener());
		this.gobackBn.setOnClickListener(new GobackButton());
		this.username_1.setOnFocusChangeListener(new username_1OnFocusChange());
	}
	
	private class username_1OnFocusChange implements OnFocusChangeListener
	{
		public void onFocusChange(View v, boolean hasFocus) {
			if (!username_1.hasFocus()) {			//����겻���û��������ʱ
				str=username_1.getText().toString().trim();
				if (str==null||str.length()<=0) 
				{
					username_1.setError("�û�������Ϊ��");
				}
				else 
				{	
					checknetwork();
					if(isnetwork){  
					    //ִ����ز���  
						new Thread(new Runnable() {
			                 //����û�����Ϊ�գ���ô���û����ύ���������Ͻ�����֤�����û����Ƿ���ڣ�����JavaEE������
							 //ajaxһ������Ȼ�㿴����������ȴ͵͵�������˺ܶ�
									public void run() {
										Operaton operaton=new Operaton();
										String result= operaton.checkusername("Check", str);
										//System.out.println("����û������ص�result1:"+result);
										Message message=new Message();
										message.obj=result;
										handler.sendMessage(message);
									}
								}).start();
						
					}else{  
					    Toast.makeText(RegisterActivity.this,R.string.isconnect, Toast.LENGTH_LONG).show();  
					}
					
				}
			}
		}
	}
	
	Handler handler=new Handler()
	{
		
		/* sendMessage
          baocuo overrides android.os.Handler.handleMessage*/
		@Override
		public void handleMessage(Message msg) {
			String msgobj=(String ) msg.obj;  //�ַ���Ϊnullʱ����toString()�ᱨ����(String)����
			System.out.println(R.string.result2+msgobj);
			
			if (msgobj==null||msgobj.length()<=0) {
				Toast.makeText(RegisterActivity.this, "�û�����֤��ʱ", Toast.LENGTH_SHORT).show();
				return;
			}

			if (msgobj.equals("t")) {  
				username_1.requestFocus();
				username_1.setError("�û���"+str+"�Ѵ���");
			}	
			else 
			{						
				password_1.requestFocus();
			}
			super.handleMessage(msg);
		}	
	};
	
	private class RegisterListener implements OnClickListener{
		
	

		@Override
		public void onClick(View v) {
			
		if(!password_1.getText().toString().trim().equals(repassword_1.getText().toString().trim())){
				password_1.setError("�������벻һ��");
				repassword_1.setError("�������벻һ��");
				
		}
		else{		
			username=username_1.getText().toString().trim();
			password=password_1.getText().toString().trim();
			if (ckman.isChecked()) {
				sex="��";
			}
			else {
				sex="Ů";
			}
			age=age_1.getText().toString().trim();
			if (age==null||age.length()<=0) 
			{  
				age_1.requestFocus();
				age_1.setError("���䲻��Ϊ��");			
				return ;
			}
			email=email_1.getText().toString().trim();
			
			checknetwork();
			if(isnetwork){  
			    //ִ����ز���  
				dialog.show();
				new Thread(new Runnable() {

					public void run() {

						Operaton operaton=new Operaton();
						
						User user=new User(username, password, sex, age,email);
						//����һ��user����
						List<User> list=new ArrayList<User>();
						list.add(user);
						WriteJson writeJson=new WriteJson();
						//��user����д��json��ʽ�ַ���
						jsonString= writeJson.getJsonData(list);
	                     
						String result= operaton.UpData("Register", jsonString);
						Message msg=new Message();
						msg.obj=result;
						handler1.sendMessage(msg);
					}
				}).start();
				
			}else{  
			    Toast.makeText(RegisterActivity.this,"�ף���������ô��", Toast.LENGTH_LONG).show();  
			}
		}
		}
		
		
	}
	
	Handler handler1=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			String msgobj=(String) msg.obj;
			if (msgobj==null||msgobj.length()<=0) {
				Toast.makeText(RegisterActivity.this, "ע�ᳬʱ", Toast.LENGTH_SHORT).show();
				return;
			}
			if(msgobj.equals("t"))
			{
				Toast.makeText(RegisterActivity.this, "ע��ɹ�", 0).show();
				Intent intent=new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
			else {
				Toast.makeText(RegisterActivity.this, "ע��ʧ��", 0).show();
			}
			super.handleMessage(msg);
		}	
	};
	
	private class GobackButton implements OnClickListener{
		

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		
		
	}

}
