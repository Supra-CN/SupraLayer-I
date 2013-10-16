package cn.supra.supralayer_i.util;

import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import cn.supra.supralayer_i.R;

public class DebugTool {
	private static final String LOGTAG = Debug.class.getSimpleName();

	public static  void dumpResponseHeader(HttpResponse response, String url) {
		Log.e(LOGTAG, "request url: "+ url);
		Log.e(LOGTAG, "got response  (status line): " + response.getStatusLine());
		Header[] headers = response.getAllHeaders();
		Log.i(LOGTAG,"charset:"+EntityUtils.getContentCharSet(response.getEntity()));
		for (int i = 0; i < headers.length; i++) {
			Header header = headers[i];
			Log.i(LOGTAG, url + ":response header " + header.getName() + " -> " + header.getValue());
		}
	}
	
	
	public static void dumpRequestHeader(HttpRequest request, String url) {
		Log.e(LOGTAG, "request url: "+ url);
		Header[] headers = request.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			Header header = headers[i];
			Log.i(LOGTAG, "request header " + header.getName() + " -> " + header.getValue());
		}
	}
	
	public static String generateInformation(final Activity activity) {
//		DisplayMetrics metric = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;  // 屏幕宽度（像素）
//        int height = metric.heightPixels;  // 屏幕高度（像素）
//        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
//        String build1 = " " + SuBrowserProperties.VERSION_NAME + " Build "
//                + SuBrowserProperties.VERSION_CODE + " Channelid "
//                + SuBrowserProperties.CHANNEL_ID + " Revision "
//                + SuBrowserProperties.REVISION + " AD Channelid "
//                + SuBrowserProperties.AD_CHANNEL_ID 
//                + " Release Date= "+SuBrowserProperties.RELEASE_DATE
//                + ", Push State="+CloudManager.getInstance().pushServiceStatus()
//                +" , FileSync State="+CloudManager.getInstance().fileSyncServiceStatus()
//                +" , Device State="+CloudManager.getInstance().deviceStatus()
//                +" , Device Type="+activity.getString(R.string.device_type)
//                +", width*height="+ width+"*"+height
//                +", density="+density
//                + ", current Locale = " + SuBrowserProperties.getInstance().getRegionLanguage()
//                + ", Locale language = " + Locale.getDefault()
//                + ", Locale country = " + Locale.getDefault().getCountry()
//                +", densityDpi="+densityDpi
//                +",IP="+Utils.getLocalIpAddress()+",port="+CloudFileSync.getInstance().lansyncGetPort()
//                + ",mtk=" + getMTKFlag(activity) 
//                + ",ule88=" + getUle88Flag(activity) 
//                ;
//        return build1;
		return null;
	}
	
	public static void throwNPE(){
		String s = null;
		s.length();
	}
}
