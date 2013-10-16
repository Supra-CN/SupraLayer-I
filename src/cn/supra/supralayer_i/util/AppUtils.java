package cn.supra.supralayer_i.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Browser;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebIconDatabase;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.supra.supralayer_i.R;
import cn.supra.supralayer_i.util.ReflectionUtils.ReflectionException;

/**
 * 和android系统相关的一下常用方法
 * 
 */
public final class AppUtils {

	private static final String LOGTAG = "AppUtils";
	/**
	 * @deprecated 请使用 TYPE_DISCONNECTED
	 */
	public static int NETWORK_STATE_NOT_CONNECTED = -1;
	/**
	 * @deprecated 请使用 TYPE_WIFI
	 */
	public static int NETWORK_STATE_CONNECT_WITH_WIF = 1;
	/**
	 * @deprecated 请使用 TYPE_MOBILE
	 */
	public static int NETWORK_STATE_CONNECT_WITH_MOBILE = 0;

	public static final int TYPE_UNKNOWN = -2;
	public static final int TYPE_DISCONNECTED = -1;
	public static final int TYPE_MOBILE = 0;
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_MOBILE_MMS = 2;
	public static final int TYPE_MOBILE_SUPL = 3;
	public static final int TYPE_MOBILE_DUN = 4;
	public static final int TYPE_MOBILE_HIPRI = 5;
	public static final int TYPE_WIMAX = 6;
	public static final int TYPE_BLUETOOTH = 7;
	public static final int TYPE_DUMMY = 8;
	public static final int TYPE_ETHERNET = 9;

	public static boolean isNetworkAvailable(Context c) {
		NetworkInfo info = getActiveNetworkInfo(c);
		return (null != info) && (info.isAvailable());
	}

	private static NetworkInfo getActiveNetworkInfo(Context context) {
		return ((ConnectivityManager) (context
				.getSystemService(Context.CONNECTIVITY_SERVICE)))
				.getActiveNetworkInfo();
	}

	public static int getConnectType(Context context) {
		if (!isNetworkAvailable(context)) {
			return TYPE_DISCONNECTED;
		}
		NetworkInfo info = getActiveNetworkInfo(context);
		return info.getType();
	}
	
	
	//判断该应用是否为系统应用，具体方法是判断该apk是否安装到/system/app下
	public static boolean isSystemApp(Context context) {
		boolean isSystemApp = false;
		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			String appLocation = pkgInfo.applicationInfo.sourceDir;
			isSystemApp = appLocation.startsWith("/system/app");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return isSystemApp;

	}

	/**
	 * this method should be called in UI thread,because All WebView methods
	 * must be called on the UI thread
	 * @param limitSize the MAX Limit size.If the cache size is larger than the limitSize,then run cleaning works.
	 * Unit is MB
	 */
	public static  void clearAppCache(Context context, int limitSize) {
		File cacheDir = context.getCacheDir();
		int dirSize = (int) FileUtils.getDirSize(cacheDir) / (1024 * 1024);
		if (dirSize >= limitSize) {
			WebView wv = new WebView(context);
			wv.clearCache(true);
			WebIconDatabase.getInstance().removeAllIcons();
			Runnable run = new Runnable() {
				@Override
				public void run() {
					WebStorage.getInstance().deleteAllData();
				}
			};
			run.run();
		}
	}
	
	public static String getApkLocation(Context context) {
		String appLocation = null;
		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			appLocation = pkgInfo.applicationInfo.sourceDir;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return appLocation;
	}

	public static boolean isWifiAvailable(Context c) {
		ConnectivityManager manager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		return false;
	}

	// TODO 是否应该将浏览器相关的操作移出这个方法，有没有必要移到单独的一个类中，优先级不是很高
	public static void openNewUrl(Context context, String url) {
//		Intent intent = null;
//		intent = new Intent(SuActionDefine.OPEN_URL_IN_NEW_ACTION);
//		intent.setData(Uri.parse(url));
//		intent.putExtra(SuActionDefine.EXTRA_APPLICATION_ID,
//				SuActionDefine.SOURCE_LOCAL_APPID);
//		intent.setClassName(context, SuBrowserActivity.class.getName());
//		context.startActivity(intent);
	}

	/**
	 * @deprecated 请使用getConnectType()方法
	 * @param c
	 * @return
	 * @author androidyue
	 */
	public static int getNetworkState(Context c) {
		return getConnectType(c);
	}

	/**
	 * TODO 旋转图片的算法还需要再深入研究一下，目前还是不够完美
	 * 
	 * @param context
	 * @param filepath
	 * @param degrees
	 * @return
	 * @author huoniao
	 */
	public static Bitmap getRotationBitmap(Context context, String filepath,
			int degrees) {
		Bitmap rotateMap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 1;
		BitmapFactory.decodeFile(filepath, options);
		int width = (int) AppUtils.getCurScreenWidth(context);
		int height = (int) AppUtils.getCurScreenHeight(context);
		Log.e("bitmap1", "outwidth: " + options.outWidth + " outheight; "
				+ options.outHeight + " width: " + width + " height: " + height);
		int minSlideLength = 0;
		if (width > height) {
			minSlideLength = 2 * width;
		} else {
			minSlideLength = 2 * height;
		}
		int sample = BitmapUtils.computeSampleSize(options.outWidth,
				options.outHeight, minSlideLength, minSlideLength
						* minSlideLength);
		if (sample >= 1)
			sample = 2;
		Log.e("bitmap1", "sampleSize=" + sample);

		try {
			options.inJustDecodeBounds = false;
			options.inSampleSize = sample;
			Bitmap map = null;
			map = BitmapFactory.decodeFile(filepath, options);
			rotateMap = rotateBitmap(map, degrees);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotateMap;
	}

	public static Bitmap rotateBitmap(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static StringBuffer extractsDigital(String str) {
		StringBuffer numberBuffer = new StringBuffer();
		for (int x = 0; x < str.length(); x++) {
			char tmp = str.charAt(x);
			if (tmp >= 48 && tmp <= 57) {
				numberBuffer.append(tmp);
			}
		}
		return numberBuffer;

	}

	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	public static Drawable toGrayscale(Drawable bmpOriginal) {

		Drawable dest;
		try {
			dest = bmpOriginal.mutate();
		} catch (Exception e) {
			dest = bmpOriginal;
		}
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		dest.setColorFilter(cf);
		return dest;
	}

	public static String getFileExtension(String filename) {
		if (null != filename && !"".equals(filename)) {
			return filename.substring(filename.lastIndexOf(".") + 1,
					filename.length()).toLowerCase();
		}
		return null;
	}

	public static void setCurrentWindowBrightness(int brightnessValue,
			Window currentWindow) {
//		if (brightnessValue < BrowserSettings.getInstance().defaultBrightness
//				&& brightnessValue != -1)
//			brightnessValue = BrowserSettings.getInstance().defaultBrightness;
//		WindowManager.LayoutParams lp = currentWindow.getAttributes();
//		lp.screenBrightness = brightnessValue / 100f;
//		currentWindow.setAttributes(lp);
	}

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

	public static String changeDownloadPath(SharedPreferences p, String old_path) {
//		if (!old_path.startsWith("/")) {
//			if(old_path.trim().equals("")) {
//				old_path=SuBrowserProperties.SU_DOWNLOADS_DIR;
//			}else {
//				old_path = "/sdcard/" + old_path;
//			}
//			p.edit()
//					.putString(BrowserSettings.PREF_DEFAULT_DOWNLOAD_PATH,
//							old_path).commit();
//		}
//
//		return old_path;
		return null;
	}

	public static void createShortcutIcon(Activity activity, String url,
			String title, int id) {
//		String className = SuBrowserActivity.class.getName();
//		// XXX: 临时使用解决从傲游导航添加到桌面快捷方式，从快捷方式启动引起崩溃的问题
//		if (url.startsWith(SuURIDefine.FAVORITES_URL)) {
//			className = BookmarkActivity.class.getName();
//		} /*
//		 * else if(url.startsWith(SuURIDefine.RSS_URL)) { className =
//		 * "com.su.app.rss.RssActivity"; }
//		 */
//
//		Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),
//				id);
//		createShortcutIcon(activity, className, url, title, bitmap);
	}

	public static String stream2String(InputStream ins) {
//		try {
//			ByteArrayOutputStream byteOus = new ByteArrayOutputStream();
//			byte[] tmp = new byte[1024];
//			int size = 0;
//			while ((size = ins.read(tmp)) != -1) {
//				byteOus.write(tmp, 0, size);
//			}
//			byteOus.flush();
//			// Log.i(TAG, "the content is: "+byteOus.toString());
//			ins.close();
//			return byteOus.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;
	}

	/**
	 * 根据UA名称
	 * 
	 * @param enumName
	 * @return
	 */

	public static CharSequence getUserAgentName(Context context, String enumName) {
//		CharSequence[] visualNames = context.getResources().getTextArray(
//				R.array.pref_ua_choices);
//		CharSequence[] enumNames = context.getResources().getTextArray(
//				R.array.pref_ua_values);
//
//		if (visualNames.length != enumNames.length) {
//			return "";
//		}
//
//		for (int i = 0; i < enumNames.length; i++) {
//			if (enumNames[i].equals(enumName)) {
//				return visualNames[i];
//			}
//		}

		return "";
	}

	public static void startSuBrowserActivity(Context context) {
		startSuBrowserActivity(null, true, context);
	}

	public static void startSuBrowserActivity(String url, boolean newTab,
			Context context) {
//		Intent intent = null;
//		if (newTab)
//			intent = new Intent(SuActionDefine.OPEN_URL_IN_NEW_ACTION);
//		else
//			intent = new Intent(SuActionDefine.OPEN_URL_IN_CURRENT_ACTION);
//
//		if (url != null) {
//			if (!url.contains("://")) {
//				url = "http://" + url;
//			}
//			intent.setData(Uri.parse(url));
//		}
//		intent.putExtra(SuActionDefine.EXTRA_APPLICATION_ID,
//				SuActionDefine.SOURCE_LOCAL_APPID);
//		intent.setClassName(context, SuBrowserActivity.class.getName());
//		context.startActivity(intent);
	}

	public static void buildNavigation(InputStream ins, Context context) {
//		try {
//
//			String bodyContent = AppUtils.stream2String(ins);
//			// 读取raw目录内的模板文件
//			Resources res = context.getResources();
//			InputStream tmplateIns = res
//					.openRawResource(R.raw.web_nav_template);
//			String template = AppUtils.stream2String(tmplateIns);
//			template = template
//					.replace("%navfontsize%",
//							context.getString(R.string.nav_font_size))
//					.replace("%navh3fontsize%",
//							context.getString(R.string.nav_h3_font_size))
//					.replace("%categoryfontsize%",
//							context.getString(R.string.nav_category_height));
//			// Log.i("nav", "template :"+template);
//			// 填充模板，并保存文件
//			String str = template.replace("%bodyContent%", bodyContent);
//			// Log.i("nav", "index :"+str);
//			FileWriter fous;
//			// 合并文件写到create_web_nav.html文件里
//			File createFile = new File(SuBrowserProperties.getInstance()
//					.getNavigationCreateUrl());
//			fous = new FileWriter(createFile);
//			fous.write(str, 0, str.length());
//			fous.flush();
//			fous.close();
//			// 删除旧有的加载文件load_web_nav.html，将create_web_nav.html重命名为load_web_nav.html
//			File loadFile = new File(SuBrowserProperties.getInstance()
//					.getNavigationLoadUrl());
//			if (loadFile.isFile() && loadFile.exists()) {
//				loadFile.delete();
//			}
//			createFile.renameTo(loadFile);
//			// Log.i("nav", "fous.close()");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public static void createShortcutIcon(Activity activity, String className,
			String url, String title, Bitmap favicon) {
//		String INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
//		Intent i = AppUtils.createShortcutIntent(activity, className, url,
//				title, favicon);
//		i.setAction(INSTALL_SHORTCUT);
//		activity.sendBroadcast(i);
//		Toast.makeText(activity, R.string.adding_shortcut_to_desktop,
//				Toast.LENGTH_SHORT).show();
	}

	public static Bitmap createBitmapFromId(Context context, int id) {
		Bitmap bitmap = BitmapFactory
				.decodeResource(context.getResources(), id);
		return bitmap;
	}

	/**
	 * 创建一个由firstBitmap和secondBitmap组成的复合图像
	 * 
	 * @param context
	 * @param firstBitmap
	 * @param secondBitmap
	 * @return
	 */
	public static Bitmap createComplexBitmap(Context context,
			Bitmap firstBitmap, Bitmap secondBitmap) {
		Bitmap icon = firstBitmap;

		// Make a copy of the regular icon so we can modify the pixels.
		Bitmap copy = icon.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(copy);

		// Make a Paint for the white background rectangle and for
		// filtering the favicon.
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setColor(Color.WHITE);

		// Create a rectangle that is slightly wider than the favicon
		final float iconSize = 16; // 16x16 favicon
		final float padding = 2; // white padding around icon
		final float rectSize = iconSize + 2 * padding;
		final float y = icon.getHeight() - rectSize;
		RectF r = new RectF(0, y, rectSize, y + rectSize);

		// Draw a white rounded rectangle behind the favicon
		canvas.drawRoundRect(r, 2, 2, p);

		// Draw the favicon in the same rectangle as the rounded rectangle
		// but inset by the padding (results in a 16x16 favicon).
		r.inset(padding, padding);
		canvas.drawBitmap(secondBitmap, null, r, p);

		return copy;
	}

	public static Intent createShortcutIntent(Context activity,
			String className, String url, String title, Bitmap favicon) {
//		final Intent i = new Intent();
//		final Intent shortcutIntent = new Intent(Intent.ACTION_VIEW,
//				Uri.parse(url));
//		shortcutIntent.addCategory(Intent.CATEGORY_BROWSABLE);
//		shortcutIntent.setClassName(activity, className);
//		long urlHash = url.hashCode();
//		long uniqueId = (urlHash << 32) | shortcutIntent.hashCode();
//		shortcutIntent.putExtra(Browser.EXTRA_APPLICATION_ID,
//				Long.toString(uniqueId));
//		i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//		i.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
//		if (favicon == null) {
//			i.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
//					Intent.ShortcutIconResource.fromContext(activity,
//							R.drawable.icon));
//		} else {
//			i.putExtra(Intent.EXTRA_SHORTCUT_ICON, favicon);
//			// Bitmap
//			// b=createComplexBitmap(activity,favicon,createBitmapFromId(activity,R.drawable.icon));
//			// i.putExtra(Intent.EXTRA_SHORTCUT_ICON, b);
//
//		}
//		// Do not allow duplicate items
//		i.putExtra("duplicate", false);
//		Intent directCall = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//		directCall.setClassName(activity, className);
//		directCall.putExtra(SuActionDefine.EXTRA_APPLICATION_ID,
//				SuActionDefine.SOURCE_LOCAL_APPID);
//		directCall.setAction(Intent.ACTION_MAIN);
//		directCall.addCategory(Intent.CATEGORY_LAUNCHER);
//		i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, directCall);
//
//		return i;
		return null;
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

	public static int log2(int value) {
		return (int) (Math.log(value) / Math.log(2));
	}

	/**
	 * 获得当前屏幕的宽度，其大小为像素乘以像素密度
	 * 
	 * @param cx
	 *            Context
	 * @return float
	 */
	public static float getCurScreenWidth(Context cx) {

		WindowManager manage = (WindowManager) cx
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manage.getDefaultDisplay();
		return display.getWidth();
	}

	public static File getCacheFile(String url) {
		return getCacheFile(url, null);
	}

	public static File getCacheFile(String url, String filename) {
//		CacheResult cacheResult = null;
//		try {
//			cacheResult = CacheManager.getCacheFile(url, null);
//		} catch (NullPointerException e) {
//			return null;
//		}
//		if (cacheResult != null) {
//			InputStream in = cacheResult.getInputStream();
//			if (filename == null) {
//				filename = FileNameBuilder.getInstance().guessFileName(url,
//						null, null);
//			}
//			String dest_path = SuBrowserProperties.SU_DOWNLOADS_DIR + filename;
//			return getCacheFile(in, dest_path);
//		} else {
			return null;
//		}
	}

	public static File getCacheFile(InputStream in, String filename) {
		// FileInputStream in = new FileInputStream(inputStream);
		File m = null;
		FileOutputStream out = null;
		try {
			m = new File(filename);
			m.createNewFile();
			out = new FileOutputStream(m);
			byte[] buf = new byte[1024];
			int bb;
			while ((bb = in.read(buf)) != -1) {
				out.write(buf, 0, bb);
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return m;
	}

	/**
	 * 获得当前屏幕的高度，其大小为像素乘以像素密度
	 * 
	 * @param cx
	 *            Context
	 * @return float
	 */
	public static float getCurScreenHeight(Context cx) {

		WindowManager manage = (WindowManager) cx
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manage.getDefaultDisplay();
		return display.getHeight();

	}

	/**
	 * 通过邮件，短信等方式，分享链接或其它信息
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */

	public static void shareMessage(Context context, String title, String url) {
//		String shareText = context.getString(R.string.share_title) + ": "
//				+ title + " ; " + context.getString(R.string.share_url) + ": "
//				+ url + " " + context.getString(R.string.share_sign);
//		Browser.sendString(context, shareText);
	}

	/**
	 * On this page,select the share page context menu will call this method.
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	public static final void sharePage(Context c, String title, String url) {
//		Intent send = new Intent(Intent.ACTION_SEND);
//		send.setType("text/plain");
//		send.putExtra(Intent.EXTRA_TEXT, title + " " + url + " "
//				+ c.getResources().getString(R.string.share_sign));
//		send.putExtra(Intent.EXTRA_SUBJECT, title);
//		try {
//			c.startActivity(Intent.createChooser(send,
//					c.getString(R.string.choosertitle_sharevia)));
//		} catch (android.content.ActivityNotFoundException ex) {
//		}
	}

	/**
	 * 获取文件大小字符串
	 * 
	 * @param c
	 * @param size
	 * @return
	 */
	public static CharSequence getSizeString(Context c, long size) {
		CharSequence appSize = null;
		appSize = Formatter.formatFileSize(c, size);
		return appSize;
	}

	public static void saveBitmapToFile(Bitmap bitmap, String filename) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filename));
			fos.write(bitmap2byte(bitmap));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 生成高亮文字
	 * 
	 * @param src
	 *            包含高亮字符串的整个字符串
	 * @param highlight
	 *            高亮文字
	 * @param hightlightColor
	 *            高亮文字的颜色
	 * @return
	 */
	public static CharSequence hightlightText(String src, String highlight,
			int hightlightColor) {

		if (src == null)
			return "";

		SpannableStringBuilder style = new SpannableStringBuilder(src);
		try {
			String tmpSrc = src.toLowerCase();
			String tmpHl = highlight.toLowerCase();
			int start = tmpSrc.indexOf(tmpHl);
			int end = start + highlight.length();

			if (start >= 0)
				style.setSpan(new ForegroundColorSpan(hightlightColor), start,
						end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return style;
	}

	public static Bitmap createScreenshot(WebView view, int width, int height) {
		Picture thumbnail = view.capturePicture();
		if (thumbnail == null) {
			return null;
		}
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bm);
		if (canvas != null) {
			canvas.drawColor(Color.WHITE);
			canvas.scale(1.0f, 0.5f);
		}
		thumbnail.draw(canvas);

		return bm;
	}

	/**
	 * 用于将2011-03-02 10:05:33格式的时间转换为“今天 10：05”格式的时间
	 * 
	 * @author huoniao
	 * */
	public static String getLocalTime(Context context, String time) {
		// 取出年月日来，比较字符串即可
//		String str_curTime = DateFormat.format("yyyy-MM-dd", new Date())
//				.toString();
//		int result = str_curTime
//				.compareTo(time.substring(0, time.indexOf(" ")));
//		if (result > 0) {
//			return context.getString(R.string.haha_yesterday)
//					+ time.substring(time.indexOf(" "), time.lastIndexOf(":"));
//		} else if (result == 0) {
//			return context.getString(R.string.haha_today)
//					+ time.substring(time.indexOf(" "), time.lastIndexOf(":"));
//		} else {
//			return time;
//		}
		return null;
	}

	/**
	 * 用于将精确到毫秒的时间戳13位long格式的时间转换为“上午 10：05”，“昨天”，“04-32”格式的时间
	 * 
	 * @author supra
	 * @param context
	 * @param timeStamp
	 * @return
	 */
	public static String getLocalTime(Context context, long timeStamp) {
//		String formatTime;
		// 刚刚过去的一段时间，显示为“x小时前"或"x分钟前"，单位是小时
//		int justRange = 6;
//		Date destDate = new Date(timeStamp);
//		Date now = new Date();
//		Log.i(LOGTAG + "-time", "timeStamp ： " + timeStamp);
//		Log.i(LOGTAG + "-time", "time      ： " + destDate.toLocaleString());
//		long nowStamp = now.getTime();
//		final int unitMinute = 60 * 1000;
//		final int unitHour = 60;
//		long diffTime = nowStamp - timeStamp;
//		if (destDate.compareTo(now) > 0) {
//			formatTime = String.valueOf(timeStamp);
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			formatTime = format.format(destDate);
//			return formatTime;
//		}
//		int diffMinute = new Long(diffTime / unitMinute).intValue();
//		int diffHour = new Long(diffMinute / unitHour).intValue();
//		if (diffHour <= 6) {
//			if (diffHour >= 1) {
//				formatTime = context
//						.getString(
//								R.string.reader_channel_list_time_x_hours_ago,
//								diffHour);
//			} else if (diffMinute >= 1) {
//				formatTime = context.getString(
//						R.string.reader_channel_list_time_x_minutes_ago,
//						diffMinute);
//			} else {
//				formatTime = context
//						.getString(R.string.reader_channel_list_time_just_now);
//			}
//			return formatTime;
//		}
//		
//		int yearOfDest = destDate.getYear();
//		int yearOfNow = now.getYear();
//		int monthOfDest = destDate.getMonth();
//		int monthOfNow = now.getMonth();
//		int dateOfDest = destDate.getDate();
//		int dateOfNow = now.getDate();
////		try {
//			if (yearOfDest == yearOfNow) {
//				if ((monthOfDest == monthOfNow) && (dateOfDest == dateOfNow)) {
//					// 今天显示12小时制的“上午 10：05”
//					SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
//					formatTime = format.format(destDate);
//				} else if ((monthOfDest == monthOfNow)
//						&& (dateOfDest == (dateOfNow - 1))) {
//					// 昨天显示“昨天”
//					formatTime = context
//							.getString(R.string.reader_channel_list_time_yesterday);
//				} else {
//					// 昨天之前显示“04-32”格式
//					SimpleDateFormat format = new SimpleDateFormat("MM-dd");
//					formatTime = format.format(destDate);
//				}
//			} else {
//				// 去年之前显示“2012-04-32”格式
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//				formatTime = format.format(destDate);
//			}
//			// formatTime = date.toLocaleString();
////		} catch (Exception e) {
////			formatTime = String.valueOf(timeStamp);
////			e.printStackTrace();
////		}
//		Log.i(LOGTAG + "-time", "formatTime： " + formatTime);
//
//		return formatTime;
		return null;
	}

	public static byte[] bitmap2byte(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 获得屏幕的像素密度
	 * 
	 * @param cx
	 *            Context
	 * @return float
	 */
	public static float getDentisy(Context cx) {
		DisplayMetrics dm = cx.getResources().getDisplayMetrics();
		return dm.density;
	}

	public static final String getTextFromClipboard(Context context) {
		ClipboardManager cm = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		if (cm.hasText()) {
			Log.i("Clipboard", "Clipboard content:" + cm.getText().toString());
			return cm.getText().toString();
		} else {
			Log.i("Clipboard", "Clipboard content is null.");
			return null;
		}
	}

	public static final void copyToClipboard(Context context, CharSequence text) {

		ClipboardManager cm = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText(text);
	}

	public static int getCurrentOrientation(int orientationInput) {
		int i = roundOrientation(orientationInput);
		if (i == 0 || i == 180) {
			return Configuration.ORIENTATION_PORTRAIT;
		} else {
			return Configuration.ORIENTATION_LANDSCAPE;
		}
	}

	public static int getCurrentOrientation(Activity activity) {
		int orientation = activity.getRequestedOrientation();
		if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			return orientation;
		}
		orientation = activity.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		} else {
			return ActivityInfo.SCREEN_ORIENTATION_USER;
		}
	}
	
	public static void setScreenOrientationFollowSetting(Activity activity){
//		setScreenOrientation(activity,BrowserSettings.getInstance().screenOrientationType);
	}
	
//	public static  void setScreenOrientation(Activity activity,ScreenOrientationType screenOrientationType) {
//        
//        if(screenOrientationType==ScreenOrientationType.Auto) {
//        	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
//        }else {
//            if(screenOrientationType==ScreenOrientationType.Landscape) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }else {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//        }
//    }

	public static int roundOrientation(int orientationInput) {
		int orientation = orientationInput;
		if (orientation == -1)
			orientation = 0;

		orientation = orientation % 360;
		int retVal;
		if (orientation < (0 * 90) + 70) {
			retVal = 0;
		} else if (orientation < (1 * 90) + 45) {
			retVal = 90;
		} else if (orientation < (2 * 90) + 45) {
			retVal = 180;
		} else if (orientation < (3 * 90) + 45) {
			retVal = 270;
		} else {
			retVal = 0;
		}

		Log.e(LOGTAG, "map orientation " + orientationInput + " to " + retVal);
		return retVal;
	}

	/**
	 * 可以从本地或者远端加载一个Drawable资源 支持从本地磁盘加载资源
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static final Drawable loadDrawable(String url) {
		// URL resUrl = new URL(url);
		// InputStream in = (InputStream) resUrl.getContent();
//		Drawable drawable = null;
//		try {
//			if (url.toLowerCase().startsWith("http://")) {
//				HttpResponse response = new SuHttpClient().get(url);
//				if(SuBrowserProperties.enableLogFlag) {
//					if(url.startsWith("http://static.xgres.com/")) {
//						SuReaderHelper.mCategoryPicBytes+=response.getEntity().getContentLength();
//					}
//				}
//
//				drawable = Drawable.createFromStream(response.getEntity()
//						.getContent(), null);
//			} else {
//				File file = new File(url);
//				if (file.exists()) {
//					BitmapFactory.Options opts = new BitmapFactory.Options();
//					opts.inScaled = true;
//					opts.inDensity = DisplayMetrics.DENSITY_MEDIUM;
//					opts.inTargetDensity = DisplayMetrics.DENSITY_MEDIUM;
//					Bitmap bm = BitmapFactory.decodeFile(url, opts);
//					// Bitmap bm = BitmapFactory.decodeFile(url);
//					drawable = new BitmapDrawable(bm);
//				}
//			}
//		} catch (Exception e) {
//			Log.e("LoadDrawable", "error.url=" + url);
//			e.printStackTrace();
//		}
//
//		return drawable;
		return null;
	}

	public static final Bitmap loadBitmap(String url, int width, int height) {
		// URL resUrl = new URL(url);
		// InputStream in = (InputStream) resUrl.getContent();
		Bitmap bm = null;
		try {
			if (url.toLowerCase().startsWith("http://")) {
				HttpResponse response = new SuHttpClient().get(url);
				InputStream in = response.getEntity().getContent();
				bm = ThumbnailUtils.extractThumbnail(in, width, height);
				in.close();
			} else {
				File file = new File(url);
				if (file.exists()) {
					FileInputStream in = new FileInputStream(url);
					bm = ThumbnailUtils.extractThumbnail(in, width, height);
					in.close();
				}
			}
		} catch (Exception e) {
			Log.e("LoadDrawable", "error.url=" + url);
			e.printStackTrace();
		}

		return bm;
	}

	public static final Bitmap loadBitmap(String url) {
		if (url.toLowerCase().startsWith("http://")) {
			HttpResponse response;
			try {
				response = new SuHttpClient().get(url);
				return BitmapFactory.decodeStream(response.getEntity()
						.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 请求一个Drawable对象，支持从缓存加载
	 * 
	 * @param url
	 * @param cacheDir
	 *            首先尝试从缓存目录加载，尝试失败后从网络加载
	 * @return
	 * @throws IOException
	 */
	public static final Drawable loadDrawable(String url, String cacheDir)
			throws IOException {
		if (url.startsWith("http://")) {
			URL myUrl = new URL(url);
			String cacheFile = cacheDir + myUrl.getPath();
			File file = new File(cacheFile);
			if (!file.exists()) {
				HttpUtils.downloadFile(url, cacheFile);
				return loadDrawable(cacheFile);
			} else {
				return loadDrawable(cacheFile);
			}
		}
		return loadDrawable(url);
	}

	public static boolean isCmWap() {
//		String connType = SuBrowserProperties.getInstance().getConnectonType();
//		return connType != null && connType.equals("cmwap");
		return false;
	}

	/**
	 * 判断是不是新版本文件，是新版本更新本地版本号，返回true，否则返回false
	 * 
	 * @param context
	 * @param propertyName
	 * @param updateVersion
	 * @return
	 */
	// public static boolean isNewVersion(Context context,String
	// propertyName,String updateVersion) {
	// int newVersion=getNewVersion(updateVersion);
	// if(newVersion>getVersion(context,propertyName)) {
	// setVersion(context,propertyName,newVersion);
	// return true;
	// }
	//
	// return false;
	// }

	/**
	 * 获取安装包versionCode
	 * 
	 * @param context
	 * @param mPackageName
	 * @return 如获取返回versionCode,未获取返回0.
	 */
	public static int getPkgVersionCode(Context context, String mPackageName) {
		int vcode = 0;
		try {
			vcode = context.getPackageManager().getPackageInfo(mPackageName,
					PackageManager.GET_UNINSTALLED_PACKAGES).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return vcode;
	}

	/**
	 * 获取安装包versionName
	 * 
	 * @param context
	 * @param mPackageName
	 * @return 如获取返回versionName,未获取返回"".
	 */
	public static String getPkgVersionName(Context context, String mPackageName) {
		String vname = "";
		try {
			vname = context.getPackageManager().getPackageInfo(mPackageName,
					PackageManager.GET_UNINSTALLED_PACKAGES).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return vname;
	}

	/**
	 * 平铺背景图片
	 * 
	 * @param mIcon
	 * @param id
	 *            图片id
	 */
	public static void setTileBackgroundColor(int color, View view) {
		view.setBackgroundColor(color);
	}
	
	/**
	 * 平铺背景图片
	 * 
	 * @param mIcon
	 * @param id
	 *            图片id
	 */
	public static void setTileBackground(Resources res, int id, View view) {
		Bitmap bitmap = BitmapFactory.decodeResource(res, id);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		bd.setDither(true);
		view.setBackgroundDrawable(bd);
	}

	/**
	 * 平铺背景图片
	 * 
	 * @param mIcon
	 * @param id
	 *            图片id
	 */
	public static void setTileBackground(BitmapDrawable bd, View view) {
		bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		bd.setDither(true);
		view.setBackgroundDrawable(bd);
	}

	/**
	 * 平铺背景图片
	 * 
	 * @param mIcon
	 * @param id
	 *            图片id
	 */
	public static Drawable getTileDrawable(Resources res, int id) {
		Bitmap bitmap = BitmapFactory.decodeResource(res, id);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		bd.setDither(true);
		return bd;
	}

	/**
	 * 获取当前屏幕的一些属性 例如高度，宽度，横竖屏等
	 * 
	 * @author huoniao
	 * */
	public static String getScreenResolution(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		StringBuilder sb = new StringBuilder();
		return sb.append(display.getWidth()).append("#")
				.append(display.getHeight()).toString();
	}
	
	public static Point getScreenRealResolution(Context context) {
		Point size = new Point();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// since SDK_INT = 1;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17){
			try {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (Exception ignored) {
			}
		}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17){
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			} catch (Exception ignored) {
			}
		}
		size.x = widthPixels;
		size.y = heightPixels;
		return size;
		
	}

	/**
	 * 检查APK 是否安装
	 */
	public static boolean checkPackageInstall(Context con, String packageName) {

		PackageManager packageManager = con.getPackageManager();

		List<PackageInfo> paklist = packageManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			if (pak != null
					&& pak.applicationInfo.packageName
							.equalsIgnoreCase(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static String getToday(String format) {
		return getToday(format, null);
	}
	
	public static String getToday(String format, TimeZone tz){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (null != tz) {
			sdf.setTimeZone(tz);
		}
		Date d = new Date();
		return sdf.format(d);
	}
	
	public static long getStartOfToday() {
		return getStartOfToday(null);
	}
	
	public static long getStartOfToday(TimeZone tz) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (null != tz) {
			sdf.setTimeZone(tz);
		}
		Date d = new Date();
		String today = sdf.format(d);
		try {
			return sdf.parse(today).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}
	

	public static Bitmap file2Bitmap(File file) {
		ParcelFileDescriptor pfdInput;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			pfdInput = ParcelFileDescriptor.open(file,
					ParcelFileDescriptor.MODE_READ_ONLY);
			bitmap = BitmapFactory.decodeFileDescriptor(
					pfdInput.getFileDescriptor(), null, options);
			pfdInput.close();
		} catch (FileNotFoundException ex) {
			Log.e(LOGTAG, "couldn't open thumbnail " + file + "; " + ex);
		} catch (IOException ex) {
			Log.e(LOGTAG, "couldn't open thumbnail " + file + "; " + ex);
		} catch (NullPointerException ex) {
			// we seem to get this if the file doesn't exist anymore
			Log.e(LOGTAG, "couldn't open thumbnail " + file + "; " + ex);
		} catch (OutOfMemoryError ex) {
			Log.e(LOGTAG, "failed to allocate memory for thumbnail " + file
					+ "; " + ex);
		}

		return bitmap;

	}

	public static String getSDcardPath() {
		String sdDir = "/sdcard";
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return sdDir;
	}

	public static int queryDefaultBrowserCount(Context mContext) {
		List<IntentFilter> intentList = new ArrayList<IntentFilter>();
		List<ComponentName> cnList = new ArrayList<ComponentName>();
		ArrayList<ComponentName> defaultBrowserCount = new ArrayList<ComponentName>();
		final PackageManager packageManager = (PackageManager) mContext
				.getPackageManager();
		packageManager.getPreferredActivities(intentList, cnList, null);
		IntentFilter dhIF;
		for (int i = 0; i < cnList.size(); i++) {
			dhIF = intentList.get(i);
			if (dhIF.hasAction(Intent.ACTION_VIEW)
					&& dhIF.hasCategory(Intent.CATEGORY_BROWSABLE)
					&& dhIF.hasCategory(Intent.CATEGORY_DEFAULT)) {
				if (!defaultBrowserCount.contains(cnList.get(i))) {
					defaultBrowserCount.add(cnList.get(i));
				}
			}
		}
		return defaultBrowserCount.size();
	}

	// Check your browser is not the default browser.
	public static boolean isDefaultBrowser(Context mContext) {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_VIEW);
		filter.addCategory(Intent.CATEGORY_BROWSABLE);
		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);
		final String myPackageName = mContext.getPackageName();
		List<ComponentName> activities = new ArrayList<ComponentName>();
		final PackageManager packageManager = (PackageManager) mContext
				.getPackageManager();
		// You can use name of your package here as third argument
		packageManager.getPreferredActivities(filters, activities,
				myPackageName);
		for (ComponentName activity : activities) {
			if (myPackageName.equals(activity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static boolean Copy(File oldfile, String newPath) {
		boolean result = false;
		try {
			int bytesum = 0;
			int byteread = 0;
			// File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldfile);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				result = true;
			}
		} catch (Exception e) {
			System.out.println("error  ");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 使用Notification实现播放提示音： 已知问题：用在下载管理器之前，声音提示会被下载服务cancelAll()
	 * 
	 * @param sound
	 *            接受媒体文件Uri
	 */
	public static void notifyWithMedia(Context mContext, Uri sound) {
//		NotificationManager nm = (NotificationManager) mContext
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification();
//		notification.audioStreamType = AudioManager.MODE_RINGTONE;
//		notification.sound = sound;
//		nm.notify(R.string.alert, notification);
//		Log.i("notif", "sound@Utils" + System.currentTimeMillis());
//		Log.i("notif", "sound@Utils" + notification.flags);
	}

	/**
	 * 使用SoundPool实现播放提示音，简单高效避免Notificition被干掉：
	 * 已知問題：當收到文本時，下載管理器自動啓動，Acitivity切換可能導致提示音短促
	 * 
	 * @param soundResId
	 *            接收声音资源文件ID
	 */
	public static void notifyWithMedia(Context context, int soundResId) {
		new AsyncTask<Object, Integer, Void>() {
			@Override
			protected Void doInBackground(Object... params) {
				int maxStreams = 1;// SoundPool中的最大音頻數
				int srcQulity = 0;// 聲音質量，默認爲0
				int loadPriority = 1;// 加载优先级，普通为1

				Context context = (Context) params[0];
				int resId = (Integer) params[1];
				SoundPool soundPool = new SoundPool(maxStreams,
						AudioManager.STREAM_RING, srcQulity);
				soundPool.load(context, resId, loadPriority);
				AudioManager mgr = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				float streamVolumeCurrent = mgr
						.getStreamVolume(AudioManager.STREAM_RING);
				float streamVolumeMax = mgr
						.getStreamMaxVolume(AudioManager.STREAM_RING);
				final float stream = streamVolumeCurrent / streamVolumeMax;
				SoundPool.OnLoadCompleteListener onLoadCompleteListener = new SoundPool.OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool,
							int sampleId, int status) {
						int playPriority = 1;// 播放优先级，最低为0
						int loop = 0;// 0=noloop,1=loop
						float rate = 1;// 慢放或快放，1为正常
						soundPool.play(sampleId, stream, stream, playPriority,
								loop, rate);
					}
				};
				soundPool.setOnLoadCompleteListener(onLoadCompleteListener);
				return null;
			}
		}.execute(context, soundResId);
	}

	// 访方法的实现并不可靠，当需要切换到其他程序，如系统设置或一些桌面插件来切换wifi，再返回到MM时，该方法返回false
	// 与Rom实现相关？？？
	public static boolean isTopApplication(Context context, String packageName) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfoList = activityManager
				.getRunningTasks(1);

		if (runningTaskInfoList.size() > 0) {

			String topPackageName = runningTaskInfoList.get(0).topActivity
					.getPackageName();
			Log.e("CloudHelper", "topPackageName" + topPackageName);
			if (topPackageName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	// TODO 这个方法没有完全实现，方法的目的是当服务起来的时候，看看Activity是否也存在没有被杀掉
	public static boolean isRunningApplication(Context context,
			String packageName) {
		boolean flag = false;
		// ActivityManager am = (ActivityManager) context
		// .getSystemService(context.ACTIVITY_SERVICE);
		// List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
		// for (RunningAppProcessInfo ra : run) {
		// Log.e("test", "package_name" + ra.processName);
		// }
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfoList = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		String currentPkgName;
		for (RunningTaskInfo taskInfo : runningTaskInfoList) {
			currentPkgName = taskInfo.topActivity.getPackageName();
			Log.i(LOGTAG, "runnningTaskInfo = " + taskInfo
					+ "; currentPkgName = " + currentPkgName);
		}
		return flag;
	}

	/**
	 * 获取屏幕较小的一边的长度
	 * 
	 * @param context
	 * @return
	 */
	public static int compuseSmallScreenLength(Context context) {
		int width = context.getResources().getDisplayMetrics().widthPixels;
		int height = context.getResources().getDisplayMetrics().heightPixels;
		return width > height ? height : width;
	}

	/**
	 * 开启硬件加速，傲游mm目前仅仅支持在4.0以上的系统开启硬件加速
	 * 
	 * @author huoniao
	 */
	public static void enableHardwareAcceleration(Window window) {
		if (Build.VERSION.SDK_INT >= 11) {
			Class clazz = WindowManager.LayoutParams.class;
			try {
				Field field = clazz.getField("FLAG_HARDWARE_ACCELERATED");
				if (null != field) {
					int hardware_accelerated = field.getInt(clazz);
					window.setFlags(hardware_accelerated, hardware_accelerated);
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void disableViewHardwareAceleration(View view){
		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
	public static void disableHardWareAcceleration(View view) {
		try {
			ReflectionUtils.executeMethod(View.class,view, "setLayerType", new Class[]{int.class,Paint.class}, new Object[]{1,null});
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取设备的物理尺寸,保留小数点后一位，这个方法有些不准确
	 */
	public static double getDeviceInch(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();

		int widthPx = 0;
		int hightPx = 0;

		// android 4.0 后提供的私有方法
		Object widthObj = ReflectionUtils.searchAndExecuteMethod(Display.class,
				display, "getRawWidthNative");
		Object hightObj = ReflectionUtils.searchAndExecuteMethod(Display.class,
				display, "getRawHeightNative");

		if (widthObj != null && hightObj != null) {
			widthPx = (Integer) widthObj;
			hightPx = (Integer) hightObj;
			Log.i(LOGTAG, "私有方法调用成功 widthPx=" + widthPx);
			Log.i(LOGTAG, "私有方法调用成功 hightPx=" + hightPx);
		}

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		if (widthPx == 0) {
			widthPx = metrics.widthPixels;
			hightPx = metrics.heightPixels;
		}

		double inch_x = widthPx / metrics.xdpi;
		double inch_y = hightPx / metrics.ydpi;
		double real_inch = Math.sqrt(inch_x * inch_x + inch_y * inch_y);
		double inch = (Math.round(real_inch * 10));
		double getInch = inch / 10;
		Log.i(LOGTAG, "屏幕的物理尺寸=" + real_inch);
		Log.i(LOGTAG, "本应用所取的尺寸=" + getInch);
		return getInch;
	}

	public static String saveDataImageToFile(final String extra,
			String pathString) {
//		int postfix_start = extra.indexOf("/") + 1;
//		int postfix_end = extra.indexOf(";");
//		String postfix = extra.substring(postfix_start, postfix_end);
//		String imgFilePath = pathString + "." + postfix;
//		int start = extra.indexOf(',');
//		String imgStr = extra.substring(start, extra.length());
//		byte[] b = android.util.Base64.decode(imgStr, android.util.Base64.DEFAULT);
//		imgFilePath = CloudDownloadUtils.createUnipueFile(imgFilePath);
//		try {
//			Log.i(LOGTAG, "filepath=" + imgFilePath);
//			FileOutputStream out = new FileOutputStream(imgFilePath);
//			out.write(b);
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return imgFilePath;
		return null;
	}

	public static boolean isBrowserInRunningState(Context context) {
		boolean flag = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
		for (RunningAppProcessInfo ra : run) {
			Log.e("test", "package_name: " + ra.processName);
			if (context.getPackageName().equals(ra.processName)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

    public static void showPageUpdownDialog(Context context,boolean btnVisible) {
//    	if(BrowserSettings.getInstance().defaultBrowserFirstUseVolume){
//    		BrowserSettings.getInstance().defaultBrowserFirstUseVolume = false;
//    		BrowserSettings.getInstance().setPreferenceValues(
//					BrowserSettings.PREF_FIRST_USE_VOLUME,
//					false);
//    	}
////		final BrowserClientView cv = getViewManager().getActiveClientView();
//		LayoutInflater inflate = LayoutInflater.from(context);
//		LinearLayout line = (LinearLayout) inflate.inflate(
//				R.layout.page_updown_view, null);
//		boolean flagbtn = BrowserSettings.getInstance().defaultBrowserPageUpDown;
//		boolean flagvolume = BrowserSettings.getInstance().defaultBrowserPageUpDownByVolume;
//		final CheckBox checkBoxBtn = (CheckBox) line
//				.findViewById(R.id.updownbtn);
//		final CheckBox checkBoxVolume = (CheckBox) line
//				.findViewById(R.id.updownvolume);
//		if(!btnVisible) {
//			checkBoxBtn.setVisibility(View.GONE);
//		}
//		checkBoxBtn.setChecked(flagbtn);
//		checkBoxVolume.setChecked(flagvolume);
//		final AlertDialog dialog = new AlertDialog.Builder(context).setTitle(
//				R.string.dialog_title_quick_scroll_mode).setView(line)
//				.setPositiveButton(R.string.ok,
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog,
//									int which) {
//
//								BrowserSettings.getInstance().setPreferenceValues(
//												BrowserSettings.PREF_PAGE_UPDOWN,
//												checkBoxBtn.isChecked());
//								BrowserSettings.getInstance().setPreferenceValues(
//												BrowserSettings.PREF_PAGE_UPDOWN_VOLUME,
//												checkBoxVolume.isChecked());
//
//								BrowserSettings.getInstance().defaultBrowserPageUpDown = BrowserSettings
//										.getInstance().getPreferenceValues(
//												BrowserSettings.PREF_PAGE_UPDOWN,
//												false);
//								BrowserSettings.getInstance().defaultBrowserPageUpDownByVolume = BrowserSettings
//										.getInstance().getPreferenceValues(
//												BrowserSettings.PREF_PAGE_UPDOWN_VOLUME,
//												false);
//
//							}
//						}).setNegativeButton(R.string.cancel,
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//
//							}
//						}).setOnCancelListener(new OnCancelListener() {
//
//					@Override
//					public void onCancel(DialogInterface dialog) {
//
//					}
//				}).show();

	}
	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getTimeShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	public static BitmapDrawable byte2Drawable(byte[] data) {
		BitmapDrawable icon = null;
		if (data != null) {
			Options ops = new Options();
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length, ops);
				if (bitmap != null) {
					icon = new BitmapDrawable(bitmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return icon;
	}

	/**
	 * 浏览器统计常用加密方法,加密的步骤如下
	 * <li>使用AES加密，key为eu3o4[r04cml4eir</li>
	 * <li>进行Base64encode</li>
	 * <li>进行URLEncode处理</li>
	 * @param unencryptedData
	 * @return
	 * @throws Exception
	 */
	public static String suDataEncrypt(String unencryptedData) throws Exception {
		byte[] encryptedData = AES.encrypt(unencryptedData, "eu3o4[r04cml4eir");
		byte[] base64Data = Base64.encode(encryptedData);
		String base64edData= new String(base64Data, HTTP.UTF_8);
		return  Uri.encode(base64edData);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  List intersect(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.retainAll(ls2);
		return list;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  List union(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.addAll(ls2);
		return list;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  List diff(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.removeAll(ls2);
		return list;
	
	}
	

}
