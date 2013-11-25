
package cn.supra.supralayer_i.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;

import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.model.App;
import cn.supra.supralayer_i.ui.components.CustomWebView;
import cn.supra.supralayer_i.ui.fragments.BaseAppFragment;
import cn.supra.supralayer_i.ui.fragments.BaseWebViewFragment;

import java.util.UUID;

public interface UIManager extends OnTouchListener {

    SpaceActivity getMainActivity();
    
    BaseAppFragment getAppFragment(UUID fragmentID);
    
    App getAPP(UUID fragmentID);
    
    UUID ActiveAPP(App app);
    
    /**
     * Browser management.  
     */
//    void addTab(String url, boolean openInBackground, boolean privateBrowsing);
//    
//    void addTab(boolean loadHomePage, boolean privateBrowsing);
    
//    void creatFragment();
    
    
    
    
//    void closeCurrentTab();
    
//    void closeTab(UUID tabId);
    
//    void togglePrivateBrowsing();

//    void loadUrl(String url);
    
//    void loadUrl(UUID tabId, String url, boolean loadInCurrentTabIfNotFound);
    
//    void loadRawUrl(UUID tabId, String url, boolean loadInCurrentTabIfNotFound);
    
//    void loadUrl(BaseWebViewFragment webViewFragment, String url);
    
//    void loadCurrentUrl();
    
//    void loadHomePage();
    
//    void loadHomePage(UUID tabId, boolean loadInCurrentTabIfNotFound);
    
//    void openBookmarksActivityForResult();
    
//    void addBookmarkFromCurrentPage();
    
//    void shareCurrentPage();
    
//    void startSearch();
    
//    void clearFormData();
    
//    void clearCache();
    
//    void setHttpAuthUsernamePassword(String host, String realm, String username, String password);
    
//    CustomWebView getCurrentWebView();
    
//    CustomWebView getWebViewByTabId(UUID tabId);
    
//    BaseWebViewFragment getCurrentWebViewFragment();
    
//    void setUploadMessage(ValueCallback<Uri> uploadMsg);
    
//    ValueCallback<Uri> getUploadMessage();
    
//    void onNewIntent(Intent intent);
    
//    boolean isFullScreen();
    
//    void toggleFullScreen();
        
    /**
     * Events.
     */ 
    boolean onEventBack();
    boolean onEventHome();
    boolean onEventRecent();
    
//    boolean onKeySearch();
    
//    void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
    
//    void onMenuVisibilityChanged(boolean isVisible);
    
//    void onPageStarted(WebView view, String url, Bitmap favicon);
    
//    void onPageFinished(WebView view, String url);
    
//    void onProgressChanged(WebView view, int newProgress);
    
//    void onReceivedTitle(WebView view, String title);
    
//    void onReceivedIcon(WebView view, Bitmap icon);
    
//    void onMainActivityPause();
    
//    void onMainActivityResume();
    
//    void onShowStartPage();
    
//    void onHideStartPage();
    
//    void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback);
    
//    void onHideCustomView();
    
//    void onGeolocationPermissionsShowPrompt(String origin, Callback callback);
    
//    void onGeolocationPermissionsHidePrompt();
    
//    void onActionModeStarted(ActionMode mode);
    
//    void onActionModeFinished(ActionMode mode);

}
