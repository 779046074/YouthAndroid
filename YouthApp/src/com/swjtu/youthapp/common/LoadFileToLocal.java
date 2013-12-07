package com.swjtu.youthapp.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * �����ļ��������ر���·��
 * @author Administrator
 *
 */
public class LoadFileToLocal {
	public String LoadFile(String url,String ImageSdPath){//����ͼƬ�洢�ı���·��
		//ͼƬ����
		String filename = url.substring(url.lastIndexOf("/")+1,url.length());
		  try {   
    		  File Path = new File(android.os.Environment.getExternalStorageDirectory()//�����ļ���
						+ ImageSdPath);
    		  if (!Path.exists()) {
    			  Path.mkdirs();
    	        }
              //��ָ����·���µ�ͼƬ���浽����
    		  InputStream imageStream = new URL(url).openStream();
              //Bitmap map =BitmapFactory.decodeStream(imageStream);
              File file=new File(android.os.Environment.getExternalStorageDirectory()+ ImageSdPath+filename);
              file.createNewFile();
              OutputStream fos = new FileOutputStream(file);
              
            byte[] buffer = new byte[1024];
  			int count = 0;
  			while ((count = imageStream.read(buffer)) >= 0)
  			{
  				fos.write(buffer, 0, count);
  			}
  			fos.close();
  			imageStream.close();
          } catch (MalformedURLException e) {   
              e.printStackTrace();   
          } catch (IOException e) {   
              e.printStackTrace();   
          }
		return ImageSdPath+filename;//���ر���·��
	}
	
	
	public  static Bitmap getHttpBitmap(String url)
	{
	
		Bitmap bitmap = null;
		try
		{
			
			URL pictureUrl = new URL(url);
			InputStream in = pictureUrl.openStream();
			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		   	opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	        opt.inPurgeable = true;  
	        opt.inInputShareable = true;  
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
			
					
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return bitmap;
		/*
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;   
		opts.inPreferredConfig = Bitmap.Config.RGB_565;   
		opts.inPurgeable = true;  
		opts.inInputShareable = true;  
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, opts);
		opts.inSampleSize =LoadBitmapFromLocal.computeSampleSize(opts,800);       
		opts.inJustDecodeBounds = false;
		Bitmap bitmap=null;
		try {
				bitmap = BitmapFactory.decodeFile(url, opts);
		    } 
		catch (OutOfMemoryError err) {
				bitmap=null;
		    }
		finally{
			return bitmap;
		}
		*/
	}	

}
