/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 * 
 */
public class GroupMessageAdapter extends AbstractListAdapter<App42Message> {

	private ImageCacheManager imageCacheManager;
	private final LayoutInflater inflater;
	private final int SelfMessage = 0;
	private final int FriendMessage = 1;
	private final int SelfImgMsg = 2;
	private final int FriendImgMsg = 3;
	private final int NotifyMessage = 4;

	/**
	 * @param context
	 * @param resourceId
	 */
	public GroupMessageAdapter(Context context) {
		super(context, R.layout.home_list_item);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageCacheManager = new ImageCacheManager(context, UiUtil.dpToPx(
				context, 150), R.drawable.default_icon, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		GroupMessage message = (GroupMessage) this.getItem(position);
		int rowType=getRowType(message);
			if(rowType==NotifyMessage)
				return showNotificationMessage(message, convertView, parent);
			else
				return showChatMessages(message, convertView, parent, rowType);
	}

	private View showNotificationMessage(GroupMessage message, View convertView,
			ViewGroup parent){
		ViewHolder holder=new ViewHolder();;
		convertView=this.inflater.inflate(R.layout.row_chat_notify, parent, false);
		holder.message = (TextView) convertView
				.findViewById(R.id.chat_message);
		convertView.setTag(holder);
		boolean isNotifyByYou=true;
		String notificationMessage="";
		if (message.getSenderName() != null
				&& !message.getSenderName().equals(
						App42ChatApplication.getUserId()))
			isNotifyByYou=false;
		if(message.getMessageType()==MessageType.GroupIconChange)
			notificationMessage="changed the group icon";
		else
			notificationMessage="changed the group name";
		if(isNotifyByYou)
			holder.message.setText("You "+notificationMessage);
		else
			holder.message.setText(message.getSenderName()+" "+notificationMessage);
		return convertView;
	}
	private View showChatMessages(GroupMessage message, View convertView,
			ViewGroup parent, int rowType) {
		ViewHolder holder;
		convertView = getChatView(message, parent, rowType);
		holder = new ViewHolder();
		holder.time = (TextView) convertView.findViewById(R.id.chat_time);
		holder.displayName = (TextView) convertView
				.findViewById(R.id.chat_user);
		if (message.getMessageType() == MessageType.Image) {
			holder.icon = (ImageView) convertView.findViewById(R.id.chat_image);
		} else {
			holder.message = (TextView) convertView
					.findViewById(R.id.chat_message);
		}
		convertView.setTag(holder);
		holder.time.setText(Utility.getTime(message.getSendingTime()));
		if (message.getSenderName() != null
				&& !message.getSenderName().equals(
						App42ChatApplication.getUserId()))
			holder.displayName.setText(message.getSenderName());
		else
			holder.displayName.setVisibility(View.GONE);
		if (message.getMessageType() == MessageType.Image) {
			if (message.getMessage().startsWith("http"))
				imageCacheManager.loadImage(message.getMessage(), holder.icon);
			else
				holder.icon.setImageURI(Uri.parse(message.getMessage()));
		} else {
			holder.message.setText(UiUtil.getSmiledText(mContext,
					message.getMessage()));
		}
		return convertView;
	}

	private View getChatView(GroupMessage message, ViewGroup parent, int rowType) {

		switch (rowType) {
		case SelfMessage:
			return this.inflater.inflate(R.layout.row_chat_me, parent, false);

		case SelfImgMsg:
			return this.inflater.inflate(R.layout.row_image_me, parent, false);

		case FriendMessage:
			return this.inflater.inflate(R.layout.row_chat_friend, parent,
					false);

		case FriendImgMsg:
			return this.inflater.inflate(R.layout.row_image_friend, parent,
					false);
		case NotifyMessage:
			return this.inflater.inflate(R.layout.row_chat_notify, parent,
					false);

		}
		// }
		// else{
		// return this.inflater.inflate(R.layout.row_chat_notify, parent,
		// false);
		// }
		return null;
	}

	/**
	 * @param message
	 * @return
	 */
	private int getRowType(GroupMessage message) {
		if (message.getSenderName() == null
				|| message.getSenderName().equals(
						App42ChatApplication.getUserId())) {
			return getMyRowType(message.getMessageType());
		} else {
			return getFriendRowType(message.getMessageType());
		}
	}

	/**
	 * @param messageType
	 * @return
	 */
	private int getMyRowType(MessageType messageType) {
		if (messageType == MessageType.Text) {
			return SelfMessage;
		} else if (messageType == MessageType.Image) {
			return SelfImgMsg;
		} else {
			return NotifyMessage;
		}
	}

	/**
	 * @param messageType
	 * @return
	 */
	private int getFriendRowType(MessageType messageType) {
		if (messageType == MessageType.Text) {
			return FriendMessage;
		} else if (messageType == MessageType.Image) {
			return FriendImgMsg;
		} else {
			return NotifyMessage;
		}
	}
}
