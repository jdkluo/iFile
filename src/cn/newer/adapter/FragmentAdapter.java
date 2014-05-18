package cn.newer.adapter;
import java.io.File;

import cn.newer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentAdapter extends BaseAdapter {

    private Context mContext ;	 
    private File[] mfileData ;
	private LayoutInflater mlayoutinflater;


	public FragmentAdapter( File[]files ,Context pContext) {
		mContext = pContext ;
		mfileData = files ;
		mlayoutinflater = LayoutInflater.from(mContext);
	}
	
	public void refresh(File[]file){
		mfileData = file ;
		notifyDataSetChanged();
	}
	


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
	  return mfileData ==null? 0: mfileData.length;
	}

	@Override
	public Object getItem(int position) {
	
		return mfileData[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder ;
		View _view = convertView ;
		if(_view ==null){
			
			mViewHolder = new ViewHolder() ;
			_view = mlayoutinflater.inflate(R.layout.drawer_list_item, null) ;
			mViewHolder.mFileImage =(ImageView) _view.findViewById(R.id.iv_drawer) ;
			mViewHolder.mFileName = (TextView) _view.findViewById(R.id.drawer_item_name) ;
			
			_view.setTag(mViewHolder) ;
		}else{
			mViewHolder = (ViewHolder) _view.getTag() ;
		}
		
		/*目录的显示特点*/
		if(mfileData[position].isDirectory() && mfileData[position].canRead()){  
			/*文件夹分为空与非空*/
			if(mfileData[position].listFiles().length == 0 && mfileData[position].listFiles()== null  ){
				mViewHolder.mFileImage.setImageResource(R.drawable.ic_action_collection) ;
				mViewHolder.mFileName.setText(mfileData[position].getName()) ;
			}else{
				mViewHolder.mFileImage.setImageResource(R.drawable.ic_action_collection) ;
				mViewHolder.mFileName.setText(mfileData[position].getName()) ;
			}
			
		 }else{   //文件的处理
			 
			 String _FileName = mfileData[position].getName().toLowerCase() ;
			 
			 if(_FileName.endsWith(".txt")){  //文本显示t
                  mViewHolder.mFileImage.setImageResource(R.drawable.ic_action_download) ;
				  mViewHolder.mFileName.setText(_FileName) ; 
					  
			 }else if(_FileName.endsWith(".png") || _FileName.endsWith(".jpg") ||_FileName.endsWith(".jpeg") ){
				 mViewHolder.mFileImage.setTag(mfileData[position].getAbsolutePath()) ;
				  mViewHolder.mFileImage.setImageResource(R.drawable.ic_action_picture) ;
				 mViewHolder.mFileName.setText(_FileName) ;  
				 
			 }else if(_FileName.endsWith(".mp4")|| _FileName.endsWith(".avi")|| _FileName.endsWith(".3gp")){
				  mViewHolder.mFileImage.setImageResource(R.drawable.ic_action_video) ;
				  mViewHolder.mFileName.setText(_FileName) ;  
				  
			 }else{
				  mViewHolder.mFileImage.setImageResource(R.drawable.ic_action_collection) ;
				  mViewHolder.mFileName.setText(mfileData[position].getName()) ; 
			 }
		}
		
		return _view;
		
	}
	
	
	private static final class ViewHolder{
		private ImageView mFileImage ;
		private TextView mFileName ;
	}
}
