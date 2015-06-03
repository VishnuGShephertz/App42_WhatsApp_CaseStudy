/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Vishnus
 *
 */
public abstract class AbstractListAdapter<T> extends ArrayAdapter<T> {
	protected Context mContext;
	static class ViewHolder {
		int position;
		TextView displayName;
		ImageView icon;
		TextView message;
		TextView time;
		CheckBox selectionCheck;
	}
	
	/**
	 * @param context
	 * @param resourceId
	 */
	public AbstractListAdapter(Context context, int resourceId) {
		super(context, resourceId);
		this.mContext=context;
	}
	/**
	 * @param context
	 * @param resourceId
	 * @param objects
	 */
	public AbstractListAdapter(Context context, int resourceId, List<T> objects) {
		super(context, resourceId, objects);
		this.mContext=context;
		this.clear();
		this.addAll(objects);
	}
	/**
	 * @param dataList
	 */
	public void addAll(List<? extends T> dataList) {
		if (dataList != null && dataList.size() > 0) {
			for (T data : dataList) {
				add(data);
			}
		}
	}
	/**
	 * @param data
	 */
	public void setData(List<? extends T> data) {
		this.clear();
		this.addAll(data);
	}
	
	public void addData(T data){
		add(data);
	}
}
