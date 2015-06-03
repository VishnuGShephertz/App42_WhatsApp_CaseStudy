/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.adapter;

/**
 * @author Vishnu
 *
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.app42messanger.R;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.model.IUser;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 *
 */
public class FriendAdapter extends AbstractListAdapter<IUser> {
	
	private final LayoutInflater inflater;
	private ImageCacheManager imageCacher;
	public FriendAdapter(Context context) {
		super(context, R.layout.home_list_item);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageCacher = new ImageCacheManager(context, UiUtil.dpToPx(context, 50), R.drawable.default_icon,true);
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
		IUser user = this.getItem(position);
		holder.position = position;
		holder.displayName.setText(user.getName());
		if(user.getPicUri()!=null&&!user.getPicUri().isEmpty()){
			imageCacher.loadImage(user.getPicUri(), holder.icon );
		}
		else{
			holder.icon.setImageBitmap(UiUtil.getDefaultPicBitMap(mContext));
		}
	
		if(user.getMessage().startsWith("http"))
			holder.message.setText("Image");
		else
		holder.message.setText(UiUtil.getSmiledText(mContext, user.getMessage()));
		holder.time.setText(Utility.getTime(user.getLastCommTime()));
		return convertView;
	}

}