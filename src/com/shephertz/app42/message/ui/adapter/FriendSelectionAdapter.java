
/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.model.IUser;

/**
 * @author Vishnu
 *
 */
public class FriendSelectionAdapter extends AbstractListAdapter<IUser> {
	
	private final LayoutInflater inflater;
	
	public FriendSelectionAdapter(Context context) {
		super(context, R.layout.home_list_item);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.friend_selection_item, parent, false);
			holder = new ViewHolder();
			holder.displayName = (TextView)convertView.findViewById(R.id.name);
			holder.icon = (ImageView)convertView.findViewById(R.id.user_pic);
			holder.message = (TextView)convertView.findViewById(R.id.message_text);
			holder.selectionCheck = (CheckBox)convertView.findViewById(R.id.select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		 }
		IUser user = this.getItem(position);
		holder.position = position;
		holder.displayName.setText(user.getName());
		holder.message.setText(user.getMessage());
	holder.selectionCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				//((FriendSelectionActivity)mContext).showDialogPopup(item.getId(), position);
			}
		});
		return convertView;
	}

}