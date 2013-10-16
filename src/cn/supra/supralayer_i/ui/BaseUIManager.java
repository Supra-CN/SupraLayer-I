package cn.supra.supralayer_i.ui;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.ui.components.CustomWebView;
import cn.supra.supralayer_i.ui.fragments.BaseWebViewFragment;
import cn.supra.supralayer_i.ui.fragments.LauncherFragment;
import cn.supra.supralayer_i.util.Constants;
import cn.supra.supralayer_i.util.UrlUtils;

public abstract class BaseUIManager implements UIManager {

	protected SpaceActivity mActivity;
	protected ActionBar mActionBar;
	protected FragmentManager mFragmentManager;
	
	protected LauncherFragment mLauncherFragment = null;

	private Handler mHandler;

	public BaseUIManager(SpaceActivity activity) {
		mActivity = activity;

		mActionBar = mActivity.getActionBar();
		mFragmentManager = mActivity.getFragmentManager();

		setupUI();

		startHandler();
	}
	
	protected abstract int getTabCount();
	protected abstract void setFullScreenFromPreferences();
	protected abstract void showStartPage(BaseWebViewFragment webViewFragment);
	protected abstract void hideStartPage(BaseWebViewFragment webViewFragment);



	protected void setupUI() {
		setFullScreenFromPreferences();
	}
	
	@Override
	public SpaceActivity getMainActivity() {
		return mActivity;
	}
	
	@Override
	public void addTab(boolean loadHomePage, boolean privateBrowsing) {
		if (loadHomePage) {
			addTab(
					PreferenceManager.getDefaultSharedPreferences(mActivity).getString(Constants.PREFERENCE_HOME_PAGE, Constants.URL_ABOUT_START),
					false,
					privateBrowsing);
		} else {
			addTab(null, false, privateBrowsing);
		}
	}
	
	@Override
	public void loadUrl(BaseWebViewFragment webViewFragment, String url) {
		if ((url != null) &&
    			(url.length() > 0)) {
			
			if (UrlUtils.isUrl(url)) {
    			url = UrlUtils.checkUrl(url);
    		} else {
    			url = UrlUtils.getSearchUrl(mActivity, url);
    		}
			
			CustomWebView webView = webViewFragment.getWebView();
			
			if (url.equals(Constants.URL_ABOUT_START)) {
//				webView.clearView();
//				webView.clearHistory();
								
				showStartPage(webViewFragment);
				
				// TODO: Check if there is no pb with this.
				// This recreate a new WebView, because i cannot found a way
				// to reset completely (history and display) a WebView.
				webViewFragment.resetWebView();
			} else {
				hideStartPage(webViewFragment);				
				webView.loadUrl(url);
			}
			
			webView.requestFocus();
		}
	}


	private void startHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				default:
					super.handleMessage(msg);
				}
			}
		};
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (intent != null) {
			if (Intent.ACTION_MAIN.equals(intent.getAction())) {
				// ACTION_MAIN can specify an url to load.
				if (getTabCount() <= 0) {
					addTab(true, false);
				}
			}
		}
	}
}
