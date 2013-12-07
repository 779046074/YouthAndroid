package com.swjtu.youthapp;
import com.swjtu.youthapp.data.FetchDataFromServer;
import com.swjtu.youthapp.po.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * @author zhibinZeng
 * @Date 2013-1-31
 * @Description �û�ע��Activity
 */
public class UserRegister extends Activity{
	private static final String[] ADDRESS = {"����ի1","����ի2","����ի3","����ի4","����ի5","����ի6"
		,"����ի7","����ի8","����ի9","����ի10","����ի11","����ի12","����ի13","����ի14","����ի15","����ի16"
		,"����ի17","����ի18","����ի19","����ի20","����ի21","����ի22","����"
	};
	private static final String[] QUESTION = {"������Һ���ʲô?","��ĳ�����˭?","���СѧУ����ʲô?","������İ�������˭?"};
	private EditText registerName,password,passwordConfirm,answer;
	private Button registerSubmitBtn,registerResetBtn;
	private RadioGroup sexRadioGroup;
	private Spinner addressSpinner,questionSpinner;
	private boolean flag = true;
	private ProgressDialog registerProgressDialog;
	private Integer type=3;//ע�᷵����
	/* type=1 �û��Ѿ�����
	 * type=2 ע��ɹ�
	 * type=3 ע��ʧ��
	 */
	private static final String USER_INFO_SHARE_PREFERENCE="userinfo";
	private static final String USER_NAME="username";
	private static final String USER_PWD = "userpwd";
	//��ҳ�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.userregister);
	        registerName = (EditText) findViewById(R.id.registerName);
	        password = (EditText) findViewById(R.id.password);
	        passwordConfirm = (EditText) findViewById(R.id.passwrodConfirm);
	        answer = (EditText)findViewById(R.id.answer);
	        registerSubmitBtn = (Button) findViewById(R.id.registerSubmitBtn);
	        registerResetBtn = (Button) findViewById(R.id.registerResetBtn);
	        sexRadioGroup = (RadioGroup) findViewById(R.id.sexMenu);
	        addressSpinner = (Spinner) findViewById(R.id.address);
	        questionSpinner = (Spinner) findViewById(R.id.question);
	        registerSubmitBtn.setOnClickListener(submitOnClickListener);
	        registerResetBtn.setOnClickListener(resetOnClickListener);
	        initData();
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
				intent.setClass(UserRegister.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout2.setSelected(false);
				UserRegister.this.finish();
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
					intent.setClass(UserRegister.this, UserInfo.class);
					intent.putExtra("clickble", true);
					startActivity(intent);
					main_bottom_layout2.setSelected(false);
					UserRegister.this.finish();
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
				intent.setClass(UserRegister.this, VisionActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout3.setSelected(false);
				UserRegister.this.finish();
				
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
					intent.setClass(UserRegister.this, SinaWeiboWebView.class);
					intent.putExtra("clickble", true);
					startActivity(intent);
					overridePendingTransition(R.anim.fade,R.anim.fade);
					main_bottom_layout4.setSelected(false);
					UserRegister.this.finish();
			}
		};
		
	 
	 
	 /**
	  * ��ʼ����ʾ����
	  */
	 private void initData(){
		  ArrayAdapter<String> addressAdpter = new ArrayAdapter<String>(this,
				  android.R.layout.simple_spinner_item,
				  ADDRESS);
		  addressAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      addressSpinner.setAdapter(addressAdpter);
		  
	      ArrayAdapter<String> questionAdapter = new ArrayAdapter<String>(this, 
				  android.R.layout.simple_spinner_item,
				  QUESTION);
	      questionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      questionSpinner.setAdapter( questionAdapter);
	      
	 }
	 
	 
	 /**
	  * validate Data 
	  */
	 private Boolean validateData(){
		 if(registerName.getText().toString().trim().length()==0){
			 registerName.setError("�������û���");
			 return false;
		 }
		 if(password.getText().toString().trim().length()==0){
			 password.setError("����������");
			 return false;
		 }
		 if(passwordConfirm.getText().toString().trim().length()==0){
			 passwordConfirm.setError("���ٴ���������");
			 return false;
		 }
		if(passwordConfirm.getText().toString().trim().length()!=0&&
				!passwordConfirm.getText().toString().trim().equals(password.getText().toString().trim())){
			passwordConfirm.setError("�����������벻ƥ��");
			 return false;
		}
		if(answer.getText().toString().trim().length()==0){
			 answer.setError("�������");
			 return false;
		 }
		 return true;
	 }
	 
	 
	 /**
	  * �����û�ѡ���address
	  */
	 private String getAddress(){
		 return ADDRESS[addressSpinner.getSelectedItemPosition()];
	 }
	 

	 /**
	  * �����û�ѡ����ܱ�����
	  */
	 private String getQuestion(){
		 return QUESTION[questionSpinner.getSelectedItemPosition()];
	 }
	 
	 
	 /**
	  * �����û��Ա�
	  */
	 private String getSex(){
			RadioButton mRadio = (RadioButton) findViewById(sexRadioGroup
	    			.getCheckedRadioButtonId());
	    			return mRadio.getText().toString();
	 }
	 
	 
	 /**
	  * ע����ɺ�ת���½ҳ��
	  */
	 Handler registerHandler = new Handler(){
			public void handleMessage(Message msg) {
				//TODO ת���¼ҳ��
				Integer type=msg.getData().getInt("type");
				if(type==1){
					Toast.makeText(UserRegister.this,"�û����Ѵ��ڣ�����������",Toast.LENGTH_SHORT).show();
				}else if(type==2){
					Toast.makeText(UserRegister.this,"ע��ɹ���������ת����½ҳ��",Toast.LENGTH_SHORT).show();
					SharedPreferences share = getSharedPreferences(USER_INFO_SHARE_PREFERENCE, 0);
					share.edit().putString(USER_NAME,registerName.getText().toString().trim()).commit();
					share.edit().putString(USER_PWD,password.getText().toString().trim()).commit();
					Intent intent = new Intent();
					intent.setClass(UserRegister.this,UserLogin.class);
					startActivity(intent);
					overridePendingTransition(R.anim.fade, R.anim.fade);
					UserRegister.this.finish();
					//��ת����½ҳ��
				}else{
					Toast.makeText(UserRegister.this,"������ϣ�ע��ʧ��",Toast.LENGTH_SHORT).show();
				}
			}
		 
	 };
	 
	 
	 /**
	  *submit button listener
	  */
	 private OnClickListener submitOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			flag = validateData();
			if(flag){
				new AlertDialog.Builder(UserRegister.this).setTitle(
						"ע���û���Ϣ").setMessage(
								"�û�����"+registerName.getText().toString()+"\n"
								+"�Ա�"+getSex()+"\n"
								+"סַ��"+getAddress()+"\n"
								+"�ܱ����⣺"+getQuestion()+"\n"
								+"�ܱ���: "+answer.getText().toString()+"\n"
								)
								.setCancelable(false)
								.setPositiveButton("ȷ��",
										new DialogInterface.OnClickListener(){
									public void onClick(
											DialogInterface dialog,int id){
										registerProgressDialog=ProgressDialog.show(UserRegister.this, "��������", "���Ե�....",true);
										registerProgressDialog.setCancelable(true);
										FetchDataFromServer registerToServer = new FetchDataFromServer();
										User user = new User();
										user.setName(registerName.getText().toString().trim());
										user.setPassword(password.getText().toString().trim());
										user.setSex(getSex());
										user.setAddress(getAddress());
										user.setQuestion(getQuestion());
										user.setAnswer(answer.getText().toString().trim());
										type=registerToServer.userRegister(user);
										Message message = new Message();
										Bundle bundle = new Bundle();
						                bundle.putInt("type", type);
						                message.setData(bundle);
										registerHandler.sendMessage(message);
										registerProgressDialog.dismiss();
									}
								}).setNegativeButton("ȡ��",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												dialog.dismiss();
											}
										}).show();
			
				}
		};
	 };
	 
	 
	 /**
	  * reset button listener
	  */
	 private OnClickListener resetOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			registerName.setText("");
			password.setText("");
			passwordConfirm.setText("");
			answer.setText("");
		}
	};

}
