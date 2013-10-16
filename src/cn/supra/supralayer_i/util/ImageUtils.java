package cn.supra.supralayer_i.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils {
	/**
	 * 如果bitmapDrawable不是设置的大小，对bitmapDrawable重新设置
	 * 
	 * @param bitmapDrawable
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {

		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width == newWidth && height == newHeight) {
			return bitmap;
		} else if (width <= 0 || height <= 0) {
			return bitmap;
		} else {
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		}
	}

	/**
	 * 如果bitmapDrawable不是设置的大小，对bitmapDrawable重新设置
	 * 
	 * @param bitmapDrawable
	 * @return
	 */
	public static BitmapDrawable resizeBitmapDrawable(
			BitmapDrawable bitmapDrawable, int newWidth, int newHeight) {
		if (bitmapDrawable == null) {
			return null;
		}
		Bitmap bitmap = bitmapDrawable.getBitmap();
		Bitmap newBitmap = resizeBitmap(bitmap, newWidth, newHeight);
		if (newBitmap == null) {
			return null;
		}
		return new BitmapDrawable(newBitmap);

	}

	/**
	 * Drawable到Bitmap的转换
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static void saveBitmap(File f, Bitmap i) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f);
			i.compress(CompressFormat.PNG, 100, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 切圆角图片
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = Color.RED;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 11;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// bitmap.recycle();
		return output;
	}

	// 切圆角图片
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx, float roundPy ) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = Color.RED;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// bitmap.recycle();
		return output;
	}
	/*
	 * public static Bitmap captureView(final View captureView) { if
	 * (captureView != null) { final CountDownLatch latch = new
	 * CountDownLatch(1); final Bitmap[] cache = new Bitmap[1]; try {
	 * captureView.post(new Runnable() { public void run() { try { cache[0] =
	 * (Bitmap) ReflectionUtils.executeMethod(View.class, captureView,
	 * "createSnapshot", new Class[] {
	 * Bitmap.Config.class,int.class,boolean.class}, new
	 * Object[]{Bitmap.Config.ARGB_8888,0,false} ); } catch (Exception e) { try
	 * { e.printStackTrace(); cache[0] = (Bitmap)
	 * ReflectionUtils.executeMethod(View.class, captureView, "createSnapshot",
	 * new Class[] { Bitmap.Config.class,int.class,boolean.class}, new
	 * Object[]{Bitmap.Config.ARGB_4444,0,false} ); } catch (Exception e2) {
	 * e.printStackTrace(); Log.w("View", "Out of memory for bitmap"); } }
	 * finally { latch.countDown(); } } }); } catch (Exception e) { 
	 * handle exception }
	 * 
	 * 
	 * try { latch.await(4000, TimeUnit.MILLISECONDS); return cache[0] ; } catch
	 * (InterruptedException e) { Log.w("View",
	 * "Could not complete the capture of the view " + captureView);
	 * Thread.currentThread().interrupt(); } }
	 * 
	 * return null; }
	 */
}
