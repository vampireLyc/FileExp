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
	private List<File> tempFile;// ����˵����м��к͸���ʱ�����ڱ��汻���к͸����ļ�
	private ImageButton btn_paste;
	private ImageButton btn_cancel;
	private boolean falg;// ���ñ�־λ�����Ϊtrue����ʾҪճ�����ļ��Ǹ������ģ����Ϊfalse����ʾҪճ�����ļ��Ǽ������ġ�
	private ListView layout_more_list;// ��ʾ����˵����б�
	private MenuMoreAdapter moreAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
	}

	public void initView() {
		context = this;
		// ��ʼ���ؼ�
		this.mainPath = (TextView) findViewById(R.id.main_path);
		this.mainList = (ListView) findViewById(R.id.main_list);
		this.mainList.setOnItemClickListener(this);

		// ��ʼ��menu1�����еİ�ť�������ü�����
		this.more = (ImageButton) findViewById(R.id.more);
		this.more.setOnClickListener(this);
		this.btn_copy = (ImageButton) findViewById(R.id.btn_copy);
		this.btn_copy.setOnClickListener(this);
		this.btn_cut = (ImageButton) findViewById(R.id.btn_cut);
		this.btn_cut.setOnClickListener(this);
		this.btn_delete = (ImageButton) findViewById(R.id.btn_delete);
		this.btn_delete.setOnClickListener(this);
		// ��ʼ��menu2�����еİ�ť�������ü�����
		this.btn_paste = (ImageButton) findViewById(R.id.btn_paste);
		this.btn_paste.setOnClickListener(this);
		this.btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
		this.btn_cancel.setOnClickListener(this);
		this.falg = false;
		this.layout_more_list = (ListView) findViewById(R.id.layout_more_list);
		this.moreAdapter = new MenuMoreAdapter(this.context);
		this.layout_more_list.setAdapter(moreAdapter);
		this.layout_more_list.setOnItemClickListener(this);

		// ��ʼ�������˵������еİ�ť�������ü�����
		this.menu_title_all = (ImageButton) findViewById(R.id.menu_title_all);
		this.menu_title_all.setOnClickListener(this);
		this.menu_title_cancel = (ImageButton) findViewById(R.id.menu_title_cancel);
		this.menu_title_cancel.setOnClickListener(this);
		this.menu_title_info = (TextView) findViewById(R.id.menu_title_info);

		this.tempFile = new ArrayList<File>();

		// �ж�SD��״̬�����õ�SD����Ŀ¼�µ��ļ��б�
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// SD������
			this.currRoot = Environment.getExternalStorageDirectory();
			this.currRootInfo = currRoot.getPath();
			this.SDPath = this.currRootInfo;
		} else {
			// SD��������
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
			if (file.isDirectory()) {//�����������ļ���
				this.currRoot = file;
				this.updateInfo();
			}
			if(file.isFile()){//�����������ļ�
				OpenFiles.open(context, file);
			}
		}
		if (parent.getId() == R.id.layout_more_list) {
			switch (position) {
			case 0:// �½��ļ���
				createNewFolder();
				this.changeOperateMenu(FileUtil.HIDEMENU);
				break;
			case 1:// ����
				
				break;
			case 2:// �ղ�
				//�ղ�һ���ļ���ʲô�أ�������
				break;
			case 3:// ������
				if (this.adapter.listCheckeds.size() == 1) {// ��ѡ��һ���ļ������ļ���ʱ����ִ��������
					File file = this.currFiles[this.adapter.listCheckeds.get(0).getPosition()];
					rename(file);
				} else {// ��ѡ�����ļ������ļ���ʱ������ִ��������
					Toast.makeText(context, "ֻ����ѡ��һ���ļ�ʱ����ִ��������������������ѡ��",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 4:// ����
				List<File> list=new ArrayList<File>();
				for(int i=0;i<this.adapter.listCheckeds.size();i++){
					list.add(this.currFiles[this.adapter.listCheckeds.get(i).getPosition()]);
				}
				showInfo(list);
				this.changeOperateMenu(FileUtil.HIDEMENU);
				break;
			case 5:// ѹ��
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
	 * �õ���ǰ��ѡ����ļ��б���ת��Ϊ���ϵ���ʽ
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
	 * �õ���ǰ��ѡ����ļ��б���ת��Ϊ������ʽ
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
	 * ѹ���ļ�
	 * @param files ��ѹ�����ļ�
	 * @param file ѹ���ļ������ڵĸ�Ŀ¼
	 */
	public void zipFile(final File[] files,final File file){
		AlertDialog.Builder dialog=new AlertDialog.Builder(context);
		dialog.setTitle("ѹ���ļ�");
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
							Toast.makeText(context, "ѹ���ɹ�", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(context, "ѹ��ʧ��", Toast.LENGTH_SHORT).show();
						}
					}
				});
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();
	}
	
	/**
	 * ��ʾ�ļ�����
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
				sb.append("�ļ���С:"+file.length());
			}
			if(file.isDirectory()){
				sb.append("���ļ�����:"+file.listFiles(new MyFileFilter()).length);
			}
			sb.append("�޸�ʱ��:"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file.lastModified())));
			info[i]=sb.toString();
		}
		dialog.setItems(info, null);//������ʾһ���ļ��б�
		dialog.setView(null);
		dialog.show();
	}
	
	/**
	 * ������ָ���ļ�������
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
								Toast.makeText(context,"�������ɹ�",Toast.LENGTH_LONG).show();
								adapter.update(currRoot,currFiles);
							} else {
								Toast.makeText(context, "������ʧ��",Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(context, "�ļ���������Ϊ��",Toast.LENGTH_LONG).show();
						}
					}
				});
		dialog.setNegativeButton(R.string.cancel,null);
		dialog.show();
	}

	/**
	 * �½��ļ���
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
								Toast.makeText(context, "�½��ļ��гɹ�",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(context, "�½��ļ���ʧ��",
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
			// ��������˵���ʾ�������ز����˵�
			if (this.adapter.listCheckeds.size() > 0) {
				changeOperateMenu(FileUtil.HIDEMENU);
				this.adapter.listCheckeds.clear();
				adapter.update(this.currRoot, this.currFiles);
				if (adapter.mapState.get(this.currRoot) != null) {// ����ط��п������쳣��û�д����أ�����
					this.mainList.onRestoreInstanceState(adapter.mapState
							.get(this.currRoot));
				}
				return true;
			} else {
				// ɾ������һ��������б�״̬
				if (adapter.mapState.get(this.currRoot) != null) {
					this.adapter.mapState.remove(this.currRoot);
				}
				if (!this.currRootInfo.equals(this.SDPath)) {
					this.currRoot = this.currRoot.getParentFile();
					this.updateInfo();
					if (adapter.mapState.get(this.currRoot) != null) {// ����ط��п������쳣��û�д����أ�����
						this.mainList.onRestoreInstanceState(adapter.mapState
								.get(this.currRoot));
					}
					return true;
				} else {
					this.finish();// �˳�����
				}
			}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ���µ�ǰĿ¼���ļ��б��־��Ϣ���������б�
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
		case FileUtil.SHOWMENU1:// ��ʾ�ײ��˵�
			findViewById(R.id.operate_ccdm).setVisibility(View.VISIBLE);// ��ʾ�ײ��˵���
//			this.mainPath.setVisibility(View.GONE);// ���ص�ַ��
			AnimationSet as=new AnimationSet(true);
			TranslateAnimation ta=new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,0f,//��ʼX��
					Animation.RELATIVE_TO_PARENT,0f,//����X��
					Animation.RELATIVE_TO_PARENT,-0.1f,//��ʼY��
					Animation.RELATIVE_TO_PARENT,0f);//����Y��
			ta.setDuration(400);
			as.addAnimation(ta);
			findViewById(R.id.menu_title).startAnimation(as);
			findViewById(R.id.menu_title).setVisibility(View.VISIBLE);// ��ʾ�����˵���
			this.menu_title_info.setText("ѡ��"
					+ this.adapter.listCheckeds.size() + "��");
			if (findViewById(R.id.layout_more_linear).getVisibility() == View.GONE) {// ���û����ʾ�����б�˵�ʱ�����ఴť����û�б�ѡ��״̬
				this.more.setImageResource(R.drawable.health);
			}
			break;
		case FileUtil.SHOWMENU2:// �������в˵���ͬʱ��ʾ�ײ�ճ����ȡ���˵�
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
		case FileUtil.HIDEMENU:// �������в˵��������е�ѡ�������
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
	 * ��������¼��Ļص�����
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.more) {
			if (findViewById(R.id.layout_more_linear).getVisibility() == View.GONE) {// ���û����ʾʱ���������ʾ
				findViewById(R.id.layout_more_linear).setVisibility(
						View.VISIBLE);
				this.more.setImageResource(R.drawable.health_c);
			} else if (findViewById(R.id.layout_more_linear).getVisibility() == View.VISIBLE) {// �����ʾʱ���������ʾ
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
			this.menu_title_info.setText("ѡ��" + this.currFiles.length + "��");
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
			if(!this.falg){//����Ǽ��й����ģ�Ҫ��ԭ�ļ�ɾ��
				deleteFile(this.tempFile);
			}
		}
	}
	
	/**
	 * ɾ���ļ�
	 * @param list
	 */
	public void deleteFile(List<File> list){
		for(File file:list){
			file.delete();
		}
	}
	
	/**
	 * �����ļ�
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
	 * ���f1�ļ����Ƶ�f2�ļ�
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