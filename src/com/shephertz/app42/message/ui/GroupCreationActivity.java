/**
 * 
 */
package com.shephertz.app42.message.ui;

import java.util.Date;

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
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.ui.util.ToastManager;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

/**
 * @author Vishnu
 *
 */
public class GroupCreationActivity extends Activity implements App42CallBack{
	private EditText groupName;
	private final int RequestCode=1;
	private ProgressDialog progressDialog;
	private String grpIconUri="";
	private ImageView groupIcon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_creation);
		groupIcon=(ImageView)findViewById(R.id.group_pic);
		groupName=(EditText)findViewById(R.id.group_name);
		progressDialog=new ProgressDialog(this);
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
			String group=groupName.getText().toString();
			if(group!=null||!group.isEmpty()){
			Intent intent2=new Intent(GroupCreationActivity.this,FriendSelectionActivity.class);
			intent2.putExtra(AppConstants.GroupName, group);
			intent2.putExtra(AppConstants.GroupIcon, grpIconUri);
			startActivity(intent2);
			finish();
			}
			else{
				ToastManager.showShort(GroupCreationActivity.this, R.string.group_mandatory);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/**
	 * @param view
	 */
	public void onImageSelectionClick(View view){
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RequestCode);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RequestCode && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			//picturePath.replaceAll(" ", "%20%");
			cursor.close();
			groupIcon.setImageURI(Uri.parse(picturePath));
			progressDialog.setMessage("Uploading imgae...wait");
			progressDialog.show();
			new UploadService().uploadFile("groupIcon"+new Date().getTime(), picturePath, UploadFileType.IMAGE, "Imagesx", this);
		}
	}
	/* (non-Javadoc)
	 * @see com.shephertz.app42.paas.sdk.android.App42CallBack#onException(java.lang.Exception)
	 */
	@Override
	public void onException(final Exception arg0) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				ToastManager.showShort(GroupCreationActivity.this, arg0.getMessage());
			}
		});
	}
	/* (non-Javadoc)
	 * @see com.shephertz.app42.paas.sdk.android.App42CallBack#onSuccess(java.lang.Object)
	 */
	@Override
	public void onSuccess(final Object uploadObj) {
		// TODO Auto-generated method stub
runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				Upload upload = (Upload) uploadObj;
				grpIconUri = upload.getFileList().get(0).getUrl();
			}
		});
	}
}
