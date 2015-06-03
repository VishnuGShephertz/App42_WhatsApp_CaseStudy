/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.db.DBChannel;
import com.shephertz.app42.message.listener.DBListener;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.Group;
import com.shephertz.app42.message.ui.adapter.ContactAdapter;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;

/**
 * @author Vishnu
 * 
 */
public class ContactListFragment extends Fragment implements DBListener,OnItemClickListener{
	private View mProgressBar;
	private ListView contactListView;
	private TextView messageView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout, container,
				false);
		mProgressBar = rootView.findViewById(R.id.progress_container);
		mProgressBar.setVisibility(View.GONE);
		contactListView = (ListView) rootView.findViewById(android.R.id.list);
		contactListView.setOnItemClickListener(this);
		messageView = (TextView) rootView.findViewById(android.R.id.empty);
		return rootView;
	}

	/*
	 * (non-Javadoc) This function checks which type of LeaderBoard is going to
	 * show
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	
		
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		setHasOptionsMenu(true);
		mProgressBar.setVisibility(View.VISIBLE);
		DBChannel.fetchAppContacts(getActivity(), this);
	}

	/** This function show message
	 * @param txtMessage
	 */
	private void showEmptyMessage() {
		mProgressBar.setVisibility(View.GONE);
		messageView.setVisibility(View.VISIBLE);
		contactListView.setVisibility(View.GONE);
	}
	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.DBListener#onAppContactsFetched(java.util.ArrayList)
	 */
	@Override
	public void onAppContactsFetched(final ArrayList<AppUser> appusers) {
		// TODO Auto-generated method stub
		if(getActivity()==null)
			return;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setVisibility(View.GONE);
				ContactAdapter contactAdapter=new ContactAdapter(getActivity());
				contactAdapter.setData(appusers);
				contactListView.setAdapter(contactAdapter);
				
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.DBListener#onException(java.lang.Throwable)
	 */
	@Override
	public void onException(Throwable th) {
		// TODO Auto-generated method stub
		if(getActivity()==null)
			return;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showEmptyMessage();
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		AppUser appUser = (AppUser)this.contactListView.getItemAtPosition(position);
		 Intent intent=new Intent(getActivity(),ChatActivity.class);
         intent.putExtra(AppConstants.Friend, appUser);
         intent.putExtra(AppConstants.IsGroup, false);
         startActivity(intent);
	}
	/**
	 * This function used to register GCM device Token on AppHQ
	 * 
	 * @param userName
	 * @param deviceToekn
	 * @param callBack
	 */
	private void registerOnApp42(String userName, String deviceToekn) {
		
		new PushNotificationService().storeDeviceToken(userName,
				deviceToekn, new App42CallBack() {
					@Override
					public void onSuccess(Object arg0) {
						App42Response response = (App42Response) arg0;
						
					}
					@Override
					public void onException(Exception arg0) {
						
					}
				});
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.message.listener.DBListener#onAppGroupsFetched(java.util.ArrayList)
	 */
	@Override
	public void onAppGroupsFetched(ArrayList<Group> appGroups) {
		// TODO Auto-generated method stub
		
	}
}
