package com.liken.photo_util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class PhotoProcessor {
	
	
	
	public File fileProcessor(String picturePath) {
		// decode the file and resize to 720 x 1024
		//Bitmap bitmap = decodeSampledBitmapFromPath(picturePath, 720, 1024);
		
		Bitmap bitmap = decodeSampledBitmapFromPath(picturePath, 720,1024);
		if (bitmap == null) {
			return null;
		}

		// detect and adjust the picture's orientation
		try {
			ExifInterface exif = new ExifInterface(picturePath);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 1);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			} else if (orientation == 3) {
				matrix.postRotate(180);
			} else if (orientation == 8) {
				matrix.postRotate(270);
			}
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true); // rotating bitmap
		} catch (Exception e) {
			e.printStackTrace();
		}

		// convert the bitmap to file for uploading
		String file_path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/LikeMind";
		File dir = new File(file_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File file2 = new File(dir, "LikeMind" + timeStamp + ".png");
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file2);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		return file2;

	}
	
	public Bitmap adjustPhotoRotation(Uri uri,Activity activity){
		Bitmap bitmap=null;
		// detect and adjust the picture's orientation
		
		// The file location of the image selected.
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = activity.getContentResolver().query(uri,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
		try {
			ExifInterface exif = new ExifInterface(filePath);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 1);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			} else if (orientation == 3) {
				matrix.postRotate(180);
			} else if (orientation == 8) {
				matrix.postRotate(270);
			}
			bitmap=decodeSampledBitmapFromPath(filePath,
					720, 1024);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true); // rotating bitmap
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
       
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromPath(String picturePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(picturePath, options);
	}

}
