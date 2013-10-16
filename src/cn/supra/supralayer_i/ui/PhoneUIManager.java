package cn.supra.supralayer_i.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import cn.supra.supralayer_i.R;
import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.ui.components.CustomWebView;
import cn.supra.supralayer_i.ui.fragments.BaseWebViewFragment;
import cn.supra.supralayer_i.ui.fragments.LauncherFragment;
import cn.supra.supralayer_i.ui.fragments.PhoneWebViewFragment;
import cn.supra.supralayer_i.util.Constants;

public class PhoneUIManager extends BaseUIManager {
	
	private enum AnimationType {
		NONE,
		FADE
	}

	private List<PhoneWebViewFragment> mFragmentsList;
	private Map<UUID, PhoneWebViewFragment> mFragmentsMap;

	private int mCurrentTabIndex = -1;
	private Fragment mCurrentFragment = null;


	public PhoneUIManager(SpaceActivity activity) {
		super(activity);
		
		mFragmentsList = new ArrayList<PhoneWebViewFragment>();
		mFragmentsMap = new Hashtable<UUID, PhoneWebViewFragment>();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setFullScreenFromPreferences() {
//		Window win = mActivity.getWindow();
//		WindowManager.LayoutParams winParams = win.getAttributes();
//		final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//
//		if (PreferenceManager.getDefaultSharedPreferences(mActivity)
//				.getBoolean(Constants.PREFERENCE_FULL_SCREEN, false)) {
//			winParams.flags |= bits;
//		} else {
//			winParams.flags &= ~bits;
//		}
//		win.setAttributes(winParams);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		for (PhoneWebViewFragment fragment : mFragmentsList) {
			fragment.getWebView().loadSettings();
		}
		
	}
	
	@Override
	public CustomWebView getCurrentWebView() {
		if (mCurrentTabIndex != -1) {
			return mFragmentsList.get(mCurrentTabIndex).getWebView();
		} else {
			return null;
		}
	}

	@Override
	public void addTab(String url, boolean openInBackground, boolean privateBrowsing) {
		boolean startPage = false;
		if (Constants.URL_ABOUT_START.equals(url)) {
			url = null;
			startPage = true;
		}
		
		
		PhoneWebViewFragment fragment = new PhoneWebViewFragment();
		fragment.init(this, privateBrowsing, url);		
		
		mFragmentsList.add(mCurrentTabIndex + 1, fragment);
		mFragmentsMap.put(fragment.getUUID(), fragment);		
		
		if (!openInBackground) {
			mCurrentTabIndex++;
			
			if (startPage) {
				fragment.setStartPageShown(true);
				
				if (mLauncherFragment == null) {
					createStartPageFragment();
				}
				
				setCurrentFragment(mLauncherFragment, AnimationType.FADE);
				onShowStartPage();
			} else {
				fragment.setStartPageShown(false);
				setCurrentFragment(fragment, AnimationType.FADE);
			}			
			
			CustomWebView webView = getCurrentWebView();

			if (!webView.isPrivateBrowsingEnabled()) {
//				Controller.getInstance().getAddonManager().onTabSwitched(mActivity, webView);
			}
		}
		
//		updateShowPreviousNextTabButtons();
//		updateUrlBar();
	}
	
	
	
	private void createStartPageFragment() {
		mLauncherFragment = new LauncherFragment();
//		mLauncherFragment.setOnStartPageItemClickedListener(new OnStartPageItemClickedListener() {					
//			@Override
//			public void onStartPageItemClicked(String url) {
//				loadUrl(url);
//			}
//		});
	}
	
	private void setCurrentFragment(Fragment fragment, AnimationType animationType) {
		if (fragment != mCurrentFragment) {
			mCurrentFragment = fragment;
			
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();			
			
			switch (animationType) {
			case NONE: break;
			case FADE: fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out); break;
			default: break;
			}
			
			fragmentTransaction.replace(R.id.WebViewContainer, mCurrentFragment);				
			fragmentTransaction.commit();
		}
	}

	@Override
	protected int getTabCount() {
		return mFragmentsList.size();
	}

	@Override
	public void onShowStartPage() {
//		mUrlBar.setTitle(mActivity.getString(R.string.ApplicationName));
//		mUrlBar.setSubtitle(R.string.UrlBarUrlDefaultSubTitle);
//		mUrlBar.hideGoStopReloadButton();
//		
//		mFaviconView.setImageDrawable(mDefaultFavicon);
//					
//		mUrlBar.setUrl(null);
//		mBack.setEnabled(false);
//		mForward.setEnabled(false);
	}

	@Override
	protected void showStartPage(BaseWebViewFragment webViewFragment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void hideStartPage(BaseWebViewFragment webViewFragment) {
		// TODO Auto-generated method stub
		
	}
}
