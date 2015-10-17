package com.example.imageuploadtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements FileUploadObserver {

	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_LOAD_VIDEO = 1111;
	public static final int RESULT_LOAD_FILE = 11;
	private Button btn_select_image;
	private Button btn_upload;
	private Button btn_select_video;
	private Button btn_file;
	private String path = null;
	ProgressDialog dialog;
	String encodedString;
	File selectedFile;
	RequestParams params = new RequestParams();
	//
	private String[] mFileList;
	private File mPath = new File(Environment.getExternalStorageDirectory() + "//sdcard//");
	private String mChosenFile;
	private static final String FTYPE = ".txt";    
	private static final int DIALOG_LOAD_FILE = 1000;
	//
	Bitmap bitmap;
	TextView messageText;
	String upLoadServerUri = null;
	int serverResponseCode = 0;
	
	private final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_select_image 	= (Button)    findViewById(R.id.btn_select);
		btn_upload 			= (Button)    findViewById(R.id.btn_upload);
		btn_select_video 	= (Button) 	  findViewById(R.id.btn_select_video);
		btn_file 			= (Button)	  findViewById(R.id.btn_file);
	//	img_selected 		= (ImageView) findViewById(R.id.img_selected);
		
		upLoadServerUri = "http://www.weavebytes.com/iyans/upload.php"; //fileToUpload
		
		// button click to select photo from gallery
		btn_select_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}
		});
		// button to 
		btn_select_video.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, RESULT_LOAD_VIDEO);
			}
		});
		
		btn_upload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog = ProgressDialog.show(MainActivity.this, "",
						"Uploading file...", true);
				dialog.setCancelable(true);
				new Thread(new Runnable() {
					public void run() {
						runOnUiThread(new Runnable() {
							public void run() {
								//messageText.setText("uploading started.....");
							}
						});

						uploadFile(path);
					}
				}).start();
			}
		});
		btn_file.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FilePicker.class);          
	            startActivityForResult(intent, RESULT_LOAD_FILE);
				Toast.makeText(getApplicationContext(), "File Selected", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if image is to be clicked
		switch (requestCode) {

		case RESULT_LOAD_IMAGE:

			if (requestCode == RESULT_LOAD_IMAGE)
				if (resultCode == Activity.RESULT_OK) {
					Uri selectedImage = data.getData();
					path = GetPath(getApplicationContext(),
							selectedImage);

					Toast.makeText(getApplicationContext(), "File Path" + path,
							Toast.LENGTH_LONG).show();
				}
			break;
			case RESULT_LOAD_FILE:

				if (requestCode == RESULT_LOAD_FILE)
					if (resultCode == Activity.RESULT_OK) {
						
						 if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
			                	
			                    selectedFile = new File
			                    		(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
			                    path = selectedFile.getPath();  
			                    Toast.makeText(getApplicationContext(), "File Path" + path,
										Toast.LENGTH_LONG).show();
			                }
					}
				break;
			case RESULT_LOAD_VIDEO:

				if (requestCode == RESULT_LOAD_VIDEO)
					if (resultCode == Activity.RESULT_OK) {
						
						Uri selectedVideo = data.getData();
						
						path = GetPath(getApplicationContext(),
								selectedVideo);

						Toast.makeText(getApplicationContext(), "File Path" + path,
								Toast.LENGTH_LONG).show();
					}
				break;
				
		}//switch
	}

	// method to get path of image
	public static String GetPath(Context context, Uri uri) {

		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(uri, filePathColumn,
				null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();

		return picturePath;
	}

	public int uploadFile(String sourceFileUri) {

		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {

			dialog.dismiss();

			Log.e("uploadFile", "Source File not exist :" + path);

			runOnUiThread(new Runnable() {
				public void run() {
					messageText.setText("Source File not exist :" + path);
				}
			});

			return 0;
		}
		
		int ret = HttpUtils.uploadFile(upLoadServerUri, path, "fileToUpload", this);
		dialog.dismiss();
		
		return ret;

	} // End else block

	@Override
	public void uploadStatusUpdate(int error, long totalBytesRead, long totalBytes) {
		Log.d(TAG, "====> file upload totalBytesRead . :  " + totalBytesRead);
		Log.d(TAG, "====> file upload totalBytes . . . :  " + totalBytesRead);
		Log.d(TAG, "====> file upload progress . . . . :  " + (float) totalBytesRead/totalBytes);		
	}
	
		
}
