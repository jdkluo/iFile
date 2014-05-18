package cn.newer.adapter;
import cn.newer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

    private Context mContext ;	 
    private int[] mImage ;
    private String[] mData ;
	private LayoutInflater mlayoutinflater;
	
	public MyAdapter(Context context , int[] image, String[] Data) {
		// TODO Auto-generated constructor stub
		mContext = context ;
		mImage = image ;
		mData = Data ;
		mlayoutinflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return  mData.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub 
		return mData[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView ;
		HolderView holder ;
		if(view ==null){
			view = mlayoutinflater.inflate(R.layout.drawer_list_item,null) ;  // 减少xml的寻找和解析时间 
			holder = new HolderView() ;
           
            holder.iv =(ImageView) view.findViewById(R.id.iv_drawer) ;
            holder.tv = (TextView)view.findViewById(R.id.drawer_item_name) ; 
            
            view.setTag(holder);
		}else{
			holder = (HolderView) view.getTag() ;  
		}
		holder.iv.setImageResource(mImage[position]);
		holder.tv.setText(mData[position]);
		
		return view;

	}
	
	private class HolderView{
		
		ImageView iv ;
		TextView tv ;
	}

}
