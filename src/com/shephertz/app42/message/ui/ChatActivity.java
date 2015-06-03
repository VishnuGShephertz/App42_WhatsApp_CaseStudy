/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.backgroud.tast.PushChannel;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.db.DBChannel;
import com.shephertz.app42.message.db.DBHelper;
import com.shephertz.app42.message.listener.DBMessageListener;
import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.FriendMessage;
import com.shephertz.app42.message.model.Group;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.service.App42MessangerService;
import com.shephertz.app42.message.ui.adapter.AbstractListAdapter;
import com.shephertz.app42.message.ui.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.shephertz.app42.message.ui.adapter.EmoticonsPagerAdapter;
import com.shephertz.app42.message.ui.adapter.FriendMessageAdapter;
import com.shephertz.app42.message.ui.adapter.GroupMessageAdapter;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu Garg
 * 
 */
public class ChatActivity extends FragmentActivity implements
		DBMessageListener, KeyClickListener {

	private AppUser friend;
	private Group group;
	// private ArrayList<App42Message> messageList;
	private ListView listView;
	private AbstractListAdapter<App42Message> adapter;
	private ProgressDialog progressDialog;
	private EditText messageText;
	private ImageCacheManager imageManeger;
	private final int RequestCode = 1;
	private LinearLayout emoticonsFooter;
	private PopupWindow popupWindow;
	private View popUpView;
	private int keyboardHeight;
	private boolean isKeyBoardVisible;
	private LinearLayout parentLayout;
	private ArrayList<String> currentEmo = new ArrayList<String>();
	private boolean isGroup = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		listView = (ListView) findViewById(R.id.chat_list);
		emoticonsFooter = (LinearLayout) findViewById(R.id.footer_for_emoticons);
		popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);
		parentLayout = (LinearLayout) findViewById(R.id.chat_list_parent);
		progressDialog = new ProgressDialog(this);
		messageText = (EditText) findViewById(R.id.chat_text);
		imageManeger = new ImageCacheManager(this, UiUtil.dpToPx(this, 50),
				R.drawable.default_icon, true);
		final float popUpheight = getResources().getDimension(
				R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);
		loadMessages();
		listView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow.isShowing())
					popupWindow.dismiss();
				return false;
			}
		});
		enablePopUpView();
		checkKeyboardHeight(parentLayout);
	}
	
	

	private void loadMessages() {
		isGroup = getIntent().getBooleanExtra(AppConstants.IsGroup, false);
		if (isGroup) {
			group = getIntent().getParcelableExtra(AppConstants.Group);
			if (group == null)
				finish();
			else {
				String [] updatedInfo=DBHelper.instance(this).getGroupNameAndPicUrl(group.getGroupId());
				if(updatedInfo!=null){
					group.setGroupName(updatedInfo[0]);
					group.setPicUri(updatedInfo[1]);
				}
				buildChatHeader(group.getGroupName(), group.getPicUri());
				DBChannel.fetchGroupMessages(group.getGroupId(), this,
						ChatActivity.this);
			}
		} else {

			friend = getIntent().getParcelableExtra(AppConstants.Friend);
			if (friend == null)
				finish();
			else {
				buildChatHeader(friend.getName(), friend.getPicUri());
				DBChannel.fetchFriendMessages(friend.getUserId(), this,
						ChatActivity.this);
			}
		}
	}

	/**
	 * @param height
	 */
	private void changeKeyboardHeight(int height) {
		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsFooter.setLayoutParams(params);
		}
	}

	private void buildChatHeader(String displayName, String picUri) {
		TextView textDisplayName = (TextView) findViewById(R.id.display_name);
		ImageView icon = (ImageView) findViewById(R.id.header_icon);
		if (picUri != null &&! picUri.isEmpty()) {
			imageManeger.loadImage(picUri, icon);
		}
		textDisplayName.setText(displayName);
	}

	public void onChatContentClick(View view) {
		if (popupWindow.isShowing())
			popupWindow.dismiss();
	}

	public void onPopupEmotions(View view) {
		if (!popupWindow.isShowing()) {
			popupWindow.setHeight((int) (keyboardHeight));
			if (isKeyBoardVisible) {
				emoticonsFooter.setVisibility(LinearLayout.GONE);
			} else {
				emoticonsFooter.setVisibility(LinearLayout.VISIBLE);
			}
			popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);

		} else {
			popupWindow.dismiss();
		}

	}

	public void onStart() {
		super.onStart();
		loadMessages();
		if(isGroup&&GroupInfoActivity.isUnsubscribed){
			GroupInfoActivity.isUnsubscribed=false;
			finish();
		}
	}

	public void loadIfo(View view) {
		if (isGroup) {
			Intent intent = new Intent(this, GroupInfoActivity.class);
			intent.putExtra(AppConstants.Group, group);
			startActivity(intent);
		}
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
			cursor.close();
			if (!isGroup)
				sendImageMessageToFriend(picturePath);
			else
				sendImageMessageToGroup(picturePath);
		}
	}

	private void sendImageMessageToFriend(String picturePath) {
		FriendMessage friendMessage = new FriendMessage(
				App42ChatApplication.getUserId(), picturePath,
				MessageType.Image);
		adapter.add(friendMessage);
		adapter.notifyDataSetChanged();
		DBHelper.instance(this).insertMyMessage(friendMessage,friend.getUserId());
//		FriendMessage friendMessage2 = new FriendMessage(friend.getUserId(),
//				picturePath, MessageType.Image);
		App42MessangerService.sendImageMessageToFriend(friend.getUserId(),
				picturePath, friendMessage);
	}

	private void sendImageMessageToGroup(String picturePath) {
		GroupMessage groupMessage = new GroupMessage(group.getGroupId(),
				group.getGroupName(), App42ChatApplication.getUserId(),
				picturePath, MessageType.Image,
				App42ChatApplication.getUserId());
		adapter.add(groupMessage);
		adapter.notifyDataSetChanged();
		DBHelper.instance(this).insertGroupMessage(groupMessage);

		App42MessangerService.sendImageMessageToGroup(group.getGroupId(),
				picturePath, groupMessage);
	}

	public void loadFromGallery(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RequestCode);
	}

	public void sendMessage(View view) {
		if (messageText.getText().toString().length() > 0) {
			Spanned sp = messageText.getText();
			String formattedMessage = getFormattedMessage(messageText.getText()
					.toString());
			messageText.setText("");
			currentEmo.clear();
			if (isGroup)
				sendMessageToGroup(formattedMessage);
			else
				sendMessageTofriend(formattedMessage);
		}

	}

	/**
	 * @param message
	 */
	private void sendMessageToGroup(String message) {
		GroupMessage groupMessage = new GroupMessage(group.getGroupId(),
				group.getGroupName(), App42ChatApplication.getUserId(),
				message, MessageType.Text, App42ChatApplication.getUserId());
		adapter.add(groupMessage);
		adapter.notifyDataSetChanged();
		DBHelper.instance(this).insertGroupMessage(groupMessage);
		try {
			PushChannel.sendMessageToGroup(groupMessage.getMessageJson()
					.toString(), group.getGroupId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param message
	 */
	private void sendMessageTofriend(String message) {
		FriendMessage friendMessage = new FriendMessage(
				App42ChatApplication.getUserId(), message, MessageType.Text);
		adapter.add(friendMessage);
		adapter.notifyDataSetChanged();
		DBHelper.instance(this).insertMyMessage(friendMessage,
				friend.getUserId());
		try {
			PushChannel.sendMessageToFriend(friendMessage.getMessageJson()
					.toString(), friend.getUserId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBMessageListener#onGetGroupMessages
	 * (java.util.ArrayList)
	 */
	@Override
	public void onMessagesFetched(final ArrayList<App42Message> appMessages) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				showList(appMessages);
			}
		});

	}

	private void enablePopUpView() {
		ViewPager pager = (ViewPager) popUpView
				.findViewById(R.id.emoticons_pager);
		pager.setOffscreenPageLimit(2);
		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(
				ChatActivity.this, UiUtil.emotionsPatterns, this);
		pager.setAdapter(adapter);
		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);
		ImageView backSpace = (ImageView) popUpView.findViewById(R.id.back);
		backSpace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
						0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				messageText.dispatchKeyEvent(event);
			}
		});
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				emoticonsFooter.setVisibility(LinearLayout.GONE);
			}
		});
	}

	private void showList(final ArrayList<App42Message> appMessages) {
		if (isGroup)
			adapter = new GroupMessageAdapter(ChatActivity.this);
		else
			adapter = new FriendMessageAdapter(ChatActivity.this);
		adapter.setData(appMessages);
		listView.setAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBMessageListener#onException(java
	 * .lang.Throwable)
	 */
	@Override
	public void onException(Throwable th) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				ToastManager.showShort(ChatActivity.this, "No Message Found");
				showList(new ArrayList<App42Message>());
			}
		});
	}

	private String getFirstEmotion() {
		if (currentEmo.size() > 0)
			return currentEmo.remove(0);
		else
			return "";
	}

	private String getFormattedMessage(String message) {
		String newMessage = "";
		for (int i = 0; i < message.length(); i++) {
			int val = message.charAt(i);
			if (val > 255) {
				System.out.println(val);
				newMessage += getFirstEmotion();
			} else
				newMessage += message.charAt(i);
		}
		return newMessage;
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	int previousHeightDiffrence = 0;

	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {

						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);
						if (previousHeightDiffrence - heightDifference > 50) {
							popupWindow.dismiss();
						}
						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {
							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {
							isKeyBoardVisible = false;

						}

					}
				});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.ui.adapter.EmoticonsGridAdapter.KeyClickListener
	 * #keyClickedIndex(int, java.lang.String)
	 */
	@Override
	public void keyClickedIndex(final int resId, String emotion) {
		// TODO Auto-generated method stub
		ImageGetter imageGetter = new ImageGetter() {
			public Drawable getDrawable(String source) {
				Drawable d = getResources().getDrawable(resId);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};
		Spanned cs = Html.fromHtml("<img src ='" + resId + "'/>", imageGetter,
				null);
		currentEmo.add(emotion);
		int cursorPosition = messageText.getSelectionStart();
		messageText.getText().insert(cursorPosition, cs);
	}
}
