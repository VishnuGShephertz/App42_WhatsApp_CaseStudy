/**
 * 
 */
package com.shephertz.app42.message.ui;

/**
 * @author Vishnu
 *
 */
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.AppPreferences;
import com.shephertz.app42.message.backgroud.tast.ImageCacheManager;
import com.shephertz.app42.message.listener.App42CallBackListener;
import com.shephertz.app42.message.service.App42MessangerService;
import com.shephertz.app42.message.tools.ContactManager;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;

/**
 * @author Vishnu
 * 
 */
public class ProfileActivity extends Activity implements App42CallBackListener {

	private EditText statusText, profileText;
	private final int RequestCode = 1;
	private ImageView profileIcon;
	private AppPreferences myPref;
	private String profileIconUri;
	private ProgressDialog progressDialog;
	private ImageCacheManager imageManager;
	private boolean isFromlogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		profileIcon = (ImageView) findViewById(R.id.profile_pic);
		statusText = (EditText) findViewById(R.id.profile_status);
		profileText = (EditText) findViewById(R.id.profile_name);
		myPref = AppPreferences.instance(this);
		progressDialog = new ProgressDialog(this);
		statusText.setText(myPref.getProfileStatus());
		profileText.setText(myPref.getProfileName());
		imageManager = new ImageCacheManager(this, UiUtil.dpToPx(this, 50),
				R.drawable.default_icon, true);
		imageManager.loadImage(myPref.getProfileUri(), profileIcon);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.create_grp, menu);
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
		case R.id.action_next:
			String status = statusText.getText().toString();
			myPref.updateProfile(profileIconUri, status, profileText.getText()
					.toString());
			if (!isFromlogin)
				App42MessangerService.updateUserProfile(profileIconUri, status,
						App42ChatApplication.getUserId());
			else {
				App42MessangerService.createUserProfile(profileIconUri, status,
						App42ChatApplication.getUserId());
				Intent intent = new Intent(this, HomeActivity.class);
				startActivity(intent);
			}
			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isFromlogin = getIntent().getBooleanExtra(AppConstants.IsFromLogin,
				false);
		if (isFromlogin)
			new ContactManager(this).refreshContacts();
	}

	/**
	 * @param view
	 */
	public void onImageSelectionClick(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RequestCode);
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
			String picPath = cursor.getString(columnIndex);
			profileIcon.setImageBitmap(UiUtil.getRoundBitmapFromLocal(this,
					picPath));
			cursor.close();
			showDilaog("Uploading image..wait");
			App42MessangerService.uploadImage(picPath, this);
		}
	}

	private void showDilaog(String message) {
		progressDialog.setMessage(message);
		progressDialog.show();
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
				profileIcon.setImageDrawable(null);
				profileIconUri = imageUrl;
				imageManager.loadImage(imageUrl, profileIcon);
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
					ToastManager.showShort(ProfileActivity.this, messsage);
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
	public void onGroupUsersFetched(ArrayList<PushNotification> pushNotification) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.listener.App42CallBackListener#
	 * onUnsubscribedFromChannel()
	 */
	@Override
	public void onUnsubscribedFromChannel() {
		// TODO Auto-generated method stub

	}

}
