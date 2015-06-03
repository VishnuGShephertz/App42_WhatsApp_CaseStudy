package com.shephertz.app42.message.ui.adapter;

import java.util.ArrayList;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.ui.adapter.EmoticonsGridAdapter.KeyClickListener;

public class EmoticonsPagerAdapter extends PagerAdapter {

	String[] emoticons;
	private static final int NO_OF_EMOTICONS_PER_PAGE = 20;
	FragmentActivity mActivity;
	KeyClickListener mListener;

	public EmoticonsPagerAdapter(FragmentActivity activity,
			String[] emoticons, KeyClickListener listener) {
		this.emoticons = emoticons;
		this.mActivity = activity;
		this.mListener = listener;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil((double) emoticons.length
				/ (double) NO_OF_EMOTICONS_PER_PAGE);
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		View layout = mActivity.getLayoutInflater().inflate(
				R.layout.emoticons_grid, null);

		int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE;
		ArrayList<String> emoticonsInAPage = new ArrayList<String>();

		for (int i = initialPosition; i < initialPosition
				+ NO_OF_EMOTICONS_PER_PAGE
				&& i < emoticons.length; i++) {
			emoticonsInAPage.add(emoticons[i]);
		}

		GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
		EmoticonsGridAdapter adapter = new EmoticonsGridAdapter(
				mActivity.getApplicationContext(), emoticonsInAPage, position,
				mListener);
		grid.setAdapter(adapter);

		((ViewPager) collection).addView(layout);

		return layout;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}