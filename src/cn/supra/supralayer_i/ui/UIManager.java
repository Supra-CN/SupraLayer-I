package cn.supra.supralayer_i.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View.OnTouchListener;
import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.ui.components.CustomWebView;
import cn.supra.supralayer_i.ui.fragments.BaseWebViewFragment;

public interface UIManager extends OnTouchListener {
	
	SpaceActivity getMainActivity();

	/**
	 * app management.	
	 */
	void addTab(String url, boolean openInBackground, boolean privateBrowsing);
	void addTab(boolean loadHomePage, boolean privateBrowsing);
	void onNewIntent(Intent intent);
	void loadUrl(BaseWebViewFragment webViewFragment, String url);
	CustomWebView getCurrentWebView();


	/**
	 * Events.
	 */	
	void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
	void onShowStartPage();
	
}
