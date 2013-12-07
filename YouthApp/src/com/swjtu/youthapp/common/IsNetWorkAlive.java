package com.swjtu.youthapp.common;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
/**
 * �ж������Ƿ���ͨ
 * */
public class IsNetWorkAlive {
	boolean success=false;//��·��ͨ��飻
	private Context context;
	public IsNetWorkAlive(Context context){
		this.context=context;
	}
	public boolean isNetAlive(){
			ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
	        //mobile 3G Data Network
	        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
	        //wifi
	        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
	        if(mobile.toString().equals("CONNECTED")){
	        	success=true;
	        }
	        if(wifi.toString().equals("CONNECTED")){
	        	success=true;
	        }
	        return success;
	}
	
}
