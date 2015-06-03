
/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.model.IGroup;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 *
 */
public class GroupAdapter extends AbstractListAdapter<IGroup> {
	
	private final LayoutInflater inflater;
	private ImageCacheManager imageManager;
	
	public GroupAdapter(Context context) {
		super(context, R.layout.home_list_item);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageManager=new ImageCacheManager(context, UiUtil.dpToPx(context, 50), R.drawable.default_icon,true);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.home_list_item, parent, false);
			holder = new ViewHolder();
			holder.displayName = (TextView)convertView.findViewById(R.id.name);
			holder.icon = (ImageView)convertView.findViewById(R.id.user_pic);
			holder.message = (TextView)convertView.findViewById(R.id.message_text);
			holder.time = (TextView)convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
	
		IGroup group = this.getItem(position);
		if(group.getPicUri()!=null&&!group.getPicUri().isEmpty())
		imageManager.loadImage(group.getPicUri(), holder.icon);
		else{
			holder.icon.setImageBitmap(UiUtil.getDefaultPicBitMap(mContext));
		}
			
		holder.position = position;
		holder.displayName.setText(group.getGroupName());
		if(group.getSenderName()==null||group.getSenderName().equals(App42ChatApplication.getUserId())){
			//holder.message.setText("You : "+getFormattedMessage(group.getMessage()));
			holder.message.setText(UiUtil.getSmiledText(mContext,getFormattedMessage("You "+group.getMessage())));
		}
		else{
			holder.message.setText(UiUtil.getSmiledText(mContext,getFormattedMessage(group.getSenderName()+"  "+group.getMessage())));
		}
		
		holder.time.setText(Utility.getTime(group.getLastCommTime()));
		return convertView;
	}

	private String getFormattedMessage(String message){
		if(message.startsWith("http"))
			return "image";
		else
			
		return message;
	}
}