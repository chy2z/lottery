package cn.lottery.framework.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.lottery.framework.SApplication;

public class ImageCompress {
	
	/*
	public static  File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, 
				".jpg", 
				storageDir 
		);
		return image;
	}
    */

	public static int getRotationFromMediaStore(Context context, Uri imageUri) {
		String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
		Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
		if (cursor == null) return 0;
		cursor.moveToFirst();
		int orientationColumnIndex = cursor.getColumnIndex(columns[1]);
		int orientation = cursor.getInt(orientationColumnIndex);
		cursor.close();
		return orientation;
	}

	private static int getImageOrientation(Uri imageUri){
		final String[] imageColumns = {
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DATE_TAKEN,
				MediaStore.Images.Media.ORIENTATION,
				MediaStore.Images.Thumbnails.DATA
		};
		final String imageOrderBy = MediaStore.Images.Media.DATE_TAKEN+" DESC";
		Cursor cursor = SApplication.getContext().getContentResolver().query(imageUri,
				imageColumns, null, null, imageOrderBy);
		if(cursor==null) return 0;
		if(cursor.moveToFirst()){
			int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
			cursor.close();
			return orientation;
		} else {
			return 0;
		}
	}



	/**
	 * 获取选择角度
	 * @param path
	 * @return
     */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}


	/**
	 * 旋转图片
	 *
	 * @param angle 角度
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(),
				matrix, true);
		return resizedBitmap;
	}

	/**
	 * 压缩图像
	 * @param fileUri
	 * @return
     */
	public static File scal(Uri fileUri){
		String path = fileUri.getPath();
		//int degree=getRotationFromMediaStore(SApplication.getContext(),fileUri);
		//degree=90;测试使用
		//int degree=getImageOrientation(fileUri);
		int degree=0;
		File outputFile = new File(path);
		long fileSize = outputFile.length();
		final long fileMaxSize = 200 * 1024;
		if (fileSize >= fileMaxSize) {
	            BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inJustDecodeBounds = true;
	            BitmapFactory.decodeFile(path, options);
	            int height = options.outHeight;
	            int width = options.outWidth;

	            double scale = Math.sqrt((float) fileSize / fileMaxSize);
	            options.outHeight = (int) (height / scale);
	            options.outWidth = (int) (width / scale);
	            options.inSampleSize = (int) (scale + 0.5);
	            options.inJustDecodeBounds = false;

	            Bitmap bitmap = null;

			if(degree==0) bitmap=BitmapFactory.decodeFile(path, options);

			else {

				Bitmap bitmap1 = BitmapFactory.decodeFile(path, options);

				bitmap = rotaingImageView(degree, bitmap1);

				if (bitmap1 != null && !bitmap1.isRecycled()) {
					bitmap1.recycle();
				}
			}
	            outputFile = new File(PhotoUtil.createImageFile().getPath());
	            FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(outputFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
					fos.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
				
	            Log.d("scal", "sss ok " + outputFile.length());
	            
	            if (bitmap!=null&&!bitmap.isRecycled()) {
	                bitmap.recycle();
	            }
	            else{
	            	File tempFile = outputFile;
	            	outputFile = new File(PhotoUtil.createImageFile().getPath());
	                PhotoUtil.copyFileUsingFileChannels(tempFile, outputFile);
	            }	            
	        }
		 return outputFile;		
	}



}
