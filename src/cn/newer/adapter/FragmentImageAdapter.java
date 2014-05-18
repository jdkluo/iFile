package cn.newer.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class FragmentImageAdapter extends BaseAdapter {

	private Context mContext ;
    private List<HashMap<String, String>> imageData ;
	private GridView mgv ;
    
	public FragmentImageAdapter(Context pContext,List<HashMap<String, String>> bindData,GridView gv) {
		mContext = pContext ;
		imageData = bindData ;
		mgv = gv ;
	}

	@Override
	public int getCount() {
		return imageData.size();
	}

	@Override
	public Object getItem(int position) {
		return imageData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		Bitmap bitmap= null ;
	    ImageView iv ;
	   if(convertView == null){
		    iv = new ImageView(mContext) ;
		    Log.i("float", "adapter2") ;
	   }else{
		   Log.i("float", "adapter3") ;
		   iv = (ImageView) convertView ;
	   }
	   
	    iv.setLayoutParams(new GridView.LayoutParams(200, 180));
	    iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	    String path = imageData.get(position).get("image_data") ;

	    iv.setImageBitmap(lessenUriImage(path));
		return iv;
	}
	
	
	public final static Bitmap lessenUriImage(String path)
	  { 
	   BitmapFactory.Options options = new BitmapFactory.Options(); 
	   options.inJustDecodeBounds = true; 
	   Bitmap bitmap = BitmapFactory.decodeFile(path, options); //此时返回 bm 为空 
	   options.inJustDecodeBounds = false; //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	   int be = (int)(options.outHeight / (float)320); 
	   if (be <= 0) 
	    be = 1;
	   options.inSampleSize = be; //重新读入图片，注意此时已经把 options.inJustDecodeBounds 设回 false 了 
	   bitmap=BitmapFactory.decodeFile(path,options); 
	   int w = bitmap.getWidth(); 
	   int h = bitmap.getHeight(); 
	   return bitmap;
	  }


}
