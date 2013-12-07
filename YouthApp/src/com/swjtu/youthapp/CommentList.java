package com.swjtu.youthapp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.swjtu.youthapp.data.FetchDataFromServer;
import com.swjtu.youthapp.po.Comment;
import com.swjtu.youthapp.widget.CommentAdapter;
import com.swjtu.youthapp.widget.PullDownListView;

/**
 * ���������б�
 * @author zhibinZeng
 * @Date 2013-2-6
 */
public class CommentList  extends Activity implements PullDownListView.OnRefreshListioner{
	private Button commentListsBackBtn;//���ذ�ť
	private PullDownListView mPullDownView;
	private ListView mListView;
	private CommentAdapter adapter;
	private static int newsnum=0;//��������
	private int newsID;//����ID
	private int categoryID;//�������ID
	private String categoryName;//�����������
	private ArrayList<HashMap<String, Object>> listItem;
	private static int updateNum=0;
	private Handler mHandler = new Handler();
	private ProgressDialog refreshDialog;// ����ProgressDialog���ͱ���
	//�����б�ͷ��
	private static int [] drawables = {R.drawable.user1,R.drawable.user2,R.drawable.user3,
		R.drawable.user4,R.drawable.user5,R.drawable.user6,R.drawable.user7,R.drawable.user8,
		R.drawable.user9,R.drawable.user10};
	//�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	private List<Integer> newsIdList = new ArrayList<Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		newsID = getIntent().getIntExtra("newsid",1);
    	categoryID = getIntent().getIntExtra("categoryid", 1);
    	categoryName = getIntent().getStringExtra("categoryName"); 
    	newsIdList = getIntent().getIntegerArrayListExtra("newsidlist");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.commentlist);
		commentListsBackBtn = (Button) findViewById(R.id.commentListBackBtn);
		commentListsBackBtn.setOnClickListener(new onClickListener());
		mPullDownView = (PullDownListView) findViewById(R.id.sreach_list);
		mPullDownView.setRefreshListioner(this);
		mListView = mPullDownView.mListView;
		refreshData();
		//�ײ�������
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
	 * ˢ������Hander
	 */
	final Handler listHandler = new Handler() {
		public void handleMessage(Message msg) {
			int type = msg.getData().getInt("type");
			if (type == 1) {
				adapter = new CommentAdapter(CommentList.this, listItem);
				mListView.setAdapter(adapter);
				mListView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						Toast.makeText(CommentList.this, "click the comment",
								Toast.LENGTH_SHORT).show();
					}
				});
				// �ж��Ƿ��ڵײ���ʾ"����"��ť
				if (newsnum > listItem.size()) {
					mPullDownView.setMore(true);// ��������true��ʾ���и�����أ�����Ϊfalse�ײ�������ʾ����
				} else {
					mPullDownView.setMore(false);
				}
			}
			if(type==2){
				mPullDownView.onRefreshComplete();//�����ʾˢ�´�����ɺ������ļ���ˢ�½�������
				if(newsnum>listItem.size()){
					mPullDownView.setMore(true);//��������true��ʾ���и�����أ�����Ϊfalse�ײ�������ʾ����
				}else{
					mPullDownView.setMore(false);
				}
				adapter.notifyDataSetChanged();	
			}
		}
	};
	
		
	/**
	 * ˢ������
	 */
	public void refreshData(){
		refreshDialog = ProgressDialog.show(CommentList.this, "�ף����Ե�һ��Ŷ...", "����Ŭ����������...",
				true);
		refreshDialog.getWindow().setGravity(Gravity.CENTER); //������ʾ�������ݶԻ���
		refreshDialog.setCancelable(true);
		new Thread() {
			public void run() {
				try {
					updateNum=1;//���´���
					listItem = new ArrayList<HashMap<String,Object>>();
					FetchDataFromServer fetchDataFromServer = new FetchDataFromServer();
					List<Comment> commentList = null;
					commentList = fetchDataFromServer.getCommentByNewsID(newsID);
					if(commentList!=null){
						//Log.d("commentlist size",commentList.size()+"");
						for(int i=0;i<commentList.size();i++){
							HashMap<String, Object> map = new HashMap<String, Object>();	
							map.put("username",commentList.get(i).getUsername());
							map.put("content", commentList.get(i).getContent());
							map.put("createtime", commentList.get(i).getCreatetime());
							Random random  = new Random();
							int r=random.nextInt(10);
							map.put("userimage",drawables[r]);
							listItem.add(map);
						}
					}
					//Thread.sleep(1500);//ģ����أ�ͣ��1.5��
					Message m = new Message();
					Bundle bundle = new Bundle();
					bundle.putInt("type",1);
					m.setData(bundle);
					listHandler.sendMessage(m);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					refreshDialog.dismiss();
				}
			}
		}.start();
	}
	
	/**
	 * ����ˢ�£�����UI
	 */
	public void onRefresh() {
		// TODO ����ˢ��
		new Thread() {
			public void run() {
				listItem.clear();
				FetchDataFromServer fetchDataFromServer = new FetchDataFromServer();
				List<Comment> commentList = null;
				commentList = fetchDataFromServer.getCommentByNewsID(newsID);
				if(commentList!=null){
					for(int i=0;i<commentList.size();i++){
						HashMap<String, Object> map = new HashMap<String, Object>();	
						map.put("username",commentList.get(i).getUsername());
						map.put("content", commentList.get(i).getContent());
						map.put("createtime", commentList.get(i).getCreatetime());
						Random random  = new Random();
						int r=random.nextInt(10);
						map.put("userimage",drawables[r]);
						listItem.add(map);
					}
				}
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("type",2);
				m.setData(bundle);
				listHandler.sendMessage(m);
			}
		}.start();
		
	}

	
	/**
	 * ������ظ���,����UI
	 */
	public void onLoadMore() {
		// TODO ��Ҽ��ظ���
		
	}
	
	
	
	


	
	/**
	 * �ײ������� ����1 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout1 = new OnClickListener() {
		public void onClick(View v) {
				main_bottom_layout1.setSelected(true);
				main_bottom_layout2.setSelected(false);
				main_bottom_layout3.setSelected(false);
				main_bottom_layout4.setSelected(false);
				Intent intent = new Intent();
				intent.setClass(CommentList.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade,R.anim.fade);
				main_bottom_layout1.setSelected(false);
				CommentList.this.finish();
		}
	};
	
	/**
	 * �ײ������� ����2 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout2 = new OnClickListener() {
		public void onClick(View v) {
			main_bottom_layout1.setSelected(false);
			main_bottom_layout2.setSelected(true);
			main_bottom_layout3.setSelected(false);
			main_bottom_layout4.setSelected(false);
			Intent intent = new Intent();
			intent.setClass(CommentList.this, UserInfo.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout2.setSelected(false);
			CommentList.this.finish();
		}
	};
	
	/**
	 * �ײ������� ����3 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout3 = new OnClickListener() {
		public void onClick(View v) {
				main_bottom_layout1.setSelected(false);
				main_bottom_layout2.setSelected(false);
				main_bottom_layout3.setSelected(true);
				main_bottom_layout4.setSelected(false);
		}
	};
	
	
	
	/**
	 * �ײ������� ����4 ��ť�����¼�
	 */
    private OnClickListener clickListener_layout4 = new OnClickListener() {
		public void onClick(View v) {
			main_bottom_layout1.setSelected(false);
			main_bottom_layout2.setSelected(false);
			main_bottom_layout3.setSelected(false);
			main_bottom_layout4.setSelected(true);
			Intent intent = new Intent();
			intent.setClass(CommentList.this, SinaWeiboWebView.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			overridePendingTransition(R.anim.fade,R.anim.fade);
			main_bottom_layout4.setSelected(false);
			CommentList.this.finish();
		}
	};
	
	
	
	
	/**
	 * �����б��ذ�ť��������
	 */
	private class onClickListener implements OnClickListener{
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CommentList.this, NewsActivity.class);
			intent.putExtra("newsid",newsID);
			intent.putExtra("categoryid", categoryID);
			intent.putExtra("categoryName",categoryName);
			intent.putIntegerArrayListExtra("newsidlist", (ArrayList<Integer>) newsIdList);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.fade);
			finish();
		}
	}
	

	/**
	 * ���·��ؼ�	
	 */
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	    	 Intent intent = new Intent();
				intent.setClass(CommentList.this, NewsActivity.class);
				intent.putExtra("newsid",newsID);
				intent.putExtra("categoryid", categoryID);
				intent.putExtra("categoryName",categoryName);
				intent.putIntegerArrayListExtra("newsidlist", (ArrayList<Integer>) newsIdList);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.fade);
				finish();
	         return false;   
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	
	

}
