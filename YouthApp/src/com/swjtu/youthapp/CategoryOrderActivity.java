package com.swjtu.youthapp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.swjtu.youthapp.data.SqliteControl;
import com.swjtu.youthapp.po.CategoryEntity;
import com.swjtu.youthapp.widget.PullDownListView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryOrderActivity extends Activity {
	private ArrayList<HashMap<String, Object>> listItem;
	private ListView categoryOrderListView;
	private SimpleAdapter listItemAdapter;
	//�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	private Button categoryOrderBackBtn; //���ذ�ť
		
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.categoryorder);
		categoryOrderListView = (ListView) findViewById(R.id.categorytitlelist);
		initCategoryTitle();
		// �ײ�������
		main_bottom_layout1 = (LinearLayout) findViewById(R.id.main_bottom_layout1_ly);
		main_bottom_layout1.setOnClickListener(clickListener_layout1);

		main_bottom_layout2 = (LinearLayout) findViewById(R.id.main_bottom_layout2_ly);
		main_bottom_layout2.setOnClickListener(clickListener_layout2);

		main_bottom_layout3 = (LinearLayout) findViewById(R.id.main_bottom_layout3_ly);
		main_bottom_layout3.setOnClickListener(clickListener_layout3);

		main_bottom_layout4 = (LinearLayout) findViewById(R.id.main_bottom_layout4_ly);
		main_bottom_layout4.setOnClickListener(clickListener_layout4);
		
		categoryOrderBackBtn = (Button) findViewById(R.id.categoryOrderBackBtn);
		categoryOrderBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO go back to main Activity
				Intent intent = new Intent();
				intent.setClass(CategoryOrderActivity.this,ViewFlipperActivity.class);
				startActivity(intent);
				CategoryOrderActivity.this.finish();
			}
		});
	}
		//���·��ؼ�	
	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //��ȡ back��
	    		Intent intent =new Intent();
				intent.setClass(CategoryOrderActivity.this,ViewFlipperActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.view_push_down_out_in, R.anim.view_push_down_in_out);//activity�л�����
				CategoryOrderActivity.this.finish();
	    	}
	    	return true;
	  }
	  
	  
	public void initCategoryTitle(){
		  listItem = new ArrayList<HashMap<String, Object>>();
		 List<CategoryEntity>categoryOrderList=new ArrayList<CategoryEntity>();
		 List<CategoryEntity>categoryUnOrderList=new ArrayList<CategoryEntity>();
		 SqliteControl sqliteControl = new SqliteControl(this);
		 categoryOrderList=sqliteControl.getCategoryOrder();
		 categoryUnOrderList=sqliteControl.getUnOrderCategories();
		 sqliteControl.close();
		 if(categoryOrderList!=null){
		     for(int i=0;i<categoryOrderList.size();i++)
	         {
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("categoryOrderImage", R.drawable.order_finish);//ͼ����Դ��ID,��ͼƬ����Ϊnull
	            map.put("categoryOrderTitle", categoryOrderList.get(i).getName());
	            map.put("order",1);//�Ѿ�������order=1
	            map.put("categoryid",categoryOrderList.get(i).getId());
	            listItem.add(map);
	         }
			 }
		 if(categoryUnOrderList!=null){
		     for(int i=0;i<categoryUnOrderList.size();i++)
	         {
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("categoryOrderImage", R.drawable.order);//ͼ����Դ��ID,��ͼƬ����Ϊnull
	            map.put("categoryOrderTitle", categoryUnOrderList.get(i).getName());
	            map.put("order",0);//�Ѿ�������order=1
	            map.put("categoryid",categoryUnOrderList.get(i).getId());
	            listItem.add(map);
	         }
			 }
		 
		  //������������Item�Ͷ�̬�����Ӧ��Ԫ��
	     listItemAdapter = new SimpleAdapter(this,listItem,//����Դ 
	            R.layout.categoryitem,//ListItem��XMLʵ��
	            //��̬������ImageItem��Ӧ������        
	            new String[] {"categoryOrderImage","categoryOrderTitle"}, 
	            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
	            new int[] {R.id.categoryOrderImage,R.id.categoryOrderTitle}
	     );   
	     //��Ӳ�����ʾ
	     categoryOrderListView.setAdapter(listItemAdapter);  
	     categoryOrderListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				int order;
				int categoryid;
				String title;
				HashMap<String, Object> data = (HashMap<String, Object>)categoryOrderListView.getItemAtPosition(position);
				order=(Integer) data.get("order");
				categoryid=(Integer) data.get("categoryid");
				title=(String) data.get("categoryOrderTitle");
				if(order==0){
					//���δ���ĵķ���
					SqliteControl sqliteControl = new SqliteControl(CategoryOrderActivity.this);
					sqliteControl.InsertIntoCategoryOrder(null, categoryid);
					sqliteControl.close();
					//�����Ƕ�̬�޸�Listview��Map��ֵ
					listItem.remove(position);//�Ƴ�ָ��λ�õ�ֵ
					//����һ���µ�list��
					 HashMap<String, Object> map = new HashMap<String, Object>();
					 //������ֵ
					  map.put("categoryOrderImage", R.drawable.order_finish);//ͼ����Դ��ID,��ͼƬ����Ϊnull
			          map.put("categoryOrderTitle", title);
			          map.put("order",1);//�Ѿ�������order=1
			          map.put("categoryid",categoryid);
			        //���µ�ֵ���뵽list��ָ��λ��
			          listItem.add(position, map);
			     	 // ֪ͨ�������������Ѿ��ı�
			          listItemAdapter.notifyDataSetChanged();
				}else{
					//����Ѿ����Ĺ��ķ���
					SqliteControl sqliteControl = new SqliteControl(CategoryOrderActivity.this);
					boolean flag=sqliteControl.deleteCategoryOrder(categoryid);
					if(flag){
						//�����Ƕ�̬�޸�Listview��Map��ֵ
						listItem.remove(position);//�Ƴ�ָ��λ�õ�ֵ
						//����һ���µ�list��
						HashMap<String, Object> map = new HashMap<String, Object>();
						//������ֵ
						map.put("categoryOrderImage", R.drawable.order);//ͼ����Դ��ID,��ͼƬ����Ϊnull
				        map.put("categoryOrderTitle", title);
				        map.put("order",0);//�Ѿ�������order=1
				        map.put("categoryid",categoryid);
				        //���µ�ֵ���뵽list��ָ��λ��
				        listItem.add(position, map);
				     	// ֪ͨ�������������Ѿ��ı�
				        listItemAdapter.notifyDataSetChanged();
					}
				}
			}
		});
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
				intent.setClass(CategoryOrderActivity.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade,R.anim.fade);
				main_bottom_layout1.setSelected(false);
				CategoryOrderActivity.this.finish();
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
			intent.setClass(CategoryOrderActivity.this, UserInfo.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout2.setSelected(false);
			CategoryOrderActivity.this.finish();
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
			intent.setClass(CategoryOrderActivity.this, VisionActivity.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout3.setSelected(false);
			CategoryOrderActivity.this.finish();
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
			intent.setClass(CategoryOrderActivity.this, SinaWeiboWebView.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			overridePendingTransition(R.anim.fade,R.anim.fade);
			main_bottom_layout4.setSelected(false);
			CategoryOrderActivity.this.finish();
		}
	};
	
	

}
