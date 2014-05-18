package cn.newer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.newer.adapter.FragemntMusicAdapter;
import cn.newer.adapter.FragmentAdapter;
import cn.newer.adapter.FragmentImageAdapter;
import cn.newer.adapter.MyAdapter;
import cn.newer.busniess.CustomOperatFile;
import cn.newer.model.Song;
import cn.newer.utils.CustomFileFilter;
import cn.newer.utils.FileSort;

public class MainActivity extends Activity implements OnItemClickListener {

	private ActionBar mActionBar;           //操作栏
	private DrawerLayout mDrawerLayout;    //抽屉布局
	private ListView mlvDrawer;           //抽屉列表的ListView
	private ActionBarDrawerToggle mActionBarDrawerToggle;    //控制抽屉的开关
	private static Context mContext;             //上下文
	private static HashMap<Integer, File> map;   //保存删除或者复制粘贴的map集合    

	private String[] mDataDrawerLists;            //初始化数据
	private int[] imageData = new int[] { R.drawable.ic_action_mic,R.drawable.ic_action_picture, R.drawable.ic_action_video,
			R.drawable.ic_action_mic, R.drawable.ic_action_mic,
			R.drawable.ic_action_download };
	
	private MyAdapter mMyAdapter;            //抽屉列表上的listView 的adapter
	private static FragmentShowFile framgemenFile;          //管理文件的碎片
	private static FragmentImageFile fragmentImageFile;            //管理图片的碎片
	private boolean isOff = true;

	private static TextView mtvPath;            //显示当前的路径的控件
	private static File currentFilePath;; // 当前显示的路径
	private static File[] mfileData;                  // fragmentFile 显示的数据
	private static FragmentAdapter mFragmentAdapter;        //显示碎片file的adapter
	private static ListView lvfragment;                  //显示文件碎片的ListView

	private static final int DROP_SETTING = 0;
	private static final int DROP_NEW = 1;
	private static final int DROP_HELP = 2;
	private static final int DROP_XIANGQIN = 3;

	private static int count = 0; // 控制KeyDown的退出
	private static String flagpath = null;
	private File files;                     //获取sk卡的文件

	private QueryHandler queryHandler ;
	private static List<Song> listSong ;
	private FragmentMusic fragmentMusic;
        

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = MainActivity.this;
        
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
			    files = Environment.getExternalStorageDirectory();
			    getFileData(files);
			    currentFilePath =  new File("/mnt/sdcard");
		}

		initView();
		initData();

		if (null == savedInstanceState) { 
			framgemenFile = new FragmentShowFile();
			fragmentImageFile = new FragmentImageFile();
			fragmentMusic = new FragmentMusic();
			FragmentManager _FragmentManager = getFragmentManager(); // 获取一个碎片管理器

			FragmentTransaction _FragmentTransaction = _FragmentManager.beginTransaction();           // 事务处理
			_FragmentTransaction.add(R.id.framelayout, framgemenFile); // 添加碎片
			_FragmentTransaction.commit();            // 提交事务
		}
		queryHandler =  new QueryHandler(getContentResolver()) ;
		startQuery() ;  //异步查询音乐
		
		
	}

	public static void getFileData(File pfiles) {
		mfileData =null ;
		mfileData = pfiles.listFiles(new CustomFileFilter()); // 过滤.文件
		mfileData = FileSort.sortFile(mfileData);        // 排序
	}

	private void initData() {      // 初始化数据
		mDataDrawerLists = getResources().getStringArray(R.array.drawer_list2);
		mMyAdapter = new MyAdapter(MainActivity.this, imageData,mDataDrawerLists); // 初始化Adapter
		mlvDrawer.setAdapter(mMyAdapter);

		mlvDrawer.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 设置ListView的选择模式是单选模式
		mlvDrawer.setItemChecked(0, true);         // 设置ListView的默认选项是第一项

		mlvDrawer.setOnItemClickListener(this);    // 设置listView的监听事件
	}

	private void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout); // 获取抽屉布局
		mlvDrawer = (ListView) findViewById(R.id.lv_drawer); // 初始化ListView
		mActionBar = getActionBar();

		mActionBar.setDisplayHomeAsUpEnabled(true); // 设置应用的actionBar上的图标
		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer, R.string.openDrawerContentDescRes,R.string.closeDrawerContentDescRes) {
			@Override
			public void onDrawerOpened(View drawerView) { // 抽屉开关的打开
				super.onDrawerOpened(drawerView);
				if (isOff == true) {
					mActionBar.setTitle(R.string.openDrawerContentDescRes); // 打开时显示的标题内容
					invalidateOptionsMenu(); // 重新绘制
				}
			}

			@Override
			public void onDrawerClosed(View drawerView) { // 抽屉开关的关闭
				super.onDrawerClosed(drawerView);
				if (isOff == true) {
					mActionBar.setTitle(R.string.closeDrawerContentDescRes); // 抽屉关闭时显示的标题
				}
			}
		};
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);    // 设置模式
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);     // 只是抽屉的阴影
		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);    // 设置抽屉的监听事件
	}

	private class MyDropDownListenser implements OnNavigationListener {       // 下拉菜单的监听事件

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			switch (itemPosition) {
			case DROP_SETTING: // 设置
				break;
			case DROP_NEW: // 新建
				break;
			case DROP_HELP: // 帮助
				break;
			case DROP_XIANGQIN: // 详情
				break;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { // 控制开关的打开和关闭
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_list,null);
		final EditText edFileName = (EditText) view.findViewById(R.id.ed_dialog);
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
			    return true;
		} else if (item.getItemId() == R.id.action_new) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("新建");
			builder.setView(view);
			builder.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(DialogInterface dialog, int which) { // 添加文件
						String name = edFileName.getText().toString();
					    if (!name.equals("") && !"".equals(name)) {
						 File file = new File(currentFilePath + "/"+ name);
						  Log.i("float", " 增加："+file.getAbsolutePath()) ;
							if (!file.exists()) {
								file.mkdir();
								getFileData(file.getParentFile());
								framgemenFile.bindDataFile();
								mFragmentAdapter.notifyDataSetChanged(); // 更新数据
								Toast.makeText(mContext, name + "已经增加成功",2000).show();
								
								} else {
									Toast.makeText(mContext, name + "已经存在",2000).show();
								}
							
								dialog.dismiss();

							} 
						}
					});

			builder.setNegativeButton(getString(R.string.dialog_cancle),new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			
			AlertDialog dialog = builder.create();
			dialog.show();

			return false;
		} else {
			return false;
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {   // 同步更新图标
		super.onPostCreate(savedInstanceState);
		mActionBarDrawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_new);
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.drawer_list,android.R.layout.simple_spinner_dropdown_item);
		mActionBar.setListNavigationCallbacks(spinnerAdapter,new MyDropDownListenser());
		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {    // 预处理
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			menu.findItem(R.id.action_settings).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
		mDrawerLayout.closeDrawer(Gravity.START); // 关闭抽屉
		mActionBar.setTitle(mDataDrawerLists[position]); // 更新操作标题栏
		isOff = false;
		switch (position) {
		case 0:            // 激活fileFragment
			FragmentManager managerA = getFragmentManager();
			FragmentTransaction fragmentTransactionA = managerA.beginTransaction();
			framgemenFile = new FragmentShowFile();
			fragmentTransactionA.replace(R.id.framelayout, framgemenFile);
			fragmentTransactionA.commit();
			break;
		case 1:                 // 激活imageFragment
			fragmentImageFile = new FragmentImageFile();
			getFragmentManager().beginTransaction().replace(R.id.framelayout, fragmentImageFile).commit();
			break;
		case 2:
			fragmentMusic = new FragmentMusic() ;
			getFragmentManager().beginTransaction().replace(R.id.framelayout, fragmentMusic).commit();
			break ;
		default:
			break;
		}
	}


	public static class FragmentShowFile extends Fragment implements MultiChoiceModeListener, OnItemClickListener {

		private View view;
		@SuppressWarnings("unused")
		private boolean isOpen = false;
		private boolean isselect = true;
		private File file;

		@Override
		public View onCreateView(LayoutInflater layoutInflater,
				ViewGroup container, Bundle savedInstanceState) {
			view = layoutInflater.inflate(R.layout.framgment_a, container,false);
		    Log.i("float", "11") ;
			lvfragment = (ListView) view.findViewById(R.id.lv_fragment2);
			   Log.i("float", "112") ;
			mtvPath = (TextView) view.findViewById(R.id.pathInfo); // 显示当前路径的信息
			Log.i("float", "22") ;
			bindDataFile();
			
			lvfragment.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); // 设置listView的多选
			lvfragment.setMultiChoiceModeListener(this);
			lvfragment.setOnItemClickListener(this);
			return view;
		}

		public  void bindDataFile() { // 绑定数据
			mFragmentAdapter = new FragmentAdapter(mfileData, mContext);
			lvfragment.setAdapter(mFragmentAdapter);
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) { // 点击项
			if (isselect) {
				map = new HashMap<Integer, File>();
				SparseBooleanArray array = lvfragment.getCheckedItemPositions(); // 稀疏数组
				int sprsesize = array.size();     // 获取选中的长度
				for (int i = 0; i < sprsesize; i++) {
					if (array.valueAt(i)) {
						int index = array.keyAt(i); // 这个获取的选项id,
						file = (File) lvfragment.getAdapter().getItem(index);
						map.put(i, file);
					}
				}
			}

			switch (item.getItemId()) {

			case R.id.action_remove:
				new FileAsyncTask(map.size()).execute(map) ;
				
				mode.finish(); // 关闭模式
				break;
				
			case R.id.action_copy:
				isselect = false ;
				mode.getMenu().findItem(R.id.action_copy).setVisible(false);
				mode.getMenu().findItem(R.id.action_parser).setVisible(true);
				mode.finish(); // 关闭模式
				break;
				
			case R.id.action_parser: // 粘贴
				@SuppressWarnings("rawtypes")
				Iterator iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry entry = (Entry) iterator.next(); // 获取实体对象
					File file = (File) entry.getValue(); // 获取文件
					Log.i("float", "222:"+file.getAbsolutePath()) ;
					try {
						CustomOperatFile.cp(file, new File(flagpath));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				iterator = null;
				
				getFileData(new File(flagpath)) ;
				mFragmentAdapter.refresh(mfileData);
				mFragmentAdapter.notifyDataSetChanged();
				lvfragment.setAdapter(mFragmentAdapter);
			
				mode.finish(); // 关闭模式
			default:
				break;
			}
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) { // 创建菜单选项
			getActivity().getMenuInflater().inflate(R.menu.home_menu, menu); // 添加菜单
			menu.findItem(R.id.action_copy).setVisible(true); // 设置图标的可见性
			mode.setTitle("选中");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) { // 销毁
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) { // 预处理
			menu.findItem(R.id.action_parser).setVisible(true); // 设置粘贴不可见
			return false;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
			int count = lvfragment.getCheckedItemCount(); // 被选中的数目
			mode.setSubtitle(count + "项");

		}

		@Override
		public void onItemClick(AdapterView<?> perant, View view, int position,long id) { // ListView上的点击事件

			File file = mfileData[position]; // 获取文件
			flagpath = file.getPath();
			currentFilePath = file.getParentFile();

			if (!file.isFile()) {
				mtvPath.append("/" + file.getName());
			}

			if (file.isDirectory()) { // 如果是目录
			     getFileData(file) ;
			 	 mFragmentAdapter.refresh(mfileData);
				 mFragmentAdapter.notifyDataSetChanged();
				lvfragment.setAdapter(mFragmentAdapter);
				
			} else {  // 如果是文件

				Uri uri = Uri.fromFile(file);
				String[] projection = { MediaStore.Files.FileColumns.MIME_TYPE };

				Cursor cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), projection,
						MediaStore.Files.FileColumns.DATA + " = ?",
						new String[] { file.getAbsoluteFile().toString() },
						null);

				if (cursor != null) {
					
					cursor.moveToNext();
					String mine = cursor.getString(0);
					Intent _intent = new Intent(Intent.ACTION_VIEW);
					_intent.setDataAndType(uri, mine);
					getActivity().startActivity(_intent);
					
				} else {
					Toast.makeText(getActivity(), "没有此数据类型", 3000).show();
				}

			}

		}

		public void showMessage(String info) {
			Toast.makeText(mContext, info, 2000).show();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentFilePath != null && !currentFilePath.getName().endsWith("mnt")) {
				mfileData = currentFilePath.listFiles(new CustomFileFilter());
				FileSort.sortFile(mfileData);
				currentFilePath = currentFilePath.getParentFile();
				String path = mtvPath.getText().toString(); // 更新路径信息

				if (!path.equals("/SDCard")) {
					int index = path.lastIndexOf("/");
					path = path.substring(0, index);
				}
				if (path != null) {
					mtvPath.setText(path); // 显示文件的路径
					framgemenFile.bindDataFile();
					lvfragment.setAdapter(mFragmentAdapter);
					mFragmentAdapter.notifyDataSetChanged(); // 通知
				} else {
					return true;
				}

			} else {
				
				if (currentFilePath.getPath().equals("/mnt")) {
					if (++count == 2) {
						count = 0;
						this.finish();
					}
					Toast.makeText(mContext, "再次点击将退出程序", 2000).show();
				}
				
			}
		}
		return false;
	}

	public static class FragmentImageFile extends Fragment { // 图片的显示

		private FragmentImageAdapter imageAdapter;
		private GridView gv;

		@SuppressLint("ShowToast")
		@Override
		public View onCreateView(LayoutInflater layouinflater,ViewGroup container, Bundle savedInstanceState) {
			View imageView = layouinflater.inflate(R.layout.fragment_image,container, false);
			gv = (GridView) imageView.findViewById(R.id.gridView);

			initAdapter();
			return imageView;
		}

		public List<HashMap<String, String>> bindDataImage() {
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

			ContentResolver contentResolver = getActivity()
					.getContentResolver(); // 获取ContentResolver

			String[] projection = { MediaStore.Images.Media._ID, 
					MediaStore.Images.Media.DISPLAY_NAME,
					MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE }; // 查询的字段
			String selection = MediaStore.Images.Media.MIME_TYPE + "=?"; // 条件

			String[] selectionArgs = { "image/jpeg" }; // 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
			String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc"; // 排序

			Cursor cursor = contentResolver.query(uri, projection, selection,selectionArgs, sortOrder); // 查询sd卡上的图片

			List<HashMap<String, String>> mImageDara = new ArrayList<HashMap<String, String>>(); // 保存图片的数据
			if (cursor != null) {
				cursor.moveToFirst();
				HashMap<String, String> map = null;
				while (cursor.moveToNext()) {
					map = new HashMap<String, String>();
					map.put("image_id", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
					map.put("image_name",cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
					map.put("image_info", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
					map.put("image_data", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
					mImageDara.add(map);
				}
				cursor.close();
			}

			return mImageDara;
		}

		public void initAdapter() {
			imageAdapter = new FragmentImageAdapter(getActivity(), bindDataImage(),gv);
			gv.setAdapter(imageAdapter);
		}

	}
	
	
	private static class FileAsyncTask extends AsyncTask< HashMap, Integer, Boolean>{
         File filePath = null ;
		 ProgressDialog progressBar;
		 List<File> file = new ArrayList<File>() ;
		public FileAsyncTask(Integer values) {
			progressBar = new ProgressDialog(mContext);
			progressBar.setTitle("删除");
			progressBar.setMessage("正在删除数据....");
			progressBar.setMax(values) ;  //设置进度条的最大值	
			progressBar.setCancelable(false) ;
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) ;  //设置显示的样式
			progressBar.setButton(ProgressDialog.BUTTON_NEUTRAL, "取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					 cancel(false) ;
				}
			});
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.show() ;  //显示进度框
		}
		  
		@Override
		protected void onPostExecute(Boolean result) {      // 执行返回的结果
			super.onPostExecute(result);
            Log.i("float", "postExecute :" + currentFilePath) ;
			getFileData(currentFilePath) ;
			mFragmentAdapter.refresh(mfileData);
			mFragmentAdapter.notifyDataSetChanged();
			lvfragment.setAdapter(mFragmentAdapter);
			
		}
		@Override
		protected Boolean doInBackground(HashMap... params) {
			HashMap map = params[0] ;
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
                Map.Entry entry = (Entry) iter.next() ;
                File files = (File) entry.getValue() ;
                filePath =  files ;
                file.add(files) ; 
				}
			 
			 for(int i= 0 ;i<file.size() ;i++){
				 SystemClock.sleep(1000);
				 CustomOperatFile.rm(file.get(i)) ;
				 publishProgress(i+1) ;             //更新进度条
			 }
			 progressBar.cancel() ;  //取消进度框 
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressBar.setProgress(values[0]);           //显示进度条
		}
		
	}
	
	//****************************异步查询音乐**************************************
	
	private void startQuery() {
	
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ;
		String[] projection  = { 
				MediaStore.Audio.Media._ID,     //歌曲的id
				MediaStore.Audio.Media.TITLE,    //歌曲的名称
                MediaStore.Audio.Media.ARTIST,    //歌曲的歌手名称
                MediaStore.Audio.Media.DURATION,  //歌曲的总时间
                MediaStore.Audio.Media.ALBUM,   //歌曲的专辑
                MediaStore.Audio.Media.DATA ,    //歌曲的路径
                MediaStore.Audio.Media.SIZE,   //歌曲的总的文件大小
                };
		
		queryHandler.startQuery(0, null, uri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	}
	
	private class QueryHandler extends AsyncQueryHandler{

		public QueryHandler(ContentResolver cr) {
			super(cr);
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			
			if(cursor != null && cursor.getCount() >0){

				listSong =  new ArrayList<Song>() ;
				Song song = null ;
				while(cursor.moveToNext()){
					song = new Song() ;
					song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));   //歌曲的名称
					song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));     //作者
					song.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));         //路径
					song.setDurtion(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));  //时间
				
					Log.i("float", song.toString()+"音乐") ;
					listSong.add(song) ;  //添加
				}
			}
			
			super.onQueryComplete(token, cookie, cursor);
		}
	}
	
	
	@SuppressLint("ValidFragment")
	private static class FragmentMusic extends Fragment{    //音乐的显示
		 private View view;
		 private ListView lv_music;

		@SuppressLint("ValidFragment")
		@Override
		public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
				Bundle savedInstanceState) {
			view = layoutInflater.inflate(R.layout.fragment_list, container, false);
			 lv_music = (ListView)view.findViewById(R.id.lv_music);
			 
			 FragemntMusicAdapter musicAdapter = new FragemntMusicAdapter(getActivity(),listSong) ;
			 lv_music.setAdapter(musicAdapter) ;
			 
			 return view ;
		}
		
	}

}
