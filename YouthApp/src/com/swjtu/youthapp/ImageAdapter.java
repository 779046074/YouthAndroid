package com.swjtu.youthapp;
import java.io.File;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
public class ImageAdapter extends BaseAdapter{
	private Map<String, Bitmap> cache;
	private List<String> imgUrl;   
    private Context context;   
       
    public ImageAdapter(Context context,List<String> imgUrl,Map<String,Bitmap> cache){   
        this.imgUrl = imgUrl;   
        this.context = context;
        this.cache=cache;
    }   
  
    public int getCount() {   
        return imgUrl.size();   
    }   
  
    public Object getItem(int position) {   
        return imgUrl.get(position);   
    }   
  
    public long getItemId(int position) {   
        return position;   
    }   
  
    public View getView(final int position, View convertView, ViewGroup parent) {   
        ImageView imageView = new ImageView(context);   
        //��ǰ�涨���cache���ҵ���·���µ�ͼƬ   
        Bitmap map = cache.get(imgUrl.get(position));
        imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));    
       imageView.setScaleType(ImageView.ScaleType.FIT_XY);   
      //  imageView.setScaleType(ScaleType.FIT_CENTER);
        if(map == null){   
            //���û���صģ�����ʾĬ��ͼƬ   
            imageView.setImageResource(R.drawable.youth);   
            //���û�м��أ��������߳������   
            //((NewsActivity)context).loadImage(imgUrl.get(position));   
        }else{   
            //��������˵ģ������õ�Imageview   
            imageView.setImageBitmap(map);   
        } 
        return imageView;   
    }   

}
