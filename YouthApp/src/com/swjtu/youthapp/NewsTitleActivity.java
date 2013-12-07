package com.swjtu.youthapp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.swjtu.youthapp.data.SqliteControl;
import com.swjtu.youthapp.data.UpdateSqliteDataFromServer;
import com.swjtu.youthapp.po.NewsEntity;
import com.swjtu.youthapp.widget.MyAdapter;
import com.swjtu.youthapp.widget.PullDownListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class NewsTitleActivity extends Activity implements PullDownListView.OnRefreshListioner {
	private static int updateNum=0;
	private ProgressDialog myDialog;// ����ProgressDialog���ͱ���
	private static int refreshNoNews=1;
	private static int newsnum=0;//��������
	private static int offset=1;
	private final static int PERPAGE=10;//ÿҳĬ����ʾ����
	private static int usertotal=PERPAGE;//�û�ȷ��һ����ʾ��������
	private Handler mHandler;
	private ArrayList<HashMap<String, Object>> listItem;
	private PullDownListView mPullDownView;
	private ListView mListView;
	private MyAdapter adapter;
	private TextView categoryNameTV;//��Ŀ����
	private Button newsTitleActivityBtn;//ͷ�����ذ�ť
	private int selectedcategoryid;
	private String categoryName;
	//�û��洢�÷����µ������������ݣ����ݸ�NewsActivity,�û���һƪ����һƪ����
	private List<Integer> newsIdList;
	//�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	//int index;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.newstitles);
		selectedcategoryid=getIntent().getIntExtra("categoryid", 1);
		categoryName = getIntent().getStringExtra("categoryName");
		newsIdList = new ArrayList<Integer>();
		mHandler = new Handler();
		mPullDownView = (PullDownListView) findViewById(R.id.sreach_list);
		mPullDownView.setRefreshListioner(this);
		mListView = mPullDownView.mListView;
		categoryNameTV = (TextView) findViewById(R.id.newsCategoryName);
		categoryNameTV.setText(categoryName);

		newsTitleActivityBtn = (Button) findViewById(R.id.newsTitleActivityBtn);
		newsTitleActivityBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO �������ذ�ť
				Intent intent = new Intent();
				intent.setClass(NewsTitleActivity.this,ViewFlipperActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				NewsTitleActivity.this.finish();//�رյ�ǰActivity
			}
		});
		
		//�ײ�������
		main_bottom_layout1 = (LinearLayout) findViewById(R.id.main_bottom_layout1_ly);
		main_bottom_layout1.setOnClickListener(clickListener_layout1);
		
		main_bottom_layout2 = (LinearLayout) findViewById(R.id.main_bottom_layout2_ly);
		main_bottom_layout2.setOnClickListener(clickListener_layout2);
		
		main_bottom_layout3 = (LinearLayout) findViewById(R.id.main_bottom_layout3_ly);
		main_bottom_layout3.setOnClickListener(clickListener_layout3);
		
		main_bottom_layout4 = (LinearLayout) findViewById(R.id.main_bottom_layout4_ly);
		main_bottom_layout4.setOnClickListener(clickListener_layout4);
		
		

		InitNewsTitles(selectedcategoryid);
		if(listItem.size()==0&&updateNum==0){
			refreshData();
		}
			//�ж��Ƿ��ڵײ���ʾ"����"��ť
			if(newsnum>listItem.size()){
				mPullDownView.setMore(true);//��������true��ʾ���и�����أ�����Ϊfalse�ײ�������ʾ����
			}else{
				mPullDownView.setMore(false);
			}
			adapter = new MyAdapter(this,listItem);	
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					int newsid;
					int preNewsid=-1;//��һƪ����id
					int nextNewsid=-1;//��һƪ����id
					int categoryid;
						newsid=(Integer)listItem.get(position-1).get("newsid");
						if(position!=1){
							preNewsid = (Integer)listItem.get(position-2).get("newsid"); 
						}
						if(position!=listItem.size()){
							nextNewsid = (Integer)listItem.get(position).get("newsid"); 
						}
						 
						categoryid = (Integer)listItem.get(position-1).get("categoryid");
						Intent intent=new Intent();
						intent.setClass(NewsTitleActivity.this, NewsActivity.class);
						intent.putExtra("categoryid",categoryid);
						intent.putExtra("newsid",newsid);
						intent.putExtra("categoryName", categoryName);
						intent.putIntegerArrayListExtra("newsidlist", (ArrayList<Integer>) newsIdList);
						startActivity(intent);	
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						NewsTitleActivity.this.finish();
				}
			});
		}
	

	//ˢ������Hander
		final Handler listHandler = new Handler() {
			public void handleMessage(Message msg) {
			Intent intent = new Intent();
			intent.setClass(NewsTitleActivity.this,NewsTitleActivity.class);
			intent.putExtra("categoryid", selectedcategoryid);
			intent.putExtra("categoryName", categoryName);
			startActivity(intent);
			NewsTitleActivity.this.finish();
			}
		};
		
		//ˢ������
		public void refreshData(){
			myDialog = ProgressDialog.show(NewsTitleActivity.this, "�ף����Ե�һ��Ŷ...", "����Ŭ����������...",
					true);
			myDialog.getWindow().setGravity(Gravity.CENTER); //������ʾ�������ݶԻ���
			myDialog.setCancelable(true);
			new Thread() {
				public void run() {
					try {
						updateNum=1;//���´���
						//�������ݿ�
						UpdateSqliteDataFromServer updateSqliteDataFromServer = new UpdateSqliteDataFromServer(NewsTitleActivity.this);
						updateSqliteDataFromServer.UpdateNewsByCategory(selectedcategoryid);
						//updateSqliteDataFromServer.updateCategoryImage(selectedcategoryid);
						Thread.sleep(1500);//ģ����أ�ͣ��1.5��
						Message m = new Message();
						listHandler.sendMessage(m);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						myDialog.dismiss();
					}
				}
			}.start();
		}
	public void InitNewsTitles(int selectedcategoryid)
	{	listItem=new ArrayList<HashMap<String, Object>>();
		// FetchDataFromServer dataFromServer = new FetchDataFromServer();
		 SqliteControl sqliteControl= new SqliteControl(NewsTitleActivity.this);
		 List<NewsEntity> newsList =new ArrayList<NewsEntity>();
		 newsList = sqliteControl.GetNews(selectedcategoryid, usertotal);
		 sqliteControl.close();
		 SqliteControl sqliteControl2 = new SqliteControl(NewsTitleActivity.this);
		 newsnum=sqliteControl2.getNewsCount(selectedcategoryid);
		 sqliteControl2.close();
		 if(newsList!=null){
	     for(int i=0;i<newsList.size();i++)
         {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.viewnewsdetail);//ͼ����Դ��ID,��ͼƬ����Ϊnull
            map.put("ItemTitle", newsList.get(i).getTitle());
            map.put("newsid", newsList.get(i).getId());
            map.put("categoryid",selectedcategoryid);
            map.put("time",newsList.get(i).getTime());
            map.put("comefrom", newsList.get(i).getComefrom());
            listItem.add(map);
            newsIdList.add(newsList.get(i).getId());
         }
		 }
	  
	 }
	private int addLists(){
		UpdateSqliteDataFromServer dataFromServer =new UpdateSqliteDataFromServer(this);
		int num=dataFromServer.UpdateNewsByCategory(selectedcategoryid);
		if(num>0){
			refreshNoNews=1;
			listItem.clear();
			newsIdList.clear();
			 SqliteControl sqliteControl= new SqliteControl(NewsTitleActivity.this);
			 List<NewsEntity> newsList =new ArrayList<NewsEntity>();
			 newsList = sqliteControl.GetNews(selectedcategoryid, PERPAGE*offset);
			 sqliteControl.close();
			 SqliteControl sqliteControl2 = new SqliteControl(NewsTitleActivity.this);
			 newsnum=sqliteControl2.getNewsCount(selectedcategoryid);
			 sqliteControl2.close();
			 if(newsList!=null){
		     for(int i=0;i<newsList.size();i++)
	         {
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("ItemImage", R.drawable.viewnewsdetail);//ͼ����Դ��ID,��ͼƬ����Ϊnull
	            map.put("ItemTitle", newsList.get(i).getTitle());
	            map.put("newsid", newsList.get(i).getId());
	            map.put("categoryid",selectedcategoryid);
	            map.put("time",newsList.get(i).getTime());
	            map.put("comefrom", newsList.get(i).getComefrom());
	            listItem.add(map);
	            newsIdList.add(newsList.get(i).getId());
	         }
			 }
		}else{
			refreshNoNews++;
		}
		return num;
    }
	
	
	
	/**
	 * ˢ������Hander
	 */
	final Handler pullRefreshHadle = new Handler(){
		public void handleMessage(Message msg) {
			int num = msg.getData().getInt("num");
			mPullDownView.onRefreshComplete();//�����ʾˢ�´�����ɺ������ļ���ˢ�½�������
			if(newsnum>listItem.size()){
				mPullDownView.setMore(true);//��������true��ʾ���и�����أ�����Ϊfalse�ײ�������ʾ����
			}else{
				mPullDownView.setMore(false);
			}
			adapter.notifyDataSetChanged();	
			if(num>0){
				Toast.makeText(NewsTitleActivity.this,"����ˢ����"+num+"������",Toast.LENGTH_LONG).show();
			}else{
				switch (refreshNoNews) {
				case 1:
					Toast.makeText(NewsTitleActivity.this,"�ף�û��������Ŷ��",1500).show();
					break;
				case 2:
					Toast.makeText(NewsTitleActivity.this,"�ף����û��������...",1500).show();
					break;
				case 3:
					Toast.makeText(NewsTitleActivity.this,"�����㲻Ҫ��ˢ�ˣ�����û��������...",1500).show();
					break;
				case 4:
					Toast.makeText(NewsTitleActivity.this,"�������еõ��ۣ���DOTA��...",1500).show();
					break;
				default:
					Toast.makeText(NewsTitleActivity.this,"�ú�ѧϰ����Ҫ��ˢ��...",1500).show();
					break;
				}
			}
		}
	};
	
	//����ˢ��
	public void onRefresh() {
		new Thread() {
			public void run() {
				int num = addLists();
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("num",num);
				m.setData(bundle);
				pullRefreshHadle.sendMessage(m);
			}
		}.start();
	}
	
	
	//�ײ����ఴť
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				offset++;
				usertotal=PERPAGE*offset;
				listItem.clear();
				newsIdList.clear();
				 SqliteControl sqliteControl= new SqliteControl(NewsTitleActivity.this);
				 List<NewsEntity> newsList =new ArrayList<NewsEntity>();
				 newsList = sqliteControl.GetNews(selectedcategoryid, PERPAGE*offset);
				 sqliteControl.close();
				 if(newsList!=null){
			     for(int i=0;i<newsList.size();i++)
		         {
		            HashMap<String, Object> map = new HashMap<String, Object>();
		            map.put("ItemImage", R.drawable.viewnewsdetail);//ͼ����Դ��ID,��ͼƬ����Ϊnull
		            map.put("ItemTitle", newsList.get(i).getTitle());
		            map.put("newsid", newsList.get(i).getId());
		            map.put("categoryid",selectedcategoryid);
		            map.put("time",newsList.get(i).getTime());
		            map.put("comefrom", newsList.get(i).getComefrom());
		            listItem.add(map);
		            newsIdList.add(newsList.get(i).getId());
		            
		         }
				 }
				 //�ж��Ƿ��и�������
				 mPullDownView.onLoadMoreComplete();//�����ʾ���ظ��ദ����ɺ������ļ��ظ�����棨���ػ��������������ࣩ
				 if(newsnum>PERPAGE*offset){
					 mPullDownView.setMore(true);
				 }else{
					 mPullDownView.setMore(false);
				 }
				 
				adapter.notifyDataSetChanged();	
			}
		}, 1500);
		
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
				intent.setClass(NewsTitleActivity.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				main_bottom_layout1.setSelected(false);
				NewsTitleActivity.this.finish();
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
			intent.setClass(NewsTitleActivity.this, UserInfo.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout2.setSelected(false);
			NewsTitleActivity.this.finish();
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
			Intent intent = new Intent();
			intent.setClass(NewsTitleActivity.this, VisionActivity.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout3.setSelected(false);
			NewsTitleActivity.this.finish();
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
			intent.setClass(NewsTitleActivity.this, SinaWeiboWebView.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			overridePendingTransition(R.anim.fade,R.anim.fade);
			main_bottom_layout4.setSelected(false);
			NewsTitleActivity.this.finish();
		}
	};
	
	
	
	/**
	 * ���·��ؼ�	
	 */
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	    	 Intent intent = new Intent();
	    	 intent.setClass(NewsTitleActivity.this,ViewFlipperActivity.class);
	    	 startActivity(intent);
	    	 overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	    	 NewsTitleActivity.this.finish();
	         return false;   
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	
	
	
	
	 @Override  
	    protected void onRestart() {  
	        super.onRestart();  
	        refreshNoNews=1;
	    }  
	    @Override  
	    protected void onResume() {  
	        super.onResume();   
	        refreshNoNews=1;
	    }  
	    @Override  
	    protected void onPause() {  
	        super.onPause();    
	        refreshNoNews=1;
	        System.gc();
	    }  
	    @Override  
	    protected void onStop() {  
	        super.onStop();  
	        refreshNoNews=1;
	        System.gc();
	    }  
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();
	        refreshNoNews=1;	
	        System.gc();
	    }  
	
		
}
