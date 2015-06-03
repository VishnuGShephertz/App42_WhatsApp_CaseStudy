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
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 * 
 */
public class FriendMessageAdapter extends AbstractListAdapter<App42Message> {

	private ImageCacheManager imageCacheManager;
	private final LayoutInflater inflater;
	private final int SelfMessage = 0;
	private final int FriendMessage = 1;
	private final int SelfImgMsg = 2;
	private final int FriendImgMsg = 3;

	private final static String digits = "0123456789ABCDEF";
	/**
	 * @param context
	 * @param resourceId
	 */
	public FriendMessageAdapter(Context context) {
		super(context, 0);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageCacheManager = new ImageCacheManager(context, UiUtil.dpToPx(context, 150), R.drawable.default_icon,false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		App42Message message = this.getItem(position);
		//if (convertView == null) {
			convertView = getChatView(message, parent);
			holder = new ViewHolder();
			holder.time = (TextView) convertView.findViewById(R.id.chat_time);
			((TextView) convertView
			.findViewById(R.id.chat_user)).setVisibility(View.GONE);
			if (message.getMessageType() == MessageType.Image) {
				holder.icon = (ImageView) convertView
						.findViewById(R.id.chat_image);
			} else {
				holder.message = (TextView) convertView
						.findViewById(R.id.chat_message);
			}
			convertView.setTag(holder);
		//} else {
			holder = (ViewHolder) convertView.getTag();
		//}

		holder.time.setText(Utility.getTime(message.getSendingTime()));
		if (message.getMessageType() == MessageType.Image) {
			if(message.getMessage().startsWith("http"))
			imageCacheManager.loadImage(message.getMessage(), holder.icon);
			else
				holder.icon.setImageURI(Uri.parse(message.getMessage()));
		} else {
			holder.message.setText(UiUtil.getSmiledText(mContext, message.getMessage()));	
			//holder.message.setText(message.getMessage()+"\ud83d\ude01");
		}
		return convertView;
	}

	private String getString(){
		StringBuilder ss=new StringBuilder();
		for(int i=128513;i<128591;i++){
			ss.append(unicodeToJava((char)i));
			
		}
		return ss.toString();
	}
	 private static String unicodeToJava (char ch){
	        switch (ch) {
	            case 9:
	                return "\\t";
	            case 10:
	                return "\\n";
	            case 12:
	                return "\\f";
	            case 13:
	                return "\\r";
	            case 34:
	                return "\\\"";
	            case 92:
	                return "\\\\";
	            default:
	                break;
	        }
	        if (ch < 32 || ch > 126)
	            return "\\u" + getHexCode(ch);
	        return String.valueOf(ch);
	    }
	 private static String getHexCode(char ch){
	        return new String(new char[]{leastSignificantHexDigit(ch >>> 12), leastSignificantHexDigit(ch >>> 8), leastSignificantHexDigit(ch >>> 4), leastSignificantHexDigit(ch)});
	    }
	 private static char leastSignificantHexDigit(int ch){
	        return digits.charAt(ch & 0x0f);
	    }
	private View getChatView(App42Message message, ViewGroup parent) {

		switch (getRowType(message)) {
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

		}
		return null;
	}

	/**
	 * @param message
	 * @return
	 */
	private int getRowType(App42Message message) {
		if (message.getSenderId().equals(App42ChatApplication.getUserId())) {
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
		} 
		else
			return SelfMessage;
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
			return FriendMessage;
		}
	}
}
