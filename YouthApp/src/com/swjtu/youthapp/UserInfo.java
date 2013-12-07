package com.swjtu.youthapp;
import com.swjtu.youthapp.data.SqliteControl;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author zhibinzeng
 * @Date 2013-2-19
 * @Description �û���Ϣ
 */
public class UserInfo extends Activity{
	private TextView user_info_name,user_info_sex,user_info_address,
			user_info_registertime,user_info_marry,user_info_hobby,
			user_info_email;
	//ע����¼
	private Button Logout;
	//�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    setContentView(R.layout.user_info);
	    user_info_name = (TextView) findViewById(R.id.user_info_name);
	    user_info_sex = (TextView) findViewById(R.id.user_info_sex);
	    user_info_registertime = (TextView) findViewById(R.id.user_info_registertime);
	    user_info_marry = (TextView) findViewById(R.id.user_info_marry);
	    user_info_hobby = (TextView) findViewById(R.id.user_info_hobby);
	    user_info_address = (TextView) findViewById(R.id.user_info_address);
	    user_info_email = (TextView) findViewById(R.id.user_info_email);
	    Logout = (Button) findViewById(R.id.Logout);
	    Logout.setOnClickListener(logoutOnClickListener);
	    initData();
	  //�жϵײ��������Ƿ񱻴���
        boolean clickble = getIntent().getBooleanExtra("clickble", true);
	  //��ҳ�ײ�������
	  	main_bottom_layout1 = (LinearLayout) findViewById(R.id.main_bottom_layout1_ly);
	  	main_bottom_layout1.setOnClickListener(clickListener_layout1);
  		main_bottom_layout2 = (LinearLayout) findViewById(R.id.main_bottom_layout2_ly);
  		main_bottom_layout2.setOnClickListener(clickListener_layout2);
  		main_bottom_layout2.setSelected(clickble);
  		main_bottom_layout3 = (LinearLayout) findViewById(R.id.main_bottom_layout3_ly);
  		main_bottom_layout3.setOnClickListener(clickListener_layout3);
  		main_bottom_layout4 = (LinearLayout) findViewById(R.id.main_bottom_layout4_ly);
  		main_bottom_layout4.setOnClickListener(clickListener_layout4);
  		
	}
	
	
	/**
	 *���ݳ�ʼ�� 
	 */
	private void initData(){
		SqliteControl sqliteControl = new SqliteControl(UserInfo.this);
		SQLiteDatabase db = sqliteControl.getDatabase();
		String sqlString = "select name,sex,address,registertime,marry,hobby,email from user";
		Cursor cursor = db.rawQuery(sqlString, null);
		if(cursor.getCount()==0){
			//�û���δ��¼����ת����¼����
			cursor.close();
			sqliteControl.close();
			Intent intent = new Intent();
			intent.setClass(UserInfo.this,UserLogin.class);
			intent.putExtra("from","UserInfo");
			startActivity(intent);
			finish();
		}else{
			cursor.moveToFirst();
			user_info_name.setText(cursor.getString(0));
			user_info_sex.setText(cursor.getString(1));
			user_info_address.setText(cursor.getString(2));
			user_info_registertime.setText(cursor.getString(3));
			user_info_marry.setText(cursor.getString(4));
			user_info_hobby.setText(cursor.getString(5));
			user_info_email.setText(cursor.getString(6));
		}
	}
	
	
	/**
	 * ע����¼������
	 */
	private OnClickListener logoutOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			SqliteControl sqliteControl = new SqliteControl(UserInfo.this);
			SQLiteDatabase db = sqliteControl.getDatabase();
			db.delete("user",null,null);
			sqliteControl.close();
			Intent intent = new Intent();
			intent.setClass(UserInfo.this,UserLogin.class);
			intent.putExtra("from","UserInfo");
			startActivity(intent);
			finish();
		}
	};
	

	
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
				intent.setClass(UserInfo.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout2.setSelected(false);
				UserInfo.this.finish();
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
			intent.setClass(UserInfo.this, VisionActivity.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout3.setSelected(false);
			UserInfo.this.finish();
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
				intent.setClass(UserInfo.this, SinaWeiboWebView.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade,R.anim.fade);
				main_bottom_layout4.setSelected(false);
				UserInfo.this.finish();
		}
	};
	

}
