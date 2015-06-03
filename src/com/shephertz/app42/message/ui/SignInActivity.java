/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.app42messanger.R;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.AppPreferences;
import com.shephertz.app42.message.backgroud.tast.WakefulIntentService;
import com.shephertz.app42.message.push.App42GCMManager;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.message.util.Logger;
import com.shephertz.app42.message.util.Utility;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;

/**
 * @author Vishnu
 * 
 */
/**
 * @author Vishnu
 *
 */
public class SignInActivity extends Activity {
	private EditText edCountryCode, edPhoneNo;
	private ProgressDialog progressDialog;
    private AppPreferences mPref;
 
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		edCountryCode = (EditText) findViewById(R.id.country_code);
		edPhoneNo = (EditText) findViewById(R.id.contact_number);
		edCountryCode.setText(Utility.getCountryCode(this));
		mPref=AppPreferences.instance(this);
		progressDialog = new ProgressDialog(this);
		WakefulIntentService.scheduleAlarms();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	public void onStart() {
		super.onStart();
		String userId=mPref.getUserId();
		if (userId!=null&&App42GCMManager.isApp42Registerd(this)){
			goToHomeScreen();
		}
		else{
		if (App42GCMManager.isPlayServiceAvailable(this)) {
			App42GCMManager.registerOnGCM();
		} else {
			Logger.i("No valid Google Play Services APK found.");
		}
		}
	}

	/**
	 * This function register Device on App42 console
	 * 
	 * @param view
	 */
	public void onRegisterClicked(View view) {
		String countryCode = edCountryCode.getText().toString();
		String contactNumber = edPhoneNo.getText().toString();
		String gcmDeviceId = App42GCMManager.getRegistrationId(this);
		if (countryCode.isEmpty() || contactNumber.isEmpty()) {
			ToastManager.showShort(this, R.string.mandotory_field);
			return;
		}
		if (gcmDeviceId.isEmpty()) {
			ToastManager.showShort(this, R.string.try_again);
			return;
		}
		if (!App42GCMManager.isApp42Registerd(this)){
			progressDialog.setMessage("Please wait..registering on App42");
			progressDialog.show();
			registerOnApp42(countryCode + contactNumber, gcmDeviceId);
		}
		else {
			goToHomeScreen();
		}
	}

	/**
	 * 
	 */
	private void onRegistrationSuccess() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				String user=edCountryCode.getText().toString()+edPhoneNo.getText().toString();
			    mPref.saveUserId(user);
				goToProfileScreen(user);
			}
		});
	} 
	/**
	 * @param message
	 */
	private void onError(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				ToastManager.showShort(SignInActivity.this, message);
				goToHomeScreen();
			}
		});
	}

	/**
	 * 
	 */
	private void goToHomeScreen() {
		String userId=mPref.getUserId();
		if(userId!=null){
		App42ChatApplication.setUserId(userId);
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
		}
	}

	/**
	 * 
	 */
	private void goToProfileScreen(String userId) {
		App42ChatApplication.setUserId(userId);
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(AppConstants.IsFromLogin, true);
		startActivity(intent);
		finish();
	}

	/**
	 * This function used to register GCM device Token on AppHQ
	 * 
	 * @param userName
	 * @param deviceToekn
	 * @param callBack
	 */
	private void registerOnApp42(final String userName, String deviceToekn) {
		new PushNotificationService().storeDeviceToken(userName,
				deviceToekn, new App42CallBack() {
					@Override
					public void onSuccess(Object arg0) {
						App42Response response = (App42Response) arg0;
						App42GCMManager.storeApp42Success(userName);
						onRegistrationSuccess();
					}
					@Override
					public void onException(Exception arg0) {
						// TODO Auto-generated method stub
						onError(arg0.getMessage());
					}
				});
	}
}
