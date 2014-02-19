package com.liken.webservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import application.GlobalState;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.liken.data.SQLiteHelper;
import com.liken.photo_util.PhotoProcessor;
import com.liken.webservice.WebTask.WebTaskListener;

public class S3MediaUtil {
	Activity activity;
	public static final String ACCESS_KEY_ID = "ENTER YOUR KEY ID";
	public static final String SECRET_KEY = "ENTER YOUR SECRET KEY";
	SQLiteHelper sqlitehelper;
	public static String PICTURE_BUCKET = "likemind"
			+ ACCESS_KEY_ID.toLowerCase() + "test";
	public static final String PICTURE_BUCKET_PIC = "likemind"
			+ ACCESS_KEY_ID.toLowerCase() + "test";
	public static final String PICTURE_BUCKET_PROFILEPIC = "likemindasulue_profilepictures";
	// likemindAKIAJJPPM4267N4X7P7Qtest
	// likemindakiajjppm4267n4x7p7qtest

	String PICTURE_NAME = "NameOfThePicture";
	String User_ID;
	File pictureFile;
	S3MediaTaskListener s3MediaTaskListener;
	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY));

	public S3MediaUtil(Activity activity) {
		sqlitehelper = new SQLiteHelper(activity);
		this.activity = activity;
		User_ID = sqlitehelper.getUserID();
		s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
	}

	/*
	 * public static String getPictureBucket() { return ("my-unique-name" +
	 * ACCESS_KEY_ID + PICTURE_BUCKET) .toLowerCase(Locale.US); }
	 */

	public void setPictureName(String Name) {
		PICTURE_NAME = Name;
	}

	public class S3TaskResult {
		String errorMessage = null;
		String PICTURENAME = null;
		Uri uri = null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setPictureName(String picturename) {
			PICTURENAME = picturename;
		}

		public String getPictureName() {
			return PICTURENAME;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
	}

	public void S3PutObject(Uri Image, String... type) {
		if (type.length > 0) {
			if (type[0].equals(PICTURE_BUCKET_PROFILEPIC)) {
				PICTURE_BUCKET = PICTURE_BUCKET_PROFILEPIC;
			}
		}
		new S3PutObjectTask().execute(Image);
	}

	public void S3GetImageUri(String pictureName) {
		String URL = ((GlobalState) activity.getApplicationContext())
				.getPresignedURLforPicture(pictureName);
		if (URL != null) {
			// check whether the phone has downloaded the picture already
			// if yes
			S3TaskResult result = new S3TaskResult();
			result.setUri(Uri.parse(URL));
			s3MediaTaskListener.onCompleted(result);
		} else {
			// if no, get url from aws s3
			this.PICTURE_NAME = pictureName;
			new S3GeneratePresignedUrlTask().execute();
		}
	}

	public class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {

		protected void onPreExecute() {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			setPictureName(User_ID + timeStamp);
		}

		protected S3TaskResult doInBackground(Uri... uris) {

			if (uris == null || uris.length != 1) {
				return null;
			}

			// The file location of the image selected.
			Uri selectedImage = uris[0];

			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = activity.getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			pictureFile = new PhotoProcessor().fileProcessor(filePath);
			S3TaskResult result = new S3TaskResult();
			// save picture name into object
			result.setPictureName(PICTURE_NAME);
			// Put the image data into S3.
			try {
				// s3Client.createBucket(PICTURE_BUCKET);

				// Content type is determined by file extension.
				PutObjectRequest por = new PutObjectRequest(PICTURE_BUCKET,
						PICTURE_NAME, pictureFile);
				s3Client.putObject(por);
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			if (result.getErrorMessage() != null) {
				Log.i("S3Error", result.getErrorMessage());
			}
			pictureFile.delete();
			s3MediaTaskListener.onCompleted(result);
		}
	}

	public class S3GeneratePresignedUrlTask extends
			AsyncTask<Void, Void, S3TaskResult> {

		protected S3TaskResult doInBackground(Void... voids) {

			S3TaskResult result = new S3TaskResult();

			try {
				// Ensure that the image will be treated as such.
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				override.setContentType("image/jpeg");

				// Generate the presigned URL.

				// Added an hour's worth of milliseconds to the current time.
				Date expirationDate = new Date(
						System.currentTimeMillis() + 3600000);
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
						PICTURE_BUCKET, PICTURE_NAME);
				urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);

				URL url = s3Client.generatePresignedUrl(urlRequest);
				String urlString = url.toURI().toString();
				Log.i("S3Task", "Again!");
				((GlobalState) activity.getApplicationContext())
						.addUriforPicture(PICTURE_NAME, urlString);
				result.setUri(Uri.parse(urlString));

			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			if (result.getErrorMessage() != null) {
				Log.i("S3Error", result.getErrorMessage());
			} else if (result.getUri() != null) {

				// Display in Browser.
				// startActivity(new Intent(Intent.ACTION_VIEW,
				// result.getUri()));
			}
			s3MediaTaskListener.onCompleted(result);
		}
	}

	public static interface S3MediaTaskListener {
		void onCompleted(S3TaskResult data);

	}

	public void setS3MediaTaskListener(S3MediaTaskListener webTaskListener) {
		this.s3MediaTaskListener = webTaskListener;
	}

}
