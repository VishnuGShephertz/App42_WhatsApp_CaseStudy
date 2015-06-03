/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.shephertz.app42.message.ui.util.UiUtil;
import com.shephertz.app42.message.util.Utility;

/*
 * @author Vishnu Garg
 */
public class ImageCacheManager {
	private int requiredSize = 0;
	private boolean isSamplingNotReq = true;
	private final String FileName = "App42Messanger";
	private Bitmap defaultBitMap;
	// the simplest in-memory cache implementation. This should be replaced with
	// something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	private HashMap<String, Bitmap> cacheMap = new HashMap<String, Bitmap>();
	private File cacheDir;
	ImagesLoader imageLoaderThread = new ImagesLoader();
	private boolean isRoundImage = false;

	/*
	 * Constructor of Class
	 * 
	 * @param context of the Activity which is calling
	 * 
	 * @param imageSize required for image sampling if not pass -1
	 */
	public ImageCacheManager(Context context, int imageSize, int defaultIcon,
			boolean isRoundCorener) {
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		requiredSize = imageSize;
		this.defaultBitMap = BitmapFactory.decodeResource(
				context.getResources(), defaultIcon);
		if (imageSize > 0) {
			isSamplingNotReq = false;
		}
		isRoundImage = isRoundCorener;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					FileName);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	/*
	 * This function loads image on ImageView in background Thread
	 * 
	 * @param url URL of image
	 * 
	 * @param imageView on which image is going to be displayed
	 */
	public void loadImage(String url, ImageView imageView) {
		if (cacheMap.containsKey(url)) {
			imageView.setImageBitmap(cacheMap.get(url));
		} else {
			imageView.setTag(url);
			imageQueue(url, imageView);
		}
	}

	/*
	 * This function makes queue of images when the are requested to load
	 * 
	 * @param url URL of image
	 * 
	 * @param imageView on which image is going to be displayed
	 */
	private void imageQueue(String url, ImageView imageView) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		imagesQueue.Clean(imageView);
		ImageToLoad p = new ImageToLoad(url, imageView);
		synchronized (imagesQueue.imagesToLoad) {
			imagesQueue.imagesToLoad.push(p);
			imagesQueue.imagesToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (imageLoaderThread.getState() == Thread.State.NEW)
			imageLoaderThread.start();
	}

	/*
	 * This function used to get Bitmap of image. Also used to do sampling of
	 * image if required else load without sampling
	 * 
	 * @param url of Image
	 */
	public Bitmap getBitmap(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);

		// from SD cache
		Bitmap b;
		if (isSamplingNotReq) {
			b = decodeWithoutSampling(f);
		} else {
			b = decodewithSampleing(f);
		}

		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			Utility.CopyStream(is, os);
			os.close();
			if (isSamplingNotReq) {
				bitmap = decodeWithoutSampling(f);
			} else {
				bitmap = decodewithSampleing(f);
			}
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				clearCache();
			return null;
		}
	}

	/*
	 * This function decode File in Bitmap without sampling
	 * 
	 * @param file the is going to be decoded.
	 */
	private Bitmap decodeWithoutSampling(File f) {
		try {
			return BitmapFactory.decodeStream(new FileInputStream(f));

		} catch (Exception e) {
		}
		return null;
	}

	/*
	 * This function decode File in Bitmap with sampling This is uses to reduce
	 * memory consumption of our Application
	 * 
	 * @param file the is going to be decoded.
	 */
	private Bitmap decodewithSampleing(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < requiredSize
						|| height_tmp / 2 < requiredSize)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	

	/*
	 * Class Task for the Queue
	 */
	private class ImageToLoad {
		public String url;
		public ImageView imageView;

		public ImageToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	ImagesQueue imagesQueue = new ImagesQueue();

	/*
	 * Stops the Thread
	 */
	public void stopThread() {
		imageLoaderThread.interrupt();
	}

	/*
	 * This class stores the images that are downloaded.
	 */
	class ImagesQueue {
		private Stack<ImageToLoad> imagesToLoad = new Stack<ImageToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < imagesToLoad.size();) {
				if (imagesToLoad.get(j).imageView == image)
					imagesToLoad.remove(j);
				else
					++j;
			}
		}
	}

	/*
	 * This class load image in background Thread
	 */
	class ImagesLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (imagesQueue.imagesToLoad.size() == 0)
						synchronized (imagesQueue.imagesToLoad) {
							imagesQueue.imagesToLoad.wait();
						}
					if (imagesQueue.imagesToLoad.size() != 0) {
						ImageToLoad imageToLoad;
						synchronized (imagesQueue.imagesToLoad) {
							imageToLoad = imagesQueue.imagesToLoad.pop();
						}
						Bitmap bmp = getBitmap(imageToLoad.url);
						cacheMap.put(imageToLoad.url, bmp);
						Object tag = imageToLoad.imageView.getTag();
						if (tag != null
								&& ((String) tag).equals(imageToLoad.url)) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp,
									imageToLoad.imageView);
							Activity a = (Activity) imageToLoad.imageView
									.getContext();
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	/*
	 * This class display Image or Bitmap in UI Thread
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public void run() {
			imageView.setBackgroundDrawable(null);
			if (bitmap == null)
				bitmap = defaultBitMap;

			if (!isSamplingNotReq && isRoundImage) {
				imageView.setImageBitmap(UiUtil.getRoundedShape(bitmap, requiredSize));
			} else {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	/*
	 * This function clear the memory cache if memory is full
	 */
	public void clearCache() {
		cacheMap.clear();
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

}
