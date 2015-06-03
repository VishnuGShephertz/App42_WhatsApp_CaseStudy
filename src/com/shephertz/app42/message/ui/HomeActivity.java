/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.app42messanger.R;
import com.shephertz.app42.message.tools.ContactManager;
import com.shephertz.app42.message.ui.adapter.HomePagerAdapter;

/**
 * @author Vishnu
 * 
 */
public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private HomePagerAdapter mAdapter;
	private ActionBar actionBar;
	// private String[] tabs = { "Friends", "Groups", "Contacts" };
	private int[] icons = { R.drawable.buddy, R.drawable.group,
			R.drawable.contact };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new HomePagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Adding Tabs
		for (int i = 0; i < 3; i++) {
			actionBar.addTab(actionBar.newTab().setIcon(icons[i])
					.setTabListener(this));
		}
		//ContactManager.insertData(App42ChatApplication.getUserId());
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.group_menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//       Intent intent;
//		int id = item.getItemId();
//		switch (id) {
//		case R.id.action_call:
//			intent= new Intent(Intent.ACTION_DIAL);
//			startActivity(intent);
//			return true;
//		case R.id.action_refresh:
//			new ContactManager(HomeActivity.this).refreshContacts();
//			return true;
//		case R.id.action_profile:
//			intent=new Intent(HomeActivity.this,ProfileActivity.class);
//			startActivity(intent);
//			return true;
//		case R.id.action_create_group:
//			intent=new Intent(HomeActivity.this,GroupCreationActivity.class);
//			startActivity(intent);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		 Intent intent;
			int id = item.getItemId();
			switch (id) {
			case R.id.action_call:
				intent= new Intent(Intent.ACTION_DIAL);
				startActivity(intent);
				return true;
			case R.id.action_refresh:
				new ContactManager(HomeActivity.this).refreshContacts();
				return true;
			case R.id.action_profile:
				intent=new Intent(HomeActivity.this,ProfileActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_create_group:
				intent=new Intent(HomeActivity.this,GroupCreationActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onMenuItemSelected(featureId, item);
			}
		
	}

	@Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
	        // TODO Auto-generated method stub
	        MenuInflater inflater = getMenuInflater();
	        int currentTab =actionBar.getSelectedTab().getPosition();
	        Toast.makeText(getApplicationContext(), currentTab+"", Toast.LENGTH_SHORT);
	        menu.clear();
	        if (currentTab == 0) {
	            inflater.inflate(R.menu.friend_menu, menu);  //  menu for photospec.
	        } else if (currentTab == 1) {
	            inflater.inflate(R.menu.group_menu, menu);  //  menu for photospec.
	        }
	        else   {
	            inflater.inflate(R.menu.contact_menu, menu);  // menu for songspec
	        }
	        return super.onPrepareOptionsMenu(menu);
	    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1234 && resultCode == RESULT_OK) {
			String voice_text = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS).get(0);
			Toast.makeText(getApplicationContext(), voice_text,
					Toast.LENGTH_LONG).show();

		}
	}
}