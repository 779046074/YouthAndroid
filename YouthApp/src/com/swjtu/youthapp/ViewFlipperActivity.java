package com.swjtu.youthapp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swjtu.youthapp.common.DeleteFile;
import com.swjtu.youthapp.common.LoadBitmapFromLocal;
import com.swjtu.youthapp.data.SqliteControl;
import com.swjtu.youthapp.data.UpdateSqliteDataFromServer;
import com.swjtu.youthapp.po.CategoryEntity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
/**
 * Androidʵ�����һ���Ч��
 * @Description: Androidʵ�����һ���Ч��
 * @Author zhibinzeng
 * @Date 2012-11-22 ����10:44:04
 * @Version V1.0
 */
public class ViewFlipperActivity extends Activity
{	
	//������ͼ·��
	private final static String NewsImagePath = "/YouthAppData/images/newsimg/";
	//�Ӿ�ͼƬ·��
	private final static String VisionImagePath = "/YouthAppData/images/vision/";
	private  int dialogTip;
	private long exitTime = 0;
	public static final String TAG="ViewFlipperActivity";
	private ViewFlipper flipper;//��ҳ��ĿչʾFlipper
	private ViewFlipper main_pic_Fliper;//��ҳ����ͼƬչʾFlipper
	private GestureDetector detector;
	private GestureDetector main_pic_Detector;
	private LayoutInflater mInflater;
	private List<GridInfo> list=null;
	private List<CategoryEntity> categorylist=null;
	private final int SHOW_NEXT = 0011;//��ҳ����ͼƬ��ز���
	private boolean showNext = true;//��ҳ����ͼƬ��ز���
	private boolean isRun = true;//��ҳ����ͼƬ��ز���
	private int main_pic_currentPage = 0;
	private int mCurrentPage=0;
	private int mTotlePage;
	private boolean isDeleteMode=false;
	private ImageButton btnAdd;//������ҳ���ŷ��ఴť
	private ImageButton btnclear;//���������Ϣ
	private ImageButton btnRefresh;
	private TextView date_TextView;//��ʾ����
	int mCurrentX;
	int mCurrentY;
	private String InitConfig="initConfig";//��ʼ�����ļ���
	private String count="";//��־�Ƿ��һ������
	private ProgressDialog myDialog;// ����ProgressDialog���ͱ���
	private ProgressDialog fetchFirstDataDialog;// ����ProgressDialog���ͱ���
	//��ҳ�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	//������ͼƬ����
	public static Map<String, Bitmap> filpperPictureCache = new HashMap<String, Bitmap>();
	//��ĿͼƬ����
	public static Map<String, Bitmap> categoryBitmapCache = new HashMap<String, Bitmap>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.main);
		
		//ע���������Ӽ���
		IntentFilter filter = new IntentFilter();        
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateReceiver, filter);
		
		//��ȡ������ʾ�Ĵ���
		SharedPreferences sPreferences = getSharedPreferences("networktip",0 );
		dialogTip=sPreferences.getInt("networktips", 1);

		String string=getInitData();
		if(string.equals("first")){//��һ������
			firstDataFetch();//��һ������ʱ����ϵͳ���ݿ�
		}
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipperMain);
		detector = new GestureDetector(categoryGestureListener);
		flipper.setOnTouchListener(CategoryonTouchListener);
		flipper.setLongClickable(true);
		date_TextView = (TextView) findViewById(R.id.home_date_tv);
	    date_TextView.setText(getDate());//������ҳ��ʾ������
		btnAdd=(ImageButton)findViewById(R.id.imageButtonAdd);
		btnAdd.setOnClickListener(new btbAddListener());
		btnRefresh=(ImageButton)findViewById(R.id.imageButtonRefresh);
		btnRefresh.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				refreshData();//ˢ��Category����
				//updateCategoryImage();//�����������ͼƬ
			}
		});
		
		//�������
		btnclear=(ImageButton)findViewById(R.id.imageButtonClear);
		btnclear.setOnClickListener(btnClearClickListener);
		
		SqliteControl sqliteControl2 = new SqliteControl(this);
		categorylist=sqliteControl2.getCategoryOrder();//���û����ı��л�ȡ����
		sqliteControl2.close();
		
		if(list!=null){
			list.clear();
		}
		list = new ArrayList<GridInfo>();
		
		
		if(categorylist!=null){
			for(int i=0;i<categorylist.size();i++)
			{
				Bitmap bitmap = null;
				try {
					bitmap = setCategoryImage(categorylist.get(i).getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(bitmap!=null){
					list.add(new GridInfo(categorylist.get(i).getName(),categorylist.get(i).getId(),bitmap));
				}
			
			}
		
		}
		
		refreshFlipper();
		
		//��ҳͼƬ����Flipper
		main_pic_Fliper = (ViewFlipper) findViewById(R.id.main_pic_Fliper);
		main_pic_Detector = new GestureDetector(main_pic_OnGestureListener);
		main_pic_Fliper.setOnTouchListener(onTouchListener);
		//main_pic_Fliper.setOnClickListener(onClickListener);
		main_pic_Fliper.setLongClickable(true);
		displayRatio_selelct(main_pic_currentPage);
		thread.start();//��ҳͼƬ�����߳�
		
		//�жϵײ��������Ƿ񱻴���
        boolean clickble = getIntent().getBooleanExtra("clickble", true);
		//��ҳ�ײ�������
		main_bottom_layout1 = (LinearLayout) findViewById(R.id.main_bottom_layout1_ly);
		main_bottom_layout1.setOnClickListener(clickListener_layout1);
		main_bottom_layout1.setSelected(clickble);
		
		main_bottom_layout2 = (LinearLayout) findViewById(R.id.main_bottom_layout2_ly);
		main_bottom_layout2.setOnClickListener(clickListener_layout2);
		
		main_bottom_layout3 = (LinearLayout) findViewById(R.id.main_bottom_layout3_ly);
		main_bottom_layout3.setOnClickListener(clickListener_layout3);
		
		main_bottom_layout4 = (LinearLayout) findViewById(R.id.main_bottom_layout4_ly);
		main_bottom_layout4.setOnClickListener(clickListener_layout4);
		
		//����ͼƬ��ʼ��
		try {
			initFlipperPic(4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//onCreate()��������
	
	
	/**
	 * ��ȡ������Ϣ
	 * @return
	 */
	public String getInitData(){
		SharedPreferences sPreferences = getSharedPreferences(InitConfig,0 );
		count=sPreferences.getString("count","first");
		Editor editor = sPreferences.edit();
		editor.putString("count","other");
		editor.commit();
		return count;
	}
	
	
	/**
	 * ��ȡ����
	 * @return
	 */
    private String getDate(){
    	Date date = new Date();
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	String[] weekDays = {"������", "����һ", "���ڶ�", "������", "������", "������", "������"};
    	int w = c.get(Calendar.DAY_OF_WEEK) - 1 ;
    	if (w < 0) {
			w = 0;
		}
    	String mDate = c.get(Calendar.YEAR)+"��" + (c.get(Calendar.MONTH)+1)+ "��" + c.get(Calendar.DATE) + "��" + weekDays[w];
    	return mDate;
    }
    
	/**
	 *�����������ͼƬ����������и�������ʾ����û������ʾĬ�ϵ� 
	 * @throws IOException 
	 */
	public Bitmap setCategoryImage(int category) throws IOException{
		Bitmap mBitmap=null;
		SqliteControl sqliteControl = new SqliteControl(this);
		String sqlNewsImage="select sdpath from newscategoryimage where categoryid=?";
		SQLiteDatabase db=sqliteControl.getDatabase();
		Cursor cursor = db.rawQuery(sqlNewsImage, new String[]{String.valueOf(category)});
		cursor.moveToFirst();
		sqliteControl.close();
		if(cursor.getCount()==0){
			//��������ͼƬ�򷵻�Ĭ��ͼƬ
			 mBitmap = categoryBitmapCache.get("column_default");
			 if(mBitmap==null){
				 InputStream is = getResources().openRawResource(R.drawable.column_default);  
		         mBitmap = BitmapFactory.decodeStream(is);
		         categoryBitmapCache.put("column_default",mBitmap);
			 }
		}else{
			mBitmap = categoryBitmapCache.get(cursor.getString(0));
			if(mBitmap==null){
				LoadBitmapFromLocal bitmapFromLocal = new LoadBitmapFromLocal();
				mBitmap=bitmapFromLocal.LoadBitmap(cursor.getString(0));
				categoryBitmapCache.put(cursor.getString(0),mBitmap);
				//Log.d(cursor.getString(0),cursor.getString(0));
			}
		}
		cursor.close();
		if(mBitmap==null){
			//��������ͼƬ�򷵻�Ĭ��ͼƬ
			 mBitmap = categoryBitmapCache.get("column_default");
			 if(mBitmap==null){
				 InputStream is = getResources().openRawResource(R.drawable.column_default);  
		         mBitmap = BitmapFactory.decodeStream(is);
		         categoryBitmapCache.put("column_default",mBitmap);
			 }
		}
		return mBitmap;
	}
	
	/**
	 * btnAdd�ĵ���¼�
	 * @author Administrator
	 */
	class btbAddListener implements OnClickListener{
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(ViewFlipperActivity.this,CategoryOrderActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.fade);  
		ViewFlipperActivity.this.finish();
		}
	}
	
	
	/**
	 * �����������ͼƬ�߳�
	 */
	public void updateCategoryImage(){
		new Thread(){
			public void run() {
				//��ȡ���ݿ����������
				SqliteControl sqliteControl = new SqliteControl(ViewFlipperActivity.this);
				List<CategoryEntity>categoryList=sqliteControl.GetCategories();
				sqliteControl.close();
				for (CategoryEntity categoryEntity : categoryList) {
					UpdateSqliteDataFromServer dataFromServer = new UpdateSqliteDataFromServer(ViewFlipperActivity.this);
					dataFromServer.updateCategoryImage(categoryEntity.getId());
				}
			}
		}.start();
	}
	
	
	
	/**
	 * ��һ������ʱ��ȡ����Hander
	 */
	final Handler firstDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			refreshFlipper();//����������UI
		}
	};
	
	
	/**
	 * ��һ������ʱ��ȡ����
	 */
		public void firstDataFetch(){
			fetchFirstDataDialog = ProgressDialog.show(ViewFlipperActivity.this, "��ӭʹ����˼", "���ڳ�ʼ������...",
					true);
			fetchFirstDataDialog.getWindow().setGravity(Gravity.CENTER); //������ʾ�������ݶԻ���
			fetchFirstDataDialog.setCancelable(true);//�����ؼ�ȡ����ʾ
			//�����̼߳�������
			new Thread(){
				public void run() {
					//�������ݿ�
					UpdateSqliteDataFromServer updateSqliteDataFromServer = new UpdateSqliteDataFromServer(ViewFlipperActivity.this);
					updateSqliteDataFromServer.UpdateCategory();
					updateCategoryImage();//�����������ͼƬ
					//��ʼ�û����ı�
					updateSqliteDataFromServer.initCategoryOrder(6);
					list.clear();
					categorylist.clear();
					SqliteControl sqliteControl3 = new SqliteControl(ViewFlipperActivity.this);
					categorylist=sqliteControl3.getCategoryOrder();//���û����ı��л�ȡ����
					sqliteControl3.close();
					if(categorylist!=null){
						
						for(int i=0;i<categorylist.size();i++)
						{
							Bitmap bitmap = null;
							try {
								bitmap = setCategoryImage(categorylist.get(i).getId());
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(bitmap!=null){
								list.add(new GridInfo(categorylist.get(i).getName(),categorylist.get(i).getId(),bitmap));
							}
							
						}
						
					}
					updateNewsFromServer();//������������
					Message m = new Message();
					firstDataHandler.sendMessage(m);
					fetchFirstDataDialog.dismiss();
				}
			}.start();
			
		}
	
	
	
		
		
		
	/**
	 * ˢ������Hander
	 */
	final Handler listHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (list.size() == 0) {
				return;
			}
			refreshFlipper();
			updateCategoryImage();//�����������ͼƬ
		}
	};
	
	
	
	
	
	/**
	 * ˢ������
	 */
	public void refreshData(){
		myDialog = ProgressDialog.show(ViewFlipperActivity.this, "�ף����Ե�һ��Ŷ...", "����Ŭ����������...",
				true);
		myDialog.getWindow().setGravity(Gravity.CENTER); //������ʾ�������ݶԻ���
		myDialog.setCancelable(true);
		new Thread() {
			public void run() {
				try {
					//�������ݿ�
					UpdateSqliteDataFromServer updateSqliteDataFromServer = new UpdateSqliteDataFromServer(ViewFlipperActivity.this);
					updateSqliteDataFromServer.UpdateCategory();
					//Thread.sleep(2000);//ֹͣ���룬ģ�����
					list.clear();
					categorylist.clear();
					SqliteControl sqliteControl3 = new SqliteControl(ViewFlipperActivity.this);
					categorylist=sqliteControl3.getCategoryOrder();//���û����ı��л�ȡ����
					sqliteControl3.close();
					if(categorylist!=null){
						for(int i=0;i<categorylist.size();i++)
						{
						Bitmap bitmap = null;
						try {
							bitmap = setCategoryImage(categorylist.get(i).getId());
							} catch (IOException e) {
							e.printStackTrace();
							}
							list.add(new GridInfo(categorylist.get(i).getName(),categorylist.get(i).getId(),bitmap));
							}
					
		
					}
					
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

	
	
	
	
	/**
	 * ˢ��������
	 */
	public void refreshFlipper()
	{
		flipper.removeAllViews();
		refreshAdapterAll();
		//flipper.setDisplayedChild(0);��ǰ��ʾ�ڼ�����View
		for (int i = 0; i <flipper.getChildCount(); i++) {
			final GridView gv = (GridView) flipper.getChildAt(i);
			gv.setOnItemLongClickListener(GridViewLongClickListener);
			gv.setOnItemClickListener(GridViewOnItemClickListener);
			gv.setOnTouchListener(CategoryonTouchListener);
			gv.setTag(i);
		}
	}

	
	
	
	
	/**
	 * ˢ����ҳ��ĿFlipperAdapter��������
	 */
	
	private void refreshAdapterAll(){
		 mTotlePage=list.size();
	        for(int i=0;i<Math.ceil(list.size()/4.0);i++)
	        {
	    		GridView gridview=(GridView) mInflater.inflate(R.layout.gridview, null);
	    		flipper.addView(gridview);
	    		GridAdapter adapter = new GridAdapter(this);
	            int end=4*i+4;
	            if(end>list.size()) end=list.size();
	            adapter.setList(list.subList(4*i, end));
	            gridview.setAdapter(adapter);
	        }
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//return this.detector.onTouchEvent(event);
		return false;
	}

    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
//    	detector.onTouchEvent(ev);  
        return super.dispatchTouchEvent(ev);  
    } 
    

    


	/**
	 * �û�������ҳ ��ʾɾ����ť
	 */
    private OnItemLongClickListener GridViewLongClickListener = new OnItemLongClickListener() {
    	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
    			long id) {
    		if (isDeleteMode==true)
    			return false;
    		for (int i = 0; i < flipper.getChildCount(); i++) {
    			GridView gv = (GridView) flipper.getChildAt(i);
    			gv.clearFocus();
    			for (int j = 0; j < gv.getChildCount(); j++) {
    				ImageButton itemButtonCancel = (ImageButton) gv
    						.getChildAt(j).findViewById(R.id.itemCancel);
    				itemButtonCancel.setVisibility(View.VISIBLE);
    			}
    		}
    		isDeleteMode=true;
    		return false;
    	}
	};
	
	
	
	
	
	/**
	 * ����ָ�����ŷ���
	 */
	private OnItemClickListener GridViewOnItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			int categoryid;
			categoryid=list.get((Integer)flipper.getCurrentView().getTag()*4+arg2).getCategoryid();
			Intent intent=new Intent();
			intent.setClass(ViewFlipperActivity.this, NewsTitleActivity.class);
			String categoryName = list.get((Integer)flipper.getCurrentView().getTag()*4+arg2).getName();//�����������
			intent.putExtra("categoryid", categoryid);
			intent.putExtra("categoryName",categoryName);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			ViewFlipperActivity.this.finish();
		}
		
	};
	


	
	
	
	/**
	 * ��ҳ����ͼƬFlipperTouch������
	 * 
	 */
    private OnTouchListener onTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			//Log.d("��ҳ����ͼƬ����¼�", "��ҳ����ͼƬ����¼�");
			return main_pic_Detector.onTouchEvent(event);
		}
	};
	

	
	
	
	
	/**
	 * ��ҳ����ͼƬFilpper OnGestureListener
	 */
	private OnGestureListener main_pic_OnGestureListener = new OnGestureListener() {
		
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		public void onShowPress(MotionEvent e) {
			
		}
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
		}
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			//Log.d("����ͼƬFlipper �����¼�", "����ͼƬFlipper �����¼�");
			if (e1.getX() - e2.getX()> 50  
	                && Math.abs(velocityX) > 0) {
				showNextView();
				showNext = true;
				return true;
			} else if (e2.getX() - e1.getX() > 50  
	                && Math.abs(velocityX) > 0){
				showPreviousView();
				showNext = false;
				return true;
			}
			return false;
		}
		
		public boolean onDown(MotionEvent e) {
			return false;
		}
	}; 
	
	
	
	
	
	/**
	 *��ҳ����ͼƬ����ʾ��һ��ͼƬ
	 */
	private void showNextView(){
		main_pic_Fliper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		main_pic_Fliper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));		
		main_pic_Fliper.showNext();
		main_pic_currentPage ++;
		if (main_pic_currentPage == main_pic_Fliper.getChildCount()) {
			displayRatio_normal(main_pic_currentPage - 1);
			main_pic_currentPage = 0;
			displayRatio_selelct(main_pic_currentPage);
		} else {
			displayRatio_selelct(main_pic_currentPage);
			displayRatio_normal(main_pic_currentPage - 1);
		}
		//Log.e("currentPage", main_pic_currentPage + "");		
		
	}
	
	
	/**
	 *��ҳ����ͼƬ����ʾǰһ��ͼƬ
	 */
	private void showPreviousView(){
		displayRatio_selelct(main_pic_currentPage);
		main_pic_Fliper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		main_pic_Fliper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		main_pic_Fliper.showPrevious();
		main_pic_currentPage --;
		if (main_pic_currentPage == -1) {
			displayRatio_normal(main_pic_currentPage + 1);
			main_pic_currentPage = main_pic_Fliper.getChildCount() - 1;
			displayRatio_selelct(main_pic_currentPage);
		} else {
			displayRatio_selelct(main_pic_currentPage);
			displayRatio_normal(main_pic_currentPage + 1);
		}
		//Log.e("currentPage", currentPage + "");		
	}
	
	
	
	
	/**
	 * ����ͼƬFlipper����СԲ����ʾ����
	 * @param id
	 */
	private void displayRatio_selelct(int id){
		int[] ratioId = {R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)findViewById(ratioId[id]);
		img.setSelected(true);
	}
	
	
	
	
	
	/**
	 * ����ͼƬFlipper����СԲ����ʾ����
	 * @param id
	 */
	private void displayRatio_normal(int id){
		int[] ratioId = {R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)findViewById(ratioId[id]);
		img.setSelected(false);
	}
	
	
	
	
	/**
	 * ��ҳͼƬ�����߳�
	 */
	Thread thread = new Thread(){
		public void run() {
			// TODO ͼƬ�Զ�����
			while(isRun){
				try {
					Thread.sleep(1000 * 5);
					Message msg = new Message();
					msg.what = SHOW_NEXT;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	};
	
	
	
    Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SHOW_NEXT:
				if (showNext) {
					showNextView();
				} else {
					showPreviousView();
				}
				break;

			default:
				break;
			}
		}
    	
    };
	
	
	
    
	
    /**
	 * ��ҳ��Ŀ��ʾFilpper OnGestureListener
	 */
    private OnGestureListener categoryGestureListener = new OnGestureListener() {
		
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		public void onShowPress(MotionEvent e) {
		}
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
			
		}
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX()>120) {
				flipper.setInAnimation(AnimationUtils.loadAnimation(ViewFlipperActivity.this,
						R.anim.push_left_in_main));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(ViewFlipperActivity.this,
						R.anim.push_left_out_main));
				flipper.showNext();
				mCurrentPage = (mCurrentPage+1)%mTotlePage;      
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				flipper.setInAnimation(AnimationUtils.loadAnimation(ViewFlipperActivity.this,
						R.anim.push_right_in));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(ViewFlipperActivity.this,
						R.anim.push_right_out));
				flipper.showPrevious();
				mCurrentPage = (mCurrentPage-1)%mTotlePage;
				return true;
			}
			return false;
		}
		
		public boolean onDown(MotionEvent ev) {
			mCurrentX=(int) ev.getX();
			mCurrentY=(int) ev.getY();
			if (isDeleteMode==false)
				return false;
			for (int i = 0; i < flipper.getChildCount(); i++) {
				GridView gv = (GridView) flipper.getChildAt(i);
				for (int j = 0; j < gv.getChildCount(); j++) {
					ImageButton itemButtonCancel = (ImageButton) gv
							.getChildAt(j).findViewById(R.id.itemCancel);
					itemButtonCancel.setVisibility(View.INVISIBLE);
				}
			}
			isDeleteMode = false;
			return false;
		}
	}; 
	
	
	
	

	/**
	 * ��ҳ��ĿFlipperTouch������
	 * 
	 */
    private OnTouchListener CategoryonTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			return detector.onTouchEvent(event);
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
				/*
				Intent intent = new Intent();
				intent.setClass(ViewFlipperActivity.this, MyActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout1.setSelected(false);
				*/
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
				intent.setClass(ViewFlipperActivity.this, UserInfo.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout2.setSelected(false);
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
				intent.setClass(ViewFlipperActivity.this, VisionActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				main_bottom_layout3.setSelected(false);
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
				intent.setClass(ViewFlipperActivity.this, SinaWeiboWebView.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade,R.anim.fade);
				main_bottom_layout4.setSelected(false);
				ViewFlipperActivity.this.finish();
		}
	};
	
	
	
	
	
	/**
	 *������ҳ����ͼƬ����������и�������ʾ����û������ʾĬ�ϵ� 
	 * @throws IOException 
	 */
	public void initFlipperPic(int length) throws IOException{
		Bitmap mBitmap=null;
		SqliteControl sqliteControl = new SqliteControl(this);
		String sqlNewsImage="select sdpath,newsid from newsimage ORDER BY id DESC LIMIT "+length;
		SQLiteDatabase db=sqliteControl.getDatabase();
		Cursor cursor = db.rawQuery(sqlNewsImage, null);
		cursor.moveToFirst();
		sqliteControl.close();
		if(cursor.getCount()==0){
			//��������ͼƬ�򷵻�Ĭ��ͼƬ
			InputStream is = getResources().openRawResource(R.drawable.default_pic);  
	        mBitmap = BitmapFactory.decodeStream(is);
	        for(int i=0;i<main_pic_Fliper.getChildCount();i++){
	        	LinearLayout flipperChildrenLayout = (LinearLayout) main_pic_Fliper.getChildAt(i);
	        	ImageView imageView = (ImageView) flipperChildrenLayout.getChildAt(0);
	        	imageView.setImageBitmap(mBitmap);
	        	imageView.setTag(null);
	        }
		}else{
			int i=0;
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				mBitmap=filpperPictureCache.get(cursor.getString(0));
				if(mBitmap==null){
					LoadBitmapFromLocal bitmapFromLocal = new LoadBitmapFromLocal();
					mBitmap=bitmapFromLocal.LoadBitmap(cursor.getString(0));
					filpperPictureCache.put(cursor.getString(0), mBitmap);
					//Log.d("mbitmap null", "mbitmap null");
				}
				LinearLayout flipperChildrenLayout = (LinearLayout) main_pic_Fliper.getChildAt(i);
	        	ImageView imageView = (ImageView) flipperChildrenLayout.getChildAt(0);
	        	imageView.setImageBitmap(mBitmap);
	        	imageView.setTag(cursor.getInt(1));
	        	imageView.setOnClickListener(Flipper_pic_clickListener);
	        	imageView.setOnTouchListener(onTouchListener);
	        	i++;
			}
		}
		
	}
	
	
	
	/**
	 * �����������ݣ�����ȡ����ͼƬ
	 */
	private void updateNewsFromServer(){
		UpdateSqliteDataFromServer updateNews = new UpdateSqliteDataFromServer(this);
		updateNews.UpdateNews();//����������Ŀ����������
		updateNews.UpdateImage();//��������ͼƬ
		/**�˴������������ţ����һ�ȡ��Щ���ŵ�ͼƬ�����ö���ѭ�������ܺܲ���Ż�**/
	}
	
	
	
	/**
	 * ��ҳ����ͼƬ�����¼�������
	 */
	
	private OnClickListener Flipper_pic_clickListener  = new OnClickListener() {
		public void onClick(View v) {
			// TODO ��������ͼƬ����Ӧ���ŵ���ϸ��Ϣ
			Integer newsid = (Integer)v.getTag();
			SqliteControl sqliteControl = new SqliteControl(ViewFlipperActivity.this);
			SQLiteDatabase database = sqliteControl.getDatabase();
			String queryCategoryIDSQL = "select category from news where id=?";
			Cursor cursor = database.rawQuery(queryCategoryIDSQL,new String[]{String.valueOf(newsid)});
			cursor.moveToFirst();
			Integer categoryid = cursor.getInt(0); 
			cursor.close();
			String queryCategoryNameSQL = "select name from category where id=?";
			Cursor cursor2 = database.rawQuery(queryCategoryNameSQL,new String[]{String.valueOf(categoryid)});
			cursor2.moveToFirst();
			String categoryName = cursor2.getString(0);
			cursor2.close();
			sqliteControl.close();
			
			Intent intent=new Intent();
			intent.setClass(ViewFlipperActivity.this, NewsActivity.class);
			intent.putExtra("categoryid",categoryid);
			intent.putExtra("newsid",newsid);
			intent.putExtra("categoryName", categoryName);
			intent.putIntegerArrayListExtra("newsidlist",null);
			startActivity(intent);	
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			//�ͷű��� newsid=null;categoryid=null;categoryName=null;
			ViewFlipperActivity.this.finish();
			
		}
	};
	
	
	
	/**
	 *������水ť�Ի����ȷ��ѡ��������� 
	 */
	private DialogInterface.OnClickListener btnClearDialogPositiveClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        	fetchFirstDataDialog = ProgressDialog.show(ViewFlipperActivity.this, "��������������...", "���Ե�Ƭ��...",
					true);
			fetchFirstDataDialog.getWindow().setGravity(Gravity.CENTER); //������ʾ�������ݶԻ���
			//�����߳��������
			new Thread(){
				public void run() {
					//������ݿ������ű��⡢�����Լ�ͼƬ��¼
					SqliteControl sqliteControl = new SqliteControl(ViewFlipperActivity.this);
    				sqliteControl.clear();
    				sqliteControl.close();
    				//���SD����������ͼ
    				String filename = android.os.Environment
    						.getExternalStorageDirectory() +NewsImagePath;
    				File databaseFile = new File(filename);
    				if (databaseFile.exists()) {
    					DeleteFile.delete(databaseFile);
    				}
    				//����Ӿ���ĿͼƬ
    				String visionFileName = android.os.Environment
    						.getExternalStorageDirectory() +VisionImagePath;
    				File visionImageFile = new File(visionFileName);
    				if (visionImageFile.exists()) {
    					DeleteFile.delete(visionImageFile);
    				}
    				
    				
    				try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				fetchFirstDataDialog.dismiss();
				}
			}.start();
        }
    };
	
    
    
    /**
     *������水ť�Ի����ȡ��ѡ��������� 
     */
	private DialogInterface.OnClickListener btnClearDialogNeutralClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.cancel();
        }
    };
    
    
	
	/**
	 * ������水ť������
	 */
	private OnClickListener btnClearClickListener = new ImageButton.OnClickListener(){
		public void onClick(View v) {
			AlertDialog dialog = new AlertDialog.Builder(ViewFlipperActivity.this).setTitle("��ȷ�������������?")
        			.setPositiveButton("ȷ��", btnClearDialogPositiveClickListener)
        			.setNeutralButton("ȡ��", btnClearDialogNeutralClickListener).create();
        	 Window window = dialog.getWindow();     
        	 WindowManager.LayoutParams lp = window.getAttributes(); // ����͸����Ϊ0.3      
        	 WindowManager windowManager = getWindowManager();
        	 Display d = windowManager.getDefaultDisplay();  //Ϊ��ȡ��Ļ����   
        	 lp.alpha = 0.9f;  
        	 lp.x=0; 
        	 lp.y=0; //������ʾ������Ϊ��0,0��
        	 lp.width=(int) (d.getWidth() * 0.9);
        	 lp.height=(int)(d.getHeight()*0.4);
        	 window.setAttributes(lp);   
        	dialog.show();
		}
		
	};
	
	
	
	/**
	 * �˵�
	 */
	/*
	 public boolean onCreateOptionsMenu(Menu menu)  
	    {  
	        super.onCreateOptionsMenu(menu);  
	        MenuItem item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Exit");  
	        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()  
	        {  
	            public boolean onMenuItemClick(MenuItem item)  
	            {  
	                System.exit(0);  
	                return true;  
	            }  
	        }); 
	        MenuItem refresh = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "refresh");
	        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					refreshData();//ˢ������
				return true;
				}
			});
	        return true;  
	        
	    } 
	 
	 */
	 
	 /**
	  * �������Ӽ���
	  */
	 BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() 
	 {
	    @Override
	    public void onReceive(Context context, Intent intent)
	    {	boolean success;//��·��ͨ��飻
	        //Log.e(TAG, "����״̬�ı�");
	    	 success = false;
	        //����������ӷ���
	        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	        // State state = connManager.getActiveNetworkInfo().getState();
	        android.net.NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState(); // ��ȡ��������״̬
	        if (android.net.NetworkInfo.State.CONNECTED == state)
	        { // �ж��Ƿ�����ʹ��WIFI����
	        	success = true;
	        }
	        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); // ��ȡ��������״̬
	        if (android.net.NetworkInfo.State.CONNECTED == state) 
	        { // �ж��Ƿ�����ʹ��GPRS����
	            success = true;
	        }
	        
	        if ((!success)&&(dialogTip==0)) 
	        {
	        	AlertDialog dialog = new AlertDialog.Builder(ViewFlipperActivity.this).setTitle("���ǣ���û�����ء�")
	        			.setPositiveButton("��������", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    Intent mIntent = new Intent("/");
	                    ComponentName comp = new ComponentName(
	                            "com.android.settings",
	                            "com.android.settings.WirelessSettings");
	                    mIntent.setComponent(comp);
	                    mIntent.setAction("android.intent.action.VIEW");
	                    startActivityForResult(mIntent,0);  // �����������ɺ���Ҫ�ٴν��в�����������д�������룬�����ﲻ����д
	                }
	            }).setNeutralButton("û����������", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    dialog.cancel();
	                }
	            }).create();
	        	 Window window = dialog.getWindow();     
	        	 WindowManager.LayoutParams lp = window.getAttributes(); // ����͸����Ϊ0.3      
	        	 WindowManager windowManager = getWindowManager();
	        	 Display d = windowManager.getDefaultDisplay();  //Ϊ��ȡ��Ļ����   
	        	 lp.alpha = 0.9f;  
	        	 lp.x=0; 
	        	 lp.y=0; //������ʾ������Ϊ��0,0��
	        	 lp.width=(int) (d.getWidth() * 0.9);
	        	 lp.height=(int)(d.getHeight()*0.4);
	        	 window.setAttributes(lp);   
	        	dialog.show();
	        	SharedPreferences sPreferences = getSharedPreferences("networktip",0 );
	    		Editor editor = sPreferences.edit();
	    		editor.putInt("networktips",1);
	    		editor.commit();//ֻ����һ��
	        } else if((dialogTip==0)&&(success)){
	        	
	        	/**ÿ��������̨��������**/
	    		new Thread(){
	    			public void run(){
	    				updateNewsFromServer();
	    			}
	    		}.start();
	    		
	        	State mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
	 	        State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
	 	       if(mobile.toString().equals("CONNECTED")){
	 	    	  Toast.makeText(ViewFlipperActivity.this,"��������ʹ��GPRS����",1500).show();
		        }
		        if(wifi.toString().equals("CONNECTED")){
		        	 Toast.makeText(ViewFlipperActivity.this,"��������ʹ��WIFI����",1500).show();
		        }
	        	SharedPreferences sPreferences = getSharedPreferences("networktip",0 );
	    		Editor editor = sPreferences.edit();
	    		editor.putInt("networktips",1);
	    		editor.commit();//ֻ����һ��
	        }           
	                
	    }
	 };
	 
	/**
	 * ���·��ؼ�	
	 */
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	         if((System.currentTimeMillis()-exitTime) > 2000){  
	             Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();                                
	             exitTime = System.currentTimeMillis();   
	         } else {
	        	 //�ͷ���ҳ����ͼƬ����
	        	 if(filpperPictureCache!=null){
	        		 for(Bitmap bitmap:filpperPictureCache.values()){
	        			 if(!bitmap.isRecycled() && bitmap != null){
	        				    bitmap.recycle();
	        			 	}
	        		 }
	        	 }
	        	 //�ͷ���ĿͼƬ����
	        	 if(categoryBitmapCache!=null){
	        		 for(Bitmap bitmap:categoryBitmapCache.values()){
	        			 if(!bitmap.isRecycled() && bitmap != null){
	        				    bitmap.recycle();
	        			 	}
	        		 }
	        	 }
	        	 //�ͷ��Ӿ�ͼƬ����
	        	 if(VisionActivity.visionCacheMap!=null){
	        		 for(Bitmap bitmap:VisionActivity.visionCacheMap.values()){
	        			 if(!bitmap.isRecycled() && bitmap != null){
	        				    bitmap.recycle();
	        			 	}
	        		 }
	        	 }
	        	 
	        	 
	        	 android.os.Process.killProcess(android.os.Process.myPid());    //��ȡPID 
	        	 System.exit(0); 
	         }
	         return false;   
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	 
	 @Override
     protected void onDestroy() 
     {
		 // TODO Auto-generated method stub
         unregisterReceiver(mNetworkStateReceiver); //ȡ������
    	 super.onDestroy();
     }



	}