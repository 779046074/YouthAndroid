package com.swjtu.youthapp;
import com.swjtu.youthapp.data.FetchDataFromServer;
import com.swjtu.youthapp.data.SqliteControl;
import com.swjtu.youthapp.po.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
public class UserLogin extends Activity{
	//UI 
	private EditText loginUserNameEdit,loginPasswordEditText;
	private Button loginSubmitBtn,loginRegisterBtn;
	private CheckBox loginRememberMeCheckBox;
	private Boolean flag=false;
	private ProgressDialog loginProgressDialog;
	private static final String USER_INFO_SHARE_PREFERENCE="userinfo";
	private static final String USER_NAME="username";
	private static final String USER_PWD = "userpwd";
	private int newsid,categoryid;
	private String categoryName,from;
	//��ҳ�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		from="other";
    	from = getIntent().getStringExtra("from");
    	if(from.equals("NewsActivity")){
    		newsid = getIntent().getIntExtra("newsid",1);
        	categoryid = getIntent().getIntExtra("categoryid", 1);
        	categoryName = getIntent().getStringExtra("categoryName"); 
    	}
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.login);
	    loginUserNameEdit = (EditText) findViewById(R.id.loginUserNameEdit);
	    loginPasswordEditText = (EditText) findViewById(R.id.loginPasswordEdit);
	    loginSubmitBtn = (Button) findViewById(R.id.loginSubmit);
	    loginSubmitBtn.setOnClickListener(loginOnClickListener);
	    loginRegisterBtn = (Button) findViewById(R.id.loginRegister);
	    loginRegisterBtn.setOnClickListener(registerClickListener);
	    loginRememberMeCheckBox = (CheckBox) findViewById(R.id.loginRememberMeCheckBox);
	    getSharePreference();
	  //��ҳ�ײ�������
		main_bottom_layout1 = (LinearLayout) findViewById(R.id.main_bottom_layout1_ly);
		main_bottom_layout1.setOnClickListener(clickListener_layout1);
		main_bottom_layout2 = (LinearLayout) findViewById(R.id.main_bottom_layout2_ly);
		main_bottom_layout2.setOnClickListener(clickListener_layout2);
		main_bottom_layout3 = (LinearLayout) findViewById(R.id.main_bottom_layout3_ly);
		main_bottom_layout3.setOnClickListener(clickListener_layout3);
		main_bottom_layout4 = (LinearLayout) findViewById(R.id.main_bottom_layout4_ly);
		main_bottom_layout4.setOnClickListener(clickListener_layout4);
	  }

	/**
	 * ��ҳ�ײ������� ����1 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout1 = new OnClickListener() {
		public void onClick(View v) {
				main_bottom_layout1.setSelected(true);
				main_bottom_layout2.setSelected(false);
				main_bottom_layout3.setSelected(false);
				main_bottom_layout4.setSelected(false);
				Intent intent = new Intent();
				intent.setClass(UserLogin.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout2.setSelected(false);
				UserLogin.this.finish();
				
		}
	};

	/**
	 * ��ҳ�ײ������� ����2 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout2 = new OnClickListener() {
		public void onClick(View v) {
			main_bottom_layout1.setSelected(false);
			main_bottom_layout2.setSelected(true);
			main_bottom_layout3.setSelected(false);
			main_bottom_layout4.setSelected(false);
			Intent intent = new Intent();
			intent.setClass(UserLogin.this, UserInfo.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout2.setSelected(false);
			UserLogin.this.finish();
		}
	};
	
	/**
	 * ��ҳ�ײ������� ����3 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout3 = new OnClickListener() {
		public void onClick(View v) {
			main_bottom_layout1.setSelected(false);
			main_bottom_layout2.setSelected(false);
			main_bottom_layout3.setSelected(true);
			main_bottom_layout4.setSelected(false);
			Intent intent = new Intent();
			intent.setClass(UserLogin.this, VisionActivity.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout3.setSelected(false);
			UserLogin.this.finish();
		}
	};

	/**
	 * ��ҳ�ײ������� ����4 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout4 = new OnClickListener() {
		public void onClick(View v) {
				main_bottom_layout1.setSelected(false);
				main_bottom_layout2.setSelected(false);
				main_bottom_layout3.setSelected(false);
				main_bottom_layout4.setSelected(true);
				Intent intent = new Intent();
				intent.setClass(UserLogin.this, SinaWeiboWebView.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade,R.anim.fade);
				main_bottom_layout4.setSelected(false);
				UserLogin.this.finish();
		}
	};
	
	
	/**
	 * ��¼��ť������
	 */
	private OnClickListener loginOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			flag = validateData();
			if(flag){
				//����Ի����ܷ������߳��ڣ���Ȼ����ԭ��?
				loginProgressDialog = ProgressDialog.show(UserLogin.this,"�û���¼","���ڵ�¼,���Ժ�...",true);
				loginProgressDialog.setCancelable(true);//�����ؼ�ȡ����ʾ
				new Thread(){
					public void run(){
						
						try {
							Thread.sleep(3*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						User user = null;
						FetchDataFromServer loginDataFromServer = new FetchDataFromServer();
						user = loginDataFromServer.userLogin(loginUserNameEdit.getText().toString().trim(), 
								loginPasswordEditText.getText().toString().trim());
						loginProgressDialog.dismiss();
						Message message = new Message();
						Bundle bundle = new Bundle();
						if(user!=null){
							SharedPreferences share = getSharedPreferences(USER_INFO_SHARE_PREFERENCE, 0);
							share.edit().putString(USER_NAME,loginUserNameEdit.getText().toString().trim()).commit();
							//�ж��Ƿ񱣴�����
							if(loginRememberMeCheckBox.isChecked()){
								share.edit().putString(USER_PWD,loginPasswordEditText.getText().toString().trim()).commit();
							}
							share=null;//�ͷű���
							//��¼�ɹ����û���Ϣд�����ݿ�
							SqliteControl sqliteControl = new SqliteControl(UserLogin.this);
							SQLiteDatabase db = sqliteControl.getDatabase();
							db.delete("user",null,null);
							sqliteControl.InsertIntoUser(user.getId(), user.getName(), user.getPassword(),
									user.getSex(),user.getAge(), user.getAddress(), user.getRegistertime(),
									user.getQuestion(), user.getAnswer(),user.getMarry(),user.getHobby(),user.getEmail());
							sqliteControl.close();
							bundle.putInt("type",0);
						}else{
							bundle.putInt("type", 1);
						}
						message.setData(bundle);
						loginHandler.sendMessage(message);
					}
				}.start();
			}
		}
	};
	

	
	/**
	 * ���ע�ᰴť
	 */
	private OnClickListener registerClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(UserLogin.this,UserRegister.class);
			startActivity(intent);
			finish();
			
		}
	};
	
	
	
	/**
	 * ��¼У��
	 */
	private Boolean validateData(){
		if(loginUserNameEdit.getText().toString().trim().length()==0){
			loginUserNameEdit.setError("�������û���");
			return false;
		}
		if(loginPasswordEditText.getText().toString().trim().length()==0){
			loginPasswordEditText.setError("����������");
			return false;
		}
		return true;
	}
	
	
	/**
	 * ��SharePreference�л�ȡ����
	 */
	
	private void getSharePreference(){
		 SharedPreferences share = getSharedPreferences(USER_INFO_SHARE_PREFERENCE, 0);
	     String userName = share.getString(USER_NAME, "");
	     String password = share.getString(USER_PWD, "");
	     if(!"".equals(userName.trim())){
	    	 loginUserNameEdit.setText(userName);
	     }
	     if(!"".equals(password)){
	    	 loginPasswordEditText.setText(password);
	    	 loginRememberMeCheckBox.setChecked(true);
	     }
	     // �������Ҳ������ ֱ���õ�¼��ť��ȡ����
	     if(!"".equals(loginPasswordEditText.getText().toString().trim().length()>0)){
	    	 loginSubmitBtn.requestFocus();
	     }
	}
	
	
	/**
	 * Hander
	 */
	final Handler loginHandler = new Handler() {
	public void handleMessage(Message msg) {
		Integer type=msg.getData().getInt("type");
		if(type==0){//�ɹ���¼����ת������ҳ��
			if(from.equals("NewsActivity")){
				Intent intent=new Intent();
				intent.setClass(UserLogin.this, NewsActivity.class);
				intent.putExtra("categoryid",categoryid);
				intent.putExtra("newsid",newsid);
				intent.putExtra("categoryName", categoryName);
				startActivity(intent);	
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				UserLogin.this.finish();
			}
			if(from.equals("UserInfo")){
				Intent intent=new Intent();
				intent.setClass(UserLogin.this, UserInfo.class);
				startActivity(intent);	
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				UserLogin.this.finish();
			}
		}else{//��¼ʧ��
			Toast.makeText(UserLogin.this, "�������,��¼ʧ�ܡ�",
					Toast.LENGTH_SHORT).show();
		}
	}
};

}
