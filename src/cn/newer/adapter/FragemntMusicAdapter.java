package cn.newer.adapter;

import java.util.List;

import cn.newer.R;
import cn.newer.model.Song;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FragemntMusicAdapter extends BaseAdapter {

    private Context mContext ;	
    private List<Song> mdata ;
	private LayoutInflater layoutinflater;
    
	public FragemntMusicAdapter(Context pContext, List<Song> listSong) {
		mContext = pContext ;
		mdata = listSong ;
		layoutinflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		return mdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View view = convertView ;
		 HolderView holder ;
		 if(view == null){
			    holder = new HolderView();
			    view = layoutinflater.inflate(R.layout.fragment_music_item, null) ;
			    holder.tv_musicname =(TextView)view.findViewById(R.id.tv_musicname) ;
			    holder.tv_art =(TextView)view.findViewById(R.id.tv_ari) ;
			    holder.tv_duration =(TextView)view.findViewById(R.id.tv_durction) ;
			    view.setTag(holder) ;
		 }else{
			 holder = (HolderView) view.getTag() ;
		 }
		 
		 holder.tv_musicname.setText(mdata.get(position).getTitle());
		 holder.tv_art.setText(mdata.get(position).getArtist());
		 holder.tv_duration.setText(frommatTime(mdata.get(position).getDurtion()));

		return view;
	}
	
	private static class HolderView {
		TextView tv_musicname ;
		TextView tv_art ;
		TextView tv_duration ;
		
	}
	
	private String frommatTime(long date) {
		date /=1000 ;
		return String.format("%d:%02d",date/60 , date%60);
	}


}
