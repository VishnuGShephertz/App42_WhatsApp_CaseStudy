/**
 * 
 */
package com.shephertz.app42.message.ui;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.backgroud.tast.PushChannel;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.db.DBChannel;
import com.shephertz.app42.message.db.DBHelper;
import com.shephertz.app42.message.listener.DBListener;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.Group;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.ui.adapter.FriendSelectionAdapter;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;

/**
 * @author Vishnu
 *
 */
public class FriendSelectionActivity extends Activity implements DBListener{
	private View mProgressBar;
	private ListView friendList;
	private ArrayList<String> selectedFriendList;
	private String groupName;
	private String groupIconUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layout);
		mProgressBar = findViewById(R.id.progress_container);
		mProgressBar.setVisibility(View.VISIBLE);
		friendList = (ListView)findViewById(android.R.id.list);
		
		selectedFriendList=new ArrayList<String>();
		groupName=getIntent().getStringExtra(AppConstants.GroupName);
		groupIconUri=getIntent().getStringExtra(AppConstants.GroupIcon);
		DBChannel.fetchAppContacts(this, this);
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.DBListener#onAppContactsFetched(java.util.ArrayList)
	 */
	@Override
	public void onAppContactsFetched(final ArrayList<AppUser> appusers) {
	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setVisibility(View.GONE);
				FriendSelectionAdapter friendSelectionAdapter=new FriendSelectionAdapter(FriendSelectionActivity.this);
				friendSelectionAdapter.setData(appusers);
				friendList.setAdapter(friendSelectionAdapter);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.DBListener#onException(java.lang.Throwable)
	 */
	@Override
	public void onException(Throwable th) {
	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showEmptyMessage();
			}
		});
	}
	
	/** This function show message
	 * @param txtMessage
	 */
	private void showEmptyMessage() {
		TextView messageView = (TextView) findViewById(android.R.id.empty);
		mProgressBar.setVisibility(View.GONE);
		messageView.setVisibility(View.VISIBLE);
		friendList.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.friend_selection, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case R.id.action_create:
			createGroup(""+new Date().getTime(),groupName);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void onGroupCreated(final String groupId,final String groupname){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastManager.showShort(FriendSelectionActivity.this, R.string.group_creation_success);
				GroupMessage grpMesage=new GroupMessage(App42ChatApplication.getUserId(), "Me", groupId, groupname,groupIconUri);
				DBHelper.instance(FriendSelectionActivity.this).createNewGroup(grpMesage);
				PushChannel.subcribeDevicesToGroup(selectedFriendList, App42ChatApplication.getUserId(), groupId);
				//finish();
				subscribeUser(groupId, App42ChatApplication.getUserId());
				for(String user:selectedFriendList){
					subscribeUser(groupId, user);
				}
				GroupMessage groupMessage = new GroupMessage(groupId,
						groupname, App42ChatApplication.getUserId(),
						"Hello", MessageType.Text,
						App42ChatApplication.getUserId(),groupIconUri);
				try {
					PushChannel.sendMessageToGroup(groupMessage.getMessageJson()
							.toString(), groupId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void subscribeUser(String grpId,String userId){
		new PushNotificationService().subscribeToChannel(
				grpId, userId,new App42CallBack() {
					
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
	private void onError(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ToastManager.showShort(FriendSelectionActivity.this, message);
				
			}
		});
	}
	
	private void createGroup(final String channelName,final String groupName){
		new PushNotificationService().createChannelForApp(channelName, "Hello", new App42CallBack() {
			
			@Override
			public void onSuccess(Object arg0) {
				// TODO Auto-generated method stub
				onGroupCreated(channelName,groupName);
			}
			
			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				onError(arg0.getMessage());
			}
		});
	}
	
	public void addAndRemoveFriend(String friendId,boolean isChecked){
		if(isChecked)
			selectedFriendList.add(friendId);
		else{
			if(selectedFriendList.contains(friendId))
				selectedFriendList.remove(friendId);
		}
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.DBListener#onAppGroupsFetched(java.util.ArrayList)
	 */
	@Override
	public void onAppGroupsFetched(ArrayList<Group> appGroups) {
		// TODO Auto-generated method stub
		
	}
}
