/**
 * 
 */
package com.shephertz.app42.message.ui;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.AppPreferences;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.backgroud.tast.PushChannel;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.db.DBChannel;
import com.shephertz.app42.message.db.DBHelper;
import com.shephertz.app42.message.listener.App42CallBackListener;
import com.shephertz.app42.message.listener.DBListener;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.Group;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.service.App42MessangerService;
import com.shephertz.app42.message.ui.adapter.ContactAdapter;
import com.shephertz.app42.message.ui.adapter.GroupUserAdapter;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;

/**
 * @author Vishnu
 * 
 */
public class GroupInfoActivity extends Activity implements
		App42CallBackListener, DBListener {

	private Group group;
	private ImageCacheManager imageManeger;
	private ListView listView;
	private ProgressDialog progressDialog;
	private ArrayList<AppUser> groupUserList;
	private DBHelper dbHelper;
	private AppPreferences myPref;
	private String ownerId;
	private final int RequestCode = 1;
	ImageView groupIcon;
	TextView textDisplayName;
	private ImageCacheManager imageManager;
	static boolean isUnsubscribed=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_info);
		listView = (ListView) findViewById(R.id.list_view);
		imageManeger = new ImageCacheManager(this, UiUtil.dpToPx(this, 50),
				R.drawable.default_icon, true);
		group = getIntent().getParcelableExtra(AppConstants.Group);
		dbHelper = DBHelper.instance(this);
		myPref = AppPreferences.instance(this);
		buildHeader(group.getGroupName(), group.getPicUri());
		progressDialog = new ProgressDialog(this);
		showProgressDialog(App42ChatApplication.getMessage(R.string.fetching_group_members));
		App42MessangerService.getGroupUsers(group.getGroupId(), this);
		imageManager = new ImageCacheManager(this, UiUtil.dpToPx(this, 50),
				R.drawable.default_icon, true);
	}

	private void showProgressDialog(String message) {
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	private void buildHeader(String displayName, String picUri) {
		textDisplayName = (TextView) findViewById(R.id.group_name);
		TextView textOwnerName = (TextView) findViewById(R.id.group_owner);
		groupIcon = (ImageView) findViewById(R.id.group_pic);
		if (picUri != null &&! picUri.isEmpty()) {
			if (picUri.startsWith("http"))
				imageManeger.loadImage(picUri, groupIcon);
			else
				groupIcon.setImageURI(Uri.parse(picUri));
		}
		textDisplayName.setText(displayName);
		ownerId = dbHelper.getGroupOwnerName(group.getGroupId());
		if (ownerId.equalsIgnoreCase(App42ChatApplication.getUserId())) {
			((ImageView) findViewById(R.id.add_friends))
					.setVisibility(View.VISIBLE);
			textOwnerName.setText("Created By : " + myPref.getProfileName());
		} else
			textOwnerName.setText("Created By : "
					+ dbHelper.getDisplayNameByUserId(ownerId));
	}

	private void loadUserList() {
		GroupUserAdapter groupUserAdapter = new GroupUserAdapter(
				GroupInfoActivity.this);
		groupUserAdapter.setData(groupUserList);
		listView.setAdapter(groupUserAdapter);
	}

	public void onImgaeChange(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RequestCode);
	}

	public void onNameChange(View view) {
		showPopup();
	}

	private void changeGroupName(String groupName) {
		if (groupName == null || groupName.isEmpty()
				|| groupName.equals(group.getGroupName())) {
			ToastManager.showShort(this, "Please enter a valid name");
			return;
		}
		textDisplayName.setText(groupName);
		GroupMessage groupMessage = new GroupMessage(group.getGroupId(),
				group.getGroupName(), App42ChatApplication.getUserId(),
				groupName, MessageType.GroupNameChange,
				App42ChatApplication.getUserId());
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
	 * Create and return an example alert dialog with an edit text box.
	 */
	private Dialog showPopup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Change GroupName");
		final EditText input = new EditText(this);
		input.setText(group.getGroupName());
		builder.setView(input);
		builder.setPositiveButton("Change",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String newGroupName = input.getText().toString();
						changeGroupName(newGroupName);
					}
				});
		return builder.show();
	}

	public void onAddFriends(View view) {
		DBChannel.fetchAppContacts(GroupInfoActivity.this, this);
	}

	public void onGroupExit(View view) {
		showProgressDialog(App42ChatApplication.getMessage(R.string.unsubscribe_group_message));
		App42MessangerService.exitFromGroup(App42ChatApplication.getUserId(), group.getGroupId(), this);
		
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
			groupIcon.setImageBitmap(UiUtil.getRoundBitmapFromLocal(this,
					picturePath));
			cursor.close();
			showProgressDialog(App42ChatApplication.getMessage(R.string.upload_image_message));
			App42MessangerService.uploadImage(picturePath, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBListener#onAppContactsFetched(
	 * java.util.ArrayList)
	 */
	@Override
	public void onAppContactsFetched(final ArrayList<AppUser> appusers) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showContactList(appusers);
			}
		});
	}

	private void showContactList(final ArrayList<AppUser> appusers) {
		final Dialog dialog = new Dialog(GroupInfoActivity.this);
		dialog.setContentView(R.layout.fragment_layout);
		dialog.setTitle("Select Contact");
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		ListView contactListView = (ListView) dialog
				.findViewById(android.R.id.list);
		ContactAdapter contactAdapter = new ContactAdapter(
				GroupInfoActivity.this);
		contactAdapter.setData(appusers);
		contactListView.setAdapter(contactAdapter);
		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				AppUser appUser = appusers.get(arg2);
				subscribeUser(appUser.getUserId());
				dialog.dismiss();
			}

		});
		dialog.show();
	}

	private void subscribeUser(String userId) {
		new PushNotificationService().subscribeToChannel(group.getGroupId(),
				userId, new App42CallBack() {

					@Override
					public void onSuccess(Object arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onException(Exception arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBListener#onAppGroupsFetched(java
	 * .util.ArrayList)
	 */
	@Override
	public void onAppGroupsFetched(ArrayList<Group> appGroups) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.DBListener#onException(java.lang
	 * .Throwable)
	 */
	@Override
	public void onException(Throwable th) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.listener.App42CallBackListener#
	 * onImageUploadSuccess(java.lang.String)
	 */
	@Override
	public void onImageUploadSuccess(final String imageUrl) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog.isShowing())
					progressDialog.dismiss();
				imageManager.loadImage(imageUrl, groupIcon);
				GroupMessage groupMessage = new GroupMessage(
						group.getGroupId(), group.getGroupName(),
						App42ChatApplication.getUserId(), imageUrl,
						MessageType.GroupIconChange,
						App42ChatApplication.getUserId());
				DBHelper.instance(GroupInfoActivity.this).insertGroupMessage(
						groupMessage);
				App42MessangerService.sendImageMessageToGroup(
						group.getGroupId(), imageUrl, groupMessage);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.listener.App42CallBackListener#
	 * onCallBackException(java.lang.String)
	 */
	@Override
	public void onCallBackException(final String messsage) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog.isShowing())
					progressDialog.dismiss();
				if (messsage != null)
					ToastManager.showShort(GroupInfoActivity.this, messsage);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.listener.App42CallBackListener#
	 * onGroupUsersFetched(java.util.ArrayList)
	 */
	@Override
	public void onGroupUsersFetched(
			final ArrayList<PushNotification> pushNotification) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog.isShowing())
					progressDialog.dismiss();
				groupUserList = new ArrayList<AppUser>();
				for (int i = 0; i < pushNotification.size(); i++) {
					if (pushNotification.get(i).getUserName()
							.equals(App42ChatApplication.getUserId()))
						groupUserList.add(new AppUser(App42ChatApplication
								.getUserId(), myPref.getProfileName(), myPref
								.getProfileUri(), myPref.getProfileStatus()));
					else
						groupUserList.add(dbHelper.getUserById(pushNotification
								.get(i).getUserName()));
				}
				loadUserList();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.App42CallBackListener#onUnsubscribedFromChannel()
	 */
	@Override
	public void onUnsubscribedFromChannel() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog.isShowing())
					progressDialog.dismiss();
			ToastManager.showShort(GroupInfoActivity.this, R.string.unsubscribe_group_success_message);
			dbHelper.deleteGroup(group.getGroupId());
			isUnsubscribed=true;
			finish();
			}
		});
	}

}
