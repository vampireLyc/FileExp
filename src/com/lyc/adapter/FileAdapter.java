package com.lyc.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyc.domain.CheckedFile;
import com.lyc.main.R;
import com.lyc.util.ApkUtil;
import com.lyc.util.AsynIcon;
import com.lyc.util.FileUtil;
import com.lyc.util.MyFileFilter;

public class FileAdapter extends BaseAdapter implements OnClickListener,OnScrollListener{
	
	private Context context;
	private File currRoot;
	private File[] files;
	public LayoutInflater inflater;
	public List<CheckedFile> listCheckeds;
	private ListView mainList;
	public Map<File,Parcelable> mapState;
	public ChangeOperateMenu changeMenu;
	private boolean isUpdate;
	private Bitmap defaultIcon;
	private Bitmap videoIcon;
	private Bitmap musicIcon;
	private Bitmap documentIcon;
	private AsynIcon asynIncon;
	public boolean hasPaste;//设置标志位，如果为true，表示选中此时有需要复制的文件；如果为false，表示此时没有要复制的文件。
	
	public interface ChangeOperateMenu{
		public void changeOperateMenu(int flag);
	}
	
	public FileAdapter(Context context,File currRoot,File[] files,ListView mainList){
		this.currRoot=currRoot;
		this.isUpdate=true;
		this.asynIncon=new AsynIcon(new Handler());
		this.defaultIcon=BitmapFactory.decodeResource(context.getResources(), R.drawable.target);
		this.videoIcon=BitmapFactory.decodeResource(context.getResources(), R.drawable.video);
		this.musicIcon=BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
		this.documentIcon=BitmapFactory.decodeResource(context.getResources(), R.drawable.document);
		this.mapState=new HashMap<File,Parcelable>();
		this.context=context;
		this.files=files;
		this.mainList=mainList;
		this.mainList.setOnScrollListener(this);
		this.inflater = LayoutInflater.from(context);
		this.listCheckeds=new ArrayList<CheckedFile>();
		this.hasPaste=false;
	}
	
	public void update(File currRoot,File[] files){
		this.currRoot=currRoot;
		this.files=files;
		notifyDataSetChanged();
		//每次进入列表时，列表会显示出上一个列表状态，不知道为什么
		//这里每次更新列表，都让其先从第一个列表项开始显示，然后再可以按需要更改状态
		this.mainList.setSelection(0);
	}

	public int getCount() {
		return files.length;
	}

	public Object getItem(int position) {
		return files[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.file_item, null);
			viewHolder.icon=(ImageView) convertView.findViewById(R.id.item_icon);
			viewHolder.name=(TextView) convertView.findViewById(R.id.item_name);
			viewHolder.checkbox=(CheckBox) convertView.findViewById(R.id.item_checkbox);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		//设置ViewHolder，即设置每个Item内容
		File file=this.files[position];
		setFileIcon(viewHolder, file);
		setFileName(viewHolder, file);
		
		//判断每个item的checkbox的状态
		if(isChecked(this.files[position],position)){
			viewHolder.checkbox.setChecked(true);
		}else{
			viewHolder.checkbox.setChecked(false);
		}
		CheckedFile cf=new CheckedFile(this.files[position],position);
		viewHolder.checkbox.setTag(cf);
		viewHolder.checkbox.setOnClickListener(this);
		return convertView;
	}
	
	/**
	 * 检查position是不是被选中
	 * @param position
	 * @return
	 */
	public boolean isChecked(File file,int position){
		for(CheckedFile cf:this.listCheckeds){
			if(cf.getPosition()==position && file.getPath().equals(cf.getFile().getPath())){
				return true;
			}
		}
		return false;
	}
	
	public void addAll(){
		for(int i=0;i<this.files.length;i++){
			CheckedFile cf=new CheckedFile(this.files[i],i);
			this.listCheckeds.add(cf);
		}
	}

	private void setFileName(ViewHolder viewHolder, File file) {
		if(file.isDirectory()){
			int num=file.listFiles(new MyFileFilter()).length;
			if(num>0){
				viewHolder.name.setText(file.getName()+" | "+num+"个文件");
			}else{
				viewHolder.name.setText(file.getName()+" | 空文件夹");
			}
		}
		if(file.isFile()){
			viewHolder.name.setText(file.getName());
		}
	}

	private void setFileIcon(ViewHolder viewHolder, File file) {
		if(file.isDirectory()){
			viewHolder.icon.setImageResource(R.drawable.folder);
		}
		if(file.isFile()){
			if(FileUtil.endWith(file.getName(), ".mp3",".wav","m4a")){
				viewHolder.icon.setImageBitmap(this.musicIcon);
			}else if(FileUtil.endWith(file.getName(), ".mp4",".avi",".3gp",".flv",".wmv",".rmvb")){
				viewHolder.icon.setImageBitmap(this.videoIcon);
			}else if(file.getName().endsWith(".apk")){
				Drawable drawable = ApkUtil.loadApkIcon(context, file.getAbsolutePath());
				viewHolder.icon.setImageDrawable(drawable);
			}else{
				if(FileUtil.endWith(file.getName(), ".png",".jpg",".jpeg",".bmp",".gif")){
					if(this.isUpdate){
						String path=file.getAbsolutePath();
						viewHolder.icon.setTag(path);
						this.asynIncon.getIcons(viewHolder.icon);
					}else{
						viewHolder.icon.setImageBitmap(defaultIcon);
					}
				}else{
					viewHolder.icon.setImageBitmap(this.documentIcon);
				}
			}
		}
	}
	
	public static class ViewHolder{
		private ImageView icon;
		private TextView name;
		private CheckBox checkbox;
	}

	public void onClick(View v) {
		CheckBox cb=(CheckBox)v;
		if(!this.hasPaste){
			CheckedFile cf=(CheckedFile) cb.getTag();
			if(cb.isChecked()){
				cb.setChecked(true);
				this.listCheckeds.add(cf);
			}else{
				cb.setChecked(false);
				this.listCheckeds.remove(cf);
			}
			if(this.listCheckeds.size()>0){
				this.changeMenu.changeOperateMenu(FileUtil.SHOWMENU1);
			}else if(this.listCheckeds.size()==0){
				this.changeMenu.changeOperateMenu(FileUtil.HIDEMENU);
			}
		}else{
			cb.setChecked(false);
			Toast.makeText(this.context, "请先完成你的粘贴", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setChangeOperateMenu(ChangeOperateMenu changeMenu){
		this.changeMenu=changeMenu;
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.isUpdate=false;
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING://滑动中
			break;
		case OnScrollListener.SCROLL_STATE_IDLE://滑动停止
			isUpdate=true;
			Parcelable state=this.mainList.onSaveInstanceState();
			this.mapState.put(this.currRoot, state);
			notifyDataSetChanged();
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://手指在屏幕上
			break;
		default:
			break;
		}
	}

}
