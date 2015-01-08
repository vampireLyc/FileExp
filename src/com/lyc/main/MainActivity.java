package com.lyc.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyc.adapter.FileAdapter;
import com.lyc.adapter.FileAdapter.ChangeOperateMenu;
import com.lyc.adapter.MenuMoreAdapter;
import com.lyc.domain.CheckedFile;
import com.lyc.util.FileUtil;
import com.lyc.util.MyFileFilter;
import com.lyc.util.OpenFiles;
import com.lyc.util.ZipFileUtil;

public class MainActivity extends Activity implements OnItemClickListener,
		ChangeOperateMenu, OnClickListener {
	private TextView mainPath;
	private ListView mainList;
	private File[] currFiles;
	private File currRoot;
	private String currRootInfo;
	private Context context;
	private FileAdapter adapter;
	private String SDPath;
	private ImageButton menu_title_cancel;
	private ImageButton menu_title_all;
	private TextView menu_title_info;

	private ImageButton more;
	private ImageButton btn_copy;
	private ImageButton btn_cut;
	private ImageButton btn_delete;
	private List<File> tempFile;// 点击菜单项中剪切和复制时，用于保存被剪切和复制文件
	private ImageButton btn_paste;
	private ImageButton btn_cancel;
	private boolean falg;// 设置标志位：如果为true，表示要粘贴的文件是复制来的；如果为false，表示要粘贴的文件是剪切来的。
	private ListView layout_more_list;// 显示更多菜单的列表
	private MenuMoreAdapter moreAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
	}

	public void initView() {
		context = this;
		// 初始化控件
		this.mainPath = (TextView) findViewById(R.id.main_path);
		this.mainList = (ListView) findViewById(R.id.main_list);
		this.mainList.setOnItemClickListener(this);

		// 初始化menu1布局中的按钮，并设置监听器
		this.more = (ImageButton) findViewById(R.id.more);
		this.more.setOnClickListener(this);
		this.btn_copy = (ImageButton) findViewById(R.id.btn_copy);
		this.btn_copy.setOnClickListener(this);
		this.btn_cut = (ImageButton) findViewById(R.id.btn_cut);
		this.btn_cut.setOnClickListener(this);
		this.btn_delete = (ImageButton) findViewById(R.id.btn_delete);
		this.btn_delete.setOnClickListener(this);
		// 初始化menu2布局中的按钮，并设置监听器
		this.btn_paste = (ImageButton) findViewById(R.id.btn_paste);
		this.btn_paste.setOnClickListener(this);
		this.btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
		this.btn_cancel.setOnClickListener(this);
		this.falg = false;
		this.layout_more_list = (ListView) findViewById(R.id.layout_more_list);
		this.moreAdapter = new MenuMoreAdapter(this.context);
		this.layout_more_list.setAdapter(moreAdapter);
		this.layout_more_list.setOnItemClickListener(this);

		// 初始化顶部菜单布局中的按钮，并设置监听器
		this.menu_title_all = (ImageButton) findViewById(R.id.menu_title_all);
		this.menu_title_all.setOnClickListener(this);
		this.menu_title_cancel = (ImageButton) findViewById(R.id.menu_title_cancel);
		this.menu_title_cancel.setOnClickListener(this);
		this.menu_title_info = (TextView) findViewById(R.id.menu_title_info);

		this.tempFile = new ArrayList<File>();

		// 判断SD卡状态，并得到SD卡根目录下的文件列表
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// SD卡可用
			this.currRoot = Environment.getExternalStorageDirectory();
			this.currRootInfo = currRoot.getPath();
			this.SDPath = this.currRootInfo;
		} else {
			// SD卡不可用
		}
		currFiles = currRoot.listFiles(new MyFileFilter());
		currFiles = FileUtil.sortFiles(currFiles);
		adapter = new FileAdapter(context, currRoot, currFiles, mainList);
		this.mainList.setAdapter(adapter);
		this.mainPath.setText(this.currRootInfo);
		this.adapter.setChangeOperateMenu(MainActivity.this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.main_list) {
			File file = this.currFiles[position];
			if (file.isDirectory()) {//如果点击的是文件夹
				this.currRoot = file;
				this.updateInfo();
			}
			if(file.isFile()){//如果点击的是文件
				OpenFiles.open(context, file);
			}
		}
		if (parent.getId() == R.id.layout_more_list) {
			switch (position) {
			case 0:// 新建文件夹
				createNewFolder();
				this.changeOperateMenu(FileUtil.HIDEMENU);
				break;
			case 1:// 发送
				
				break;
			case 2:// 收藏
				//收藏一个文件干什么呢？！！！
				break;
			case 3:// 重命名
				if (this.adapter.listCheckeds.size() == 1) {// 当选择一个文件或者文件夹时才能执行重命名
					File file = this.currFiles[this.adapter.listCheckeds.get(0).getPosition()];
					rename(file);
				} else {// 当选择多个文件或者文件夹时，不能执行重命名
					Toast.makeText(context, "只有在选择一个文件时才能执行重命名操作，请重新选择",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 4:// 详情
				List<File> list=new ArrayList<File>();
				for(int i=0;i<this.adapter.listCheckeds.size();i++){
					list.add(this.currFiles[this.adapter.listCheckeds.get(i).getPosition()]);
				}
				showInfo(list);
				this.changeOperateMenu(FileUtil.HIDEMENU);
				break;
			case 5:// 压缩
				File[] files = getChosenFiles();
				zipFile(files,this.currRoot);
				changeOperateMenu(FileUtil.HIDEMENU);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 得到当前被选择的文件列表，并转换为集合的形式
	 * @return
	 */
	public List<File> getChosenFile(){
		List<File> list=new ArrayList<File>();
		for(CheckedFile cf:this.adapter.listCheckeds){
			int p=cf.getPosition();
			File f=this.currFiles[p];
			list.add(f);
		}
		return list;
	}
	
	/**
	 * 得到当前被选择的文件列表，并转换为数组形式
	 * @return
	 */
	public File[] getChosenFiles() {
		File[] files=new File[this.adapter.listCheckeds.size()];
		for(int i=0;i<this.adapter.listCheckeds.size();i++){
			files[i]=this.currFiles[this.adapter.listCheckeds.get(i).getPosition()];
		}
		return files;
	}
	
	/**
	 * 压缩文件
	 * @param files 待压缩的文件
	 * @param file 压缩文件后坐在的根目录
	 */
	public void zipFile(final File[] files,final File file){
		AlertDialog.Builder dialog=new AlertDialog.Builder(context);
		dialog.setTitle("压缩文件");
		final EditText edit=new EditText(context);
		dialog.setView(edit);
		dialog.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String zipName = edit.getText().toString();
						String zipPath=file.getPath()+File.separator+zipName;
						if(!zipPath.endsWith(".zip")){
							zipPath=zipPath+".zip";
						}
						File zipFile=new File(zipPath);
						if(ZipFileUtil.zipFile(zipFile, files)){
							Toast.makeText(context, "压缩成功", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(context, "压缩失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();
	}
	
	/**
	 * 显示文件详情
	 * @param list
	 */
	public void showInfo(List<File> list){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.file_info);
		String[] info=new String[list.size()];
		for(int i=0;i<list.size();i++){
			File file=list.get(i);
			StringBuffer sb=new StringBuffer();
			if(file.isFile()){
				sb.append("文件大小:"+file.length());
			}
			if(file.isDirectory()){
				sb.append("子文件数量:"+file.listFiles(new MyFileFilter()).length);
			}
			sb.append("修改时间:"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file.lastModified())));
			info[i]=sb.toString();
		}
		dialog.setItems(info, null);//设置显示一组文件列表
		dialog.setView(null);
		dialog.show();
	}
	
	/**
	 * 重命名指定文件的名称
	 * @param file
	 */
	public void rename(final File file) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.rename);

		final EditText edit = new EditText(context);
		edit.setHint(R.string.hint_input_name);
		edit.setText(file.getName());
		edit.selectAll();
		dialog.setView(edit);
		dialog.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String newName = edit.getText().toString();
						if (newName != null && !"".equals(newName)) {
							// /mnt/sdcard/hello1
							String path1=file.getPath();
							int indexOf = path1.lastIndexOf("/");
							String path2 = path1.substring(0, indexOf);
							// /mnt/sdcard/hello2
							boolean b=file.renameTo(new File(path2 + "/"+ newName));
							if (b) {
								Toast.makeText(context,"重命名成功",Toast.LENGTH_LONG).show();
								adapter.update(currRoot,currFiles);
							} else {
								Toast.makeText(context, "重命名失败",Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(context, "文件夹名不能为空",Toast.LENGTH_LONG).show();
						}
					}
				});
		dialog.setNegativeButton(R.string.cancel,null);
		dialog.show();
	}

	/**
	 * 新建文件夹
	 */
	private void createNewFolder() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.rename);

		final EditText edit = new EditText(context);
		edit.setHint(R.string.hint_input_name);
		edit.selectAll();
		dialog.setView(edit);

		dialog.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						File file = new File(edit.getText().toString());
						if (!file.exists()) {
							boolean b = file.mkdir();
							System.out.println("file.mkdir()");
							if (b) {
								Toast.makeText(context, "新建文件夹成功",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(context, "新建文件夹失败",
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});

		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// 如果操作菜单显示，则隐藏操作菜单
			if (this.adapter.listCheckeds.size() > 0) {
				changeOperateMenu(FileUtil.HIDEMENU);
				this.adapter.listCheckeds.clear();
				adapter.update(this.currRoot, this.currFiles);
				if (adapter.mapState.get(this.currRoot) != null) {// 这个地方有可能有异常，没有处理呢！！！
					this.mainList.onRestoreInstanceState(adapter.mapState
							.get(this.currRoot));
				}
				return true;
			} else {
				// 删除掉上一个保存的列表状态
				if (adapter.mapState.get(this.currRoot) != null) {
					this.adapter.mapState.remove(this.currRoot);
				}
				if (!this.currRootInfo.equals(this.SDPath)) {
					this.currRoot = this.currRoot.getParentFile();
					this.updateInfo();
					if (adapter.mapState.get(this.currRoot) != null) {// 这个地方有可能有异常，没有处理呢！！！
						this.mainList.onRestoreInstanceState(adapter.mapState
								.get(this.currRoot));
					}
					return true;
				} else {
					this.finish();// 退出程序
				}
			}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 更新当前目录和文件列表标志信息，并更新列表
	 */
	public void updateInfo() {
		this.currRootInfo = this.currRoot.getPath();
		this.currFiles = this.currRoot.listFiles(new MyFileFilter());
		this.currFiles = FileUtil.sortFiles(this.currFiles);
		this.mainPath.setText(this.currRootInfo);
		adapter.update(this.currRoot, this.currFiles);
	}

	public void changeOperateMenu(int flag) {
		switch (flag) {
		case FileUtil.SHOWMENU1:// 显示底部菜单
			findViewById(R.id.operate_ccdm).setVisibility(View.VISIBLE);// 显示底部菜单栏
//			this.mainPath.setVisibility(View.GONE);// 隐藏地址栏
			AnimationSet as=new AnimationSet(true);
			TranslateAnimation ta=new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,0f,//开始X轴
					Animation.RELATIVE_TO_PARENT,0f,//结束X轴
					Animation.RELATIVE_TO_PARENT,-0.1f,//开始Y轴
					Animation.RELATIVE_TO_PARENT,0f);//结束Y轴
			ta.setDuration(400);
			as.addAnimation(ta);
			findViewById(R.id.menu_title).startAnimation(as);
			findViewById(R.id.menu_title).setVisibility(View.VISIBLE);// 显示顶部菜单栏
			this.menu_title_info.setText("选中"
					+ this.adapter.listCheckeds.size() + "项");
			if (findViewById(R.id.layout_more_linear).getVisibility() == View.GONE) {// 如果没有显示更多列表菜单时，更多按钮处于没有被选中状态
				this.more.setImageResource(R.drawable.health);
			}
			break;
		case FileUtil.SHOWMENU2:// 隐藏所有菜单，同时显示底部粘贴和取消菜单
			if (findViewById(R.id.operate_ccdm).getVisibility() == View.VISIBLE) {
				findViewById(R.id.operate_ccdm).setVisibility(View.GONE);
			}
			if (findViewById(R.id.layout_more_linear).getVisibility() == View.VISIBLE) {
				findViewById(R.id.layout_more_linear).setVisibility(View.GONE);
			}
			if (findViewById(R.id.menu_title).getVisibility() == View.VISIBLE) {
				findViewById(R.id.menu_title).setVisibility(View.GONE);
			}
			this.mainPath.setVisibility(View.VISIBLE);

			findViewById(R.id.operate_pc).setVisibility(View.VISIBLE);
			break;
		case FileUtil.HIDEMENU:// 隐藏所有菜单，将所有的选择项清空
			if (findViewById(R.id.operate_ccdm).getVisibility() == View.VISIBLE) {
				findViewById(R.id.operate_ccdm).setVisibility(View.GONE);
			}
			if (findViewById(R.id.layout_more_linear).getVisibility() == View.VISIBLE) {
				findViewById(R.id.layout_more_linear).setVisibility(View.GONE);
			}
			if (findViewById(R.id.menu_title).getVisibility() == View.VISIBLE) {
				findViewById(R.id.menu_title).setVisibility(View.GONE);
			}
			if (findViewById(R.id.operate_pc).getVisibility() == View.VISIBLE) {
				findViewById(R.id.operate_pc).setVisibility(View.GONE);
			}
			this.mainPath.setVisibility(View.VISIBLE);
			this.adapter.listCheckeds.clear();
			this.adapter.notifyDataSetChanged();
			break;
		}
	}

	/**
	 * 监听点击事件的回调方法
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.more) {
			if (findViewById(R.id.layout_more_linear).getVisibility() == View.GONE) {// 如果没有显示时，点击后显示
				findViewById(R.id.layout_more_linear).setVisibility(
						View.VISIBLE);
				this.more.setImageResource(R.drawable.health_c);
			} else if (findViewById(R.id.layout_more_linear).getVisibility() == View.VISIBLE) {// 如果显示时，点击后不显示
				findViewById(R.id.layout_more_linear).setVisibility(View.GONE);
				this.more.setImageResource(R.drawable.health);
			}
		}
		if (v.getId() == R.id.menu_title_cancel || v.getId() == R.id.btn_cancel) {
			changeOperateMenu(FileUtil.HIDEMENU);
			this.adapter.listCheckeds.clear();
			this.more.setImageResource(R.drawable.health);
			this.adapter.notifyDataSetChanged();
			this.adapter.hasPaste = false;
			this.tempFile.clear();
		}
		if (v.getId() == R.id.menu_title_all) {
			this.adapter.listCheckeds.clear();
			for (int i = 0; i < this.currFiles.length; i++) {
				this.adapter.addAll();
			}
			this.menu_title_info.setText("选中" + this.currFiles.length + "项");
			this.adapter.notifyDataSetChanged();
		}
		if (v.getId() == R.id.btn_copy) {
			for (CheckedFile cf: this.adapter.listCheckeds) {
				int position=cf.getPosition();
				this.tempFile.add(this.currFiles[position]);
			}
			changeOperateMenu(FileUtil.SHOWMENU2);
			this.falg = true;
			this.adapter.hasPaste = true;
		}
		if (v.getId() == R.id.btn_cut) {
			for (CheckedFile cf: this.adapter.listCheckeds) {
				int position=cf.getPosition();
				this.tempFile.add(this.currFiles[position]);
			}
			changeOperateMenu(FileUtil.SHOWMENU2);
			this.falg = false;
			this.adapter.hasPaste = true;
		}
		if (v.getId() == R.id.btn_delete) {
			for (CheckedFile cf: this.adapter.listCheckeds) {
				int position=cf.getPosition();
				this.tempFile.add(this.currFiles[position]);
			}
			deleteFile(this.tempFile);
			this.adapter.update(this.currRoot, this.currFiles);
			this.adapter.hasPaste=false;
			this.tempFile.clear();
			changeOperateMenu(FileUtil.HIDEMENU);
			this.adapter.notifyDataSetChanged();
		}
		if (v.getId() == R.id.btn_paste) {
			pasteFile(this.currRoot,this.tempFile);
			this.tempFile.clear();
			this.adapter.update(this.currRoot, this.currFiles);
			changeOperateMenu(FileUtil.HIDEMENU);
			this.adapter.hasPaste=false;
			if(!this.falg){//如果是剪切过来的，要把原文件删除
				deleteFile(this.tempFile);
			}
		}
	}
	
	/**
	 * 删除文件
	 * @param list
	 */
	public void deleteFile(List<File> list){
		for(File file:list){
			file.delete();
		}
	}
	
	/**
	 * 复制文件
	 * @param root
	 * @param list
	 */
	public void pasteFile(File root,List<File> list){
		for(File file:list){
			if(file.isDirectory()){
				File f=new File(root.getPath()+File.separator+file.getName());
				if(f.mkdir()){
					File[] files=f.listFiles(new MyFileFilter());
					List<File> ls=new ArrayList<File>();
					for(int i=0;i<files.length;i++){
						ls.add(files[i]);
					}
					pasteFile(f,ls);
				}
			}
			if(file.isFile()){
				File f=new File(root.getPath()+File.separator+file.getName());
				paste(file,f);
			}
		}
	}
	
	/**
	 * 完成f1文件复制到f2文件
	 * @param f1
	 * @param f2
	 */
	public void paste(File f1,File f2){
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try {
			fis=new FileInputStream(f1);
			fos=new FileOutputStream(f2);
			byte[] bytes=new byte[1024];
			int temp=0;
			while((temp=fis.read(bytes))!=-1){
				fos.write(bytes, 0, temp);
			}
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos!=null){
					fos.close();
				}
				if(fis!=null){
					fis.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}