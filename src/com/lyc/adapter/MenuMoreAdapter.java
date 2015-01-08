package com.lyc.adapter;

import com.lyc.main.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuMoreAdapter extends BaseAdapter {
	public String[] more_menu;
	private Context context;
	private Resources res;
	
	public MenuMoreAdapter(Context context){
		this.context=context;
		this.res=this.context.getResources();
		this.more_menu=this.res.getStringArray(R.array.more_menu);
	}

	public int getCount() {
		return this.more_menu.length;
	}

	public Object getItem(int position) {
		return more_menu[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text=new TextView(context);
		//设置文本的宽度和高度
		text.setHeight((int)this.res.getDimension(R.dimen.dimen_40));
		text.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		text.setGravity(Gravity.CENTER_VERTICAL);
		text.setText(this.more_menu[position]);
		text.setTextColor(R.color.white);
		text.setPadding(6, 0, 0, 0);
		text.setTextSize(15);
		return text;
	}

}
