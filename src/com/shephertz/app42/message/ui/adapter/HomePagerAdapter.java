/**
 * 
 */
package com.shephertz.app42.message.ui.adapter;

import com.shephertz.app42.message.ui.ContactListFragment;
import com.shephertz.app42.message.ui.FriendListFragment;
import com.shephertz.app42.message.ui.GroupListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Vishnu
 *
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

	public HomePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Friend fragment List
			return new FriendListFragment();
		case 1:
			// Group fragment List
			return new GroupListFragment();
		case 2:
			// Contact fragment activity
			return new ContactListFragment();
		}
		return null;
	}
	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}