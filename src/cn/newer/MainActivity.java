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

	private ActionBar mActionBar;           //������
	private DrawerLayout mDrawerLayout;    //���벼��
	private ListView mlvDrawer;           //�����б��ListView
	private ActionBarDrawerToggle mActionBarDrawerToggle;    //���Ƴ���Ŀ���
	private static Context mContext;             //������
	private static HashMap<Integer, File> map;   //����ɾ�����߸���ճ����map����    

	private String[] mDataDrawerLists;            //��ʼ������
	private int[] imageData = new int[] { R.drawable.ic_action_mic,R.drawable.ic_action_picture, R.drawable.ic_action_video,
			R.drawable.ic_action_mic, R.drawable.ic_action_mic,
			R.drawable.ic_action_download };
	
	private MyAdapter mMyAdapter;            //�����б��ϵ�listView ��adapter
	private static FragmentShowFile framgemenFile;          //�����ļ�����Ƭ
	private static FragmentImageFile fragmentImageFile;            //����ͼƬ����Ƭ
	private boolean isOff = true;

	private static TextView mtvPath;            //��ʾ��ǰ��·���Ŀؼ�
	private static File currentFilePath;; // ��ǰ��ʾ��·��
	private static File[] mfileData;                  // fragmentFile ��ʾ������
	private static FragmentAdapter mFragmentAdapter;        //��ʾ��Ƭfile��adapter
	private static ListView lvfragment;                  //��ʾ�ļ���Ƭ��ListView

	private static final int DROP_SETTING = 0;
	private static final int DROP_NEW = 1;
	private static final int DROP_HELP = 2;
	private static final int DROP_XIANGQIN = 3;

	private static int count = 0; // ����KeyDown���˳�
	private static String flagpath = null;
	private File files;                     //��ȡsk�����ļ�

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
			FragmentManager _FragmentManager = getFragmentManager(); // ��ȡһ����Ƭ������

			FragmentTransaction _FragmentTransaction = _FragmentManager.beginTransaction();           // ������
			_FragmentTransaction.add(R.id.framelayout, framgemenFile); // �����Ƭ
			_FragmentTransaction.commit();            // �ύ����
		}
		queryHandler =  new QueryHandler(getContentResolver()) ;
		startQuery() ;  //�첽��ѯ����
		
		
	}

	public static void getFileData(File pfiles) {
		mfileData =null ;
		mfileData = pfiles.listFiles(new CustomFileFilter()); // ����.�ļ�
		mfileData = FileSort.sortFile(mfileData);        // ����
	}

	private void initData() {      // ��ʼ������
		mDataDrawerLists = getResources().getStringArray(R.array.drawer_list2);
		mMyAdapter = new MyAdapter(MainActivity.this, imageData,mDataDrawerLists); // ��ʼ��Adapter
		mlvDrawer.setAdapter(mMyAdapter);

		mlvDrawer.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // ����ListView��ѡ��ģʽ�ǵ�ѡģʽ
		mlvDrawer.setItemChecked(0, true);         // ����ListView��Ĭ��ѡ���ǵ�һ��

		mlvDrawer.setOnItemClickListener(this);    // ����listView�ļ����¼�
	}

	private void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout); // ��ȡ���벼��
		mlvDrawer = (ListView) findViewById(R.id.lv_drawer); // ��ʼ��ListView
		mActionBar = getActionBar();

		mActionBar.setDisplayHomeAsUpEnabled(true); // ����Ӧ�õ�actionBar�ϵ�ͼ��
		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer, R.string.openDrawerContentDescRes,R.string.closeDrawerContentDescRes) {
			@Override
			public void onDrawerOpened(View drawerView) { // ���뿪�صĴ�
				super.onDrawerOpened(drawerView);
				if (isOff == true) {
					mActionBar.setTitle(R.string.openDrawerContentDescRes); // ��ʱ��ʾ�ı�������
					invalidateOptionsMenu(); // ���»���
				}
			}

			@Override
			public void onDrawerClosed(View drawerView) { // ���뿪�صĹر�
				super.onDrawerClosed(drawerView);
				if (isOff == true) {
					mActionBar.setTitle(R.string.closeDrawerContentDescRes); // ����ر�ʱ��ʾ�ı���
				}
			}
		};
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);    // ����ģʽ
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);     // ֻ�ǳ������Ӱ
		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);    // ���ó���ļ����¼�
	}

	private class MyDropDownListenser implements OnNavigationListener {       // �����˵��ļ����¼�

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			switch (itemPosition) {
			case DROP_SETTING: // ����
				break;
			case DROP_NEW: // �½�
				break;
			case DROP_HELP: // ����
				break;
			case DROP_XIANGQIN: // ����
				break;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { // ���ƿ��صĴ򿪺͹ر�
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_list,null);
		final EditText edFileName = (EditText) view.findViewById(R.id.ed_dialog);
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
			    return true;
		} else if (item.getItemId() == R.id.action_new) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("�½�");
			builder.setView(view);
			builder.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(DialogInterface dialog, int which) { // ����ļ�
						String name = edFileName.getText().toString();
					    if (!name.equals("") && !"".equals(name)) {
						 File file = new File(currentFilePath + "/"+ name);
						  Log.i("float", " ���ӣ�"+file.getAbsolutePath()) ;
							if (!file.exists()) {
								file.mkdir();
								getFileData(file.getParentFile());
								framgemenFile.bindDataFile();
								mFragmentAdapter.notifyDataSetChanged(); // ��������
								Toast.makeText(mContext, name + "�Ѿ����ӳɹ�",2000).show();
								
								} else {
									Toast.makeText(mContext, name + "�Ѿ�����",2000).show();
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
	protected void onPostCreate(Bundle savedInstanceState) {   // ͬ������ͼ��
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
	public boolean onPrepareOptionsMenu(Menu menu) {    // Ԥ����
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			menu.findItem(R.id.action_settings).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
		mDrawerLayout.closeDrawer(Gravity.START); // �رճ���
		mActionBar.setTitle(mDataDrawerLists[position]); // ���²���������
		isOff = false;
		switch (position) {
		case 0:            // ����fileFragment
			FragmentManager managerA = getFragmentManager();
			FragmentTransaction fragmentTransactionA = managerA.beginTransaction();
			framgemenFile = new FragmentShowFile();
			fragmentTransactionA.replace(R.id.framelayout, framgemenFile);
			fragmentTransactionA.commit();
			break;
		case 1:                 // ����imageFragment
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
			mtvPath = (TextView) view.findViewById(R.id.pathInfo); // ��ʾ��ǰ·������Ϣ
			Log.i("float", "22") ;
			bindDataFile();
			
			lvfragment.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); // ����listView�Ķ�ѡ
			lvfragment.setMultiChoiceModeListener(this);
			lvfragment.setOnItemClickListener(this);
			return view;
		}

		public  void bindDataFile() { // ������
			mFragmentAdapter = new FragmentAdapter(mfileData, mContext);
			lvfragment.setAdapter(mFragmentAdapter);
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) { // �����
			if (isselect) {
				map = new HashMap<Integer, File>();
				SparseBooleanArray array = lvfragment.getCheckedItemPositions(); // ϡ������
				int sprsesize = array.size();     // ��ȡѡ�еĳ���
				for (int i = 0; i < sprsesize; i++) {
					if (array.valueAt(i)) {
						int index = array.keyAt(i); // �����ȡ��ѡ��id,
						file = (File) lvfragment.getAdapter().getItem(index);
						map.put(i, file);
					}
				}
			}

			switch (item.getItemId()) {

			case R.id.action_remove:
				new FileAsyncTask(map.size()).execute(map) ;
				
				mode.finish(); // �ر�ģʽ
				break;
				
			case R.id.action_copy:
				isselect = false ;
				mode.getMenu().findItem(R.id.action_copy).setVisible(false);
				mode.getMenu().findItem(R.id.action_parser).setVisible(true);
				mode.finish(); // �ر�ģʽ
				break;
				
			case R.id.action_parser: // ճ��
				@SuppressWarnings("rawtypes")
				Iterator iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry entry = (Entry) iterator.next(); // ��ȡʵ�����
					File file = (File) entry.getValue(); // ��ȡ�ļ�
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
			
				mode.finish(); // �ر�ģʽ
			default:
				break;
			}
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) { // �����˵�ѡ��
			getActivity().getMenuInflater().inflate(R.menu.home_menu, menu); // ��Ӳ˵�
			menu.findItem(R.id.action_copy).setVisible(true); // ����ͼ��Ŀɼ���
			mode.setTitle("ѡ��");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) { // ����
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) { // Ԥ����
			menu.findItem(R.id.action_parser).setVisible(true); // ����ճ�����ɼ�
			return false;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
			int count = lvfragment.getCheckedItemCount(); // ��ѡ�е���Ŀ
			mode.setSubtitle(count + "��");

		}

		@Override
		public void onItemClick(AdapterView<?> perant, View view, int position,long id) { // ListView�ϵĵ���¼�

			File file = mfileData[position]; // ��ȡ�ļ�
			flagpath = file.getPath();
			currentFilePath = file.getParentFile();

			if (!file.isFile()) {
				mtvPath.append("/" + file.getName());
			}

			if (file.isDirectory()) { // �����Ŀ¼
			     getFileData(file) ;
			 	 mFragmentAdapter.refresh(mfileData);
				 mFragmentAdapter.notifyDataSetChanged();
				lvfragment.setAdapter(mFragmentAdapter);
				
			} else {  // ������ļ�

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
					Toast.makeText(getActivity(), "û�д���������", 3000).show();
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
				String path = mtvPath.getText().toString(); // ����·����Ϣ

				if (!path.equals("/SDCard")) {
					int index = path.lastIndexOf("/");
					path = path.substring(0, index);
				}
				if (path != null) {
					mtvPath.setText(path); // ��ʾ�ļ���·��
					framgemenFile.bindDataFile();
					lvfragment.setAdapter(mFragmentAdapter);
					mFragmentAdapter.notifyDataSetChanged(); // ֪ͨ
				} else {
					return true;
				}

			} else {
				
				if (currentFilePath.getPath().equals("/mnt")) {
					if (++count == 2) {
						count = 0;
						this.finish();
					}
					Toast.makeText(mContext, "�ٴε�����˳�����", 2000).show();
				}
				
			}
		}
		return false;
	}

	public static class FragmentImageFile extends Fragment { // ͼƬ����ʾ

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
					.getContentResolver(); // ��ȡContentResolver

			String[] projection = { MediaStore.Images.Media._ID, 
					MediaStore.Images.Media.DISPLAY_NAME,
					MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE }; // ��ѯ���ֶ�
			String selection = MediaStore.Images.Media.MIME_TYPE + "=?"; // ����

			String[] selectionArgs = { "image/jpeg" }; // ����ֵ(�@�e�Ĳ�������ͼƬ�ĸ�ʽ�����Ǳ�׼�����в�Ҫ�Ķ�)
			String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc"; // ����

			Cursor cursor = contentResolver.query(uri, projection, selection,selectionArgs, sortOrder); // ��ѯsd���ϵ�ͼƬ

			List<HashMap<String, String>> mImageDara = new ArrayList<HashMap<String, String>>(); // ����ͼƬ������
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
			progressBar.setTitle("ɾ��");
			progressBar.setMessage("����ɾ������....");
			progressBar.setMax(values) ;  //���ý����������ֵ	
			progressBar.setCancelable(false) ;
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) ;  //������ʾ����ʽ
			progressBar.setButton(ProgressDialog.BUTTON_NEUTRAL, "ȡ��", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					 cancel(false) ;
				}
			});
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.show() ;  //��ʾ���ȿ�
		}
		  
		@Override
		protected void onPostExecute(Boolean result) {      // ִ�з��صĽ��
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
				 publishProgress(i+1) ;             //���½�����
			 }
			 progressBar.cancel() ;  //ȡ�����ȿ� 
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressBar.setProgress(values[0]);           //��ʾ������
		}
		
	}
	
	//****************************�첽��ѯ����**************************************
	
	private void startQuery() {
	
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ;
		String[] projection  = { 
				MediaStore.Audio.Media._ID,     //������id
				MediaStore.Audio.Media.TITLE,    //����������
                MediaStore.Audio.Media.ARTIST,    //�����ĸ�������
                MediaStore.Audio.Media.DURATION,  //��������ʱ��
                MediaStore.Audio.Media.ALBUM,   //������ר��
                MediaStore.Audio.Media.DATA ,    //������·��
                MediaStore.Audio.Media.SIZE,   //�������ܵ��ļ���С
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
					song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));   //����������
					song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));     //����
					song.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));         //·��
					song.setDurtion(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));  //ʱ��
				
					Log.i("float", song.toString()+"����") ;
					listSong.add(song) ;  //���
				}
			}
			
			super.onQueryComplete(token, cookie, cursor);
		}
	}
	
	
	@SuppressLint("ValidFragment")
	private static class FragmentMusic extends Fragment{    //���ֵ���ʾ
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
