package com.swjtu.youthapp;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SinaWeiboWebView extends Activity{
	private static int num=0;//�ж���ʾ�Ի�����������
	 /** Called when the activity is first created. */
	private WebView wv;
	private ProgressDialog pd;
	private Handler handler;
	private long exitTime = 0;
	//�ײ�������
	private LinearLayout main_bottom_layout1,main_bottom_layout2,main_bottom_layout3,main_bottom_layout4;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sina_weibo);
        init();//ִ�г�ʼ������
        String url = "http://m.weibo.com/swjtuyouthmedia";
        handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//����һ��Handler�����ڴ��������߳���UI��ͨѶ
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case 0:
    	        	if(num==0){
    	        		pd.show();//��ʾ���ȶԻ��� 
        	        	pd.getWindow().setGravity(Gravity.CENTER);
        	        	num++;
    	        	}
    	        	break;
    	        case 1:
    	        	pd.hide();//���ؽ��ȶԻ��򣬲���ʹ��dismiss()��cancel(),�����ٴε���show()ʱ����ʾ�ĶԻ���СԲȦ���ᶯ��
    	        	break;
    	        }
    	      }
    	      super.handleMessage(msg);
    	    }
        };
        loadurl(wv,url);
        
        //�жϵײ��������Ƿ񱻴���
        boolean clickble = getIntent().getBooleanExtra("clickble", true);
    	// �ײ�������
		main_bottom_layout1 = (LinearLayout) findViewById(R.id.main_bottom_layout1_ly);
		main_bottom_layout1.setOnClickListener(clickListener_layout1);

		main_bottom_layout2 = (LinearLayout) findViewById(R.id.main_bottom_layout2_ly);
		main_bottom_layout2.setOnClickListener(clickListener_layout2);

		main_bottom_layout3 = (LinearLayout) findViewById(R.id.main_bottom_layout3_ly);
		main_bottom_layout3.setOnClickListener(clickListener_layout3);

		main_bottom_layout4 = (LinearLayout) findViewById(R.id.main_bottom_layout4_ly);
		main_bottom_layout4.setOnClickListener(clickListener_layout4);
		main_bottom_layout4.setSelected(clickble);
        
    }
	
	
	/*
	 * ��ʼ��
	 */
    public void init(){
    	wv=(WebView)findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);//����JS
        wv.setScrollBarStyle(0);//���������Ϊ0���ǲ������������ռ䣬��������������ҳ��
        wv.setWebViewClient(new WebViewClient(){   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadurl(view,url);//������ҳ
            	
                return true;   
            }//��д�������,��webview����
 
        });
        wv.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//������ȸı������ 
             	if(progress==100){
            		handler.sendEmptyMessage(1);//���ȫ������,���ؽ��ȶԻ���
            	}else{
            		pd.setMessage("ҳ������У����Ժ�..."+progress + "%");
            	}   
                super.onProgressChanged(view, progress);   
            }   
        });
    	pd=new ProgressDialog(SinaWeiboWebView.this);
    	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       
        
    }
    
    
    
    
    
    public void loadurl(final WebView view,final String url){
    	new Thread(){
        	public void run(){
        		handler.sendEmptyMessage(0);
        		view.loadUrl(url);//������ҳ
        	}
        }.start();
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
				intent.setClass(SinaWeiboWebView.this, ViewFlipperActivity.class);
				intent.putExtra("clickble", true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade,R.anim.fade);
				main_bottom_layout1.setSelected(false);
				SinaWeiboWebView.this.finish();
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
			intent.setClass(SinaWeiboWebView.this, UserInfo.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout2.setSelected(false);
			SinaWeiboWebView.this.finish();
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
			intent.setClass(SinaWeiboWebView.this, VisionActivity.class);
			intent.putExtra("clickble", true);
			startActivity(intent);
			main_bottom_layout3.setSelected(false);
			SinaWeiboWebView.this.finish();
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
		}
	};
    
    
    
		/**
	    * �˳�ȷ��
	    */
	public void ConfirmExit(){
	    	AlertDialog.Builder ad=new AlertDialog.Builder(SinaWeiboWebView.this);
	    	ad.setTitle("�˳�");
	    	ad.setMessage("���Ҫ�뿪?��������...");
	    	ad.setPositiveButton("ȷ���뿪", new DialogInterface.OnClickListener() {//�˳���ť
				public void onClick(DialogInterface dialog, int i) {
					// TODO Auto-generated method stub
					SinaWeiboWebView.this.finish();//�ر�activity
	 
				}
			});
	    	ad.setNegativeButton("����һ���",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int i) {
					//���˳�����ִ���κβ���
				}
			});
	    	ad.show();//��ʾ�Ի���
	    }
	
    /*
     * ��׽���ؼ�(non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {   
            wv.goBack();   
            return true;   
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
        	ConfirmExit();//���˷��ؼ������Ѿ����ܷ��أ���ִ���˳�ȷ��
        	return true; 
        }   
        return super.onKeyDown(keyCode, event);   
    }

}
