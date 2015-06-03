/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 *//*
package com.shephertz.app42.message.ui;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.backgroud.tast.PushChannel;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.listener.DBMessageListener;
import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.FriendMessage;
import com.shephertz.app42.message.model.Group;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.service.App42MessangerService;
import com.shephertz.app42.message.storage.DBChannel;
import com.shephertz.app42.message.storage.DBHelper;
import com.shephertz.app42.message.ui.adapter.AbstractListAdapter;
import com.shephertz.app42.message.ui.adapter.FriendMessageAdapter;
import com.shephertz.app42.message.ui.adapter.GroupMessageAdapter;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

*//**
 * @author Vishnu Garg
 * 
 *//*
public class GroupChatActivity extends Activity implements DBMessageListener {
	private Group group;
	private ArrayList<App42Message> messageList;
	private ListView listView;
	private AbstractListAdapter<App42Message> adapter;
	private ProgressDialog progressDialog;
	private EditText messageText;
	private ImageCacheManager imageManeger;
	private final int RequestCode = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_old);
		listView = (ListView) findViewById(R.id.chat_list);

		progressDialog = new ProgressDialog(this);
		messageText = (EditText) findViewById(R.id.chat_text);
		imageManeger = new ImageCacheManager(this, UiUtil.dpToPx(this, 50),
				R.drawable.default_icon, true);
		group = getIntent().getParcelableExtra(AppConstants.Group);
		DBChannel.fetchGroupMessages(group.getGroupId(), this,
				GroupChatActivity.this);
		buildChatHeader(group.getGroupName(), group.getPicUri());

	}

	private void buildChatHeader(String displayName, String picUri) {
		TextView textDisplayName = (TextView) findViewById(R.id.display_name);
		ImageView icon = (ImageView) findViewById(R.id.header_icon);
		if (picUri != null | picUri.isEmpty()) {
			imageManeger.loadImage(picUri, icon);
		}
		textDisplayName.setText(displayName);
	}

	public void onStart() {
		super.onStart();
	}

	public void loadIfo(View view) {
		Intent intent = new Intent(this, GroupInfoActivity.class);
		intent.putExtra(AppConstants.Group, group);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RequestCode && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			// picturePath.replaceAll(" ", "%20%");
			cursor.close();
			GroupMessage groupMessage = new GroupMessage(group.getGroupId(),
					group.getGroupName(), App42ChatApplication.getUserId(),
					picturePath, MessageType.Image,
					App42ChatApplication.getUserId());
			messageList.add(groupMessage);
			adapter.setData(messageList);
			adapter.notifyDataSetChanged();
			GroupMessage groupMessage2 = new GroupMessage(group.getGroupId(),
					group.getGroupName(), App42ChatApplication.getUserId(),
					picturePath, MessageType.Image,
					App42ChatApplication.getUserId());
			App42MessangerService.sendImageMessageToGroup(group.getGroupId(),
					picturePath, groupMessage2);
			// progressDialog.setMessage("Uploading imgae...wait");
			// progressDialog.show();
			// new UploadService().uploadFile("groupIcon"+new Date().getTime(),
			// picturePath, UploadFileType.IMAGE, "Imagesx", this);
		}
	}

	public void loadFromGallery(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RequestCode);
	}

	public void sendMessage(View view) {
		String message = messageText.getText().toString();
		sendMessageToGroup(message);

	}

	*//**
	 * @param message
	 *//*
	private void sendMessageToGroup(String message) {
		GroupMessage groupMessage = new GroupMessage(group.getGroupId(),
				group.getGroupName(), App42ChatApplication.getUserId(),
				message, MessageType.Text, App42ChatApplication.getUserId());
		messageList.add(groupMessage);
		adapter.setData(messageList);
		adapter.notifyDataSetChanged();
		DBHelper.instance(this).insertGroupMessage(groupMessage);
		try {
			GroupMessage groupMessage2 = new GroupMessage(group.getGroupId(),
					group.getGroupName(), App42ChatApplication.getUserId(),
					Utility.getDefaultMessage(), MessageType.Text,
					App42ChatApplication.getUserId());
			PushChannel.sendMessageToGroup(groupMessage2.getMessageJson()
					.toString(), group.getGroupId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBMessageListener#onGetGroupMessages
	 * (java.util.ArrayList)
	 
	@Override
	public void onMessagesFetched(final ArrayList<App42Message> appMessages) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				messageList = appMessages;
				progressDialog.dismiss();
				showList();
			}
		});

	}

	private void showList() {
		adapter = new GroupMessageAdapter(GroupChatActivity.this);
		adapter.setData(messageList);
		listView.setAdapter(adapter);
	}

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBMessageListener#onException(java
	 * .lang.Throwable)
	 
	@Override
	public void onException(Throwable th) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				messageList = new ArrayList<App42Message>();
				ToastManager.showShort(GroupChatActivity.this,
						"No Message Found");
				showList();
			}
		});
	}
}
*/