
package cn.supra.supralayer_i.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import cn.supra.supralayer_i.R;
import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.ui.components.CustomWebView;
import cn.supra.supralayer_i.ui.fragments.BaseWebViewFragment;
import cn.supra.supralayer_i.ui.fragments.LauncherFragment;
import cn.supra.supralayer_i.ui.fragments.LauncherFragment.OnStartPageItemClickedListener;
import cn.supra.supralayer_i.ui.fragments.PhoneWebViewFragment;
import cn.supra.supralayer_i.util.ApplicationUtils;
import cn.supra.supralayer_i.util.Constants;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PhoneUIManager extends BaseUIManager {

    private enum SwitchTabsMethod {
        BUTTONS,
        FLING,
        BOTH
    }

    private enum AnimationType {
        NONE,
        FADE
    }

    private static final int FLIP_PIXEL_THRESHOLD = 200;
    private static final int FLIP_TIME_THRESHOLD = 400;

    private List<PhoneWebViewFragment> mFragmentsList;
    private Map<UUID, PhoneWebViewFragment> mFragmentsMap;

//    private PhoneUrlBar mUrlBar;

//    private ImageView mBubbleLeft;
//    private ImageView mBubbleRight;

//    private ImageView mFaviconView;

//    private ImageView mBack;
//    private ImageView mForward;

//    private ImageView mBookmarks;

//    private ImageView mAddTab;
//    private ImageView mCloseTab;

//    private ImageView mShowPreviousTab;
//    private ImageView mShowNextTab;

//    private RelativeLayout mTopBar;
//    private LinearLayout mBottomBar;

    private ProgressBar mProgressBar;

    private BitmapDrawable mDefaultFavicon;

    private int mToolbarsDisplayDuration;

//    private ToolbarsAnimator mToolbarsAnimator;

    private GestureDetector mGestureDetector;

//    private HideToolbarsRunnable mHideToolbarsRunnable = null;

    private ActionMode mActionMode;

    private int mCurrentTabIndex = -1;

    private Fragment mCurrentFragment = null;

    private SwitchTabsMethod mSwitchTabsMethod = SwitchTabsMethod.BOTH;

    public PhoneUIManager(SpaceActivity activity) {
        super(activity);

        updateSwitchTabsMethod();
        mFragmentsList = new ArrayList<PhoneWebViewFragment>();
        mFragmentsMap = new Hashtable<UUID, PhoneWebViewFragment>();
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
//                Controller.getInstance().getAddonManager().onTabSwitched(mActivity, webView);
            }
        }

        updateShowPreviousNextTabButtons();
        updateUrlBar();
    }

    @Override
    public void closeCurrentTab() {
        if (mFragmentsList.size() > 1) {
            closeTabByIndex(mCurrentTabIndex);
        } else {
            closeLastTab();
        }
    }

    @Override
    public void closeTab(UUID tabId) {
        int index = mFragmentsList.indexOf(getWebViewFragmentByUUID(tabId));

        if (mFragmentsList.size() > 1) {
            if ((index >= 0) &&
                    (index < mFragmentsList.size())) {
                closeTabByIndex(index);
            }
        } else if (index == mCurrentTabIndex) {
            closeLastTab();
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
    public void loadUrl(String url) {
//        mUrlBar.hideUrl();
        super.loadUrl(url);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (PhoneWebViewFragment fragment : mFragmentsList) {
            fragment.getWebView().loadSettings();
        }

        if (Constants.PREFERENCE_BUBBLE_POSITION.equals(key)) {
            updateBubblesVisibility();
        } else if (Constants.PREFERENCE_TOOLBARS_AUTOHIDE_DURATION.equals(key)) {
            updateToolbarsDisplayDuration();
        } else if (Constants.PREFERENCES_SWITCH_TABS_METHOD.equals(key)) {
            updateSwitchTabsMethod();
            updateShowPreviousNextTabButtons();
        }
    }

    @Override
    public void onMenuVisibilityChanged(boolean isVisible) {
        mMenuVisible = isVisible;

        if (!mMenuVisible) {
            startHideToolbarsThread();
        }
    }

    @Override
    public boolean onKeyBack() {
//        if (!super.onKeyBack()) {
//            if (mUrlBar.isUrlBarVisible()) {
//                mUrlBar.hideUrl();
//                startHideToolbarsThread();
//
//                return true;
//            } else {
//                CustomWebView currentWebView = getCurrentWebView();
//
//                if ((currentWebView != null) &&
//                        (currentWebView.canGoBack())) {
//                    currentWebView.goBack();
//                    return true;
//                }
//            }
//        }

        return false;
    }

    @Override
    public boolean onKeySearch() {
//        setToolbarsVisibility(true);
//        startHideToolbarsThread();
//
//        if (!mUrlBar.isUrlBarVisible()) {
//            mUrlBar.showUrl();
//        }

        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (view == getCurrentWebView()) {
            setToolbarsVisibility(true);
//            mProgressBar.setVisibility(View.VISIBLE);
//            mFaviconView.setVisibility(View.INVISIBLE);

//            mUrlBar.setUrl(url);
//
//            mUrlBar.setGoStopReloadImage(R.drawable.ic_stop);

            updateBackForwardEnabled();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (view == getCurrentWebView()) {
//            mFaviconView.setVisibility(View.VISIBLE);
//            mProgressBar.setVisibility(View.INVISIBLE);

//            mUrlBar.setUrl(url);
//
//            mUrlBar.setGoStopReloadImage(R.drawable.ic_refresh);

            updateBackForwardEnabled();
            startHideToolbarsThread();
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (view == getCurrentWebView()) {
//            mProgressBar.setProgress(newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (view == getCurrentWebView()) {
            if ((title != null) &&
                    (!title.isEmpty())) {
//                mUrlBar.setTitle(title);
//                mUrlBar.setSubtitle(view.getUrl());
            } else {
//                mUrlBar.setTitle(R.string.ApplicationName);
//                mUrlBar.setSubtitle(R.string.UrlBarUrlDefaultSubTitle);
            }
        }
    }

    @Override
    public void onShowStartPage() {
//        mUrlBar.setTitle(mActivity.getString(R.string.ApplicationName));
//        mUrlBar.setSubtitle(R.string.UrlBarUrlDefaultSubTitle);
//        mUrlBar.hideGoStopReloadButton();

//        mFaviconView.setImageDrawable(mDefaultFavicon);

//        mUrlBar.setUrl(null);
//        mBack.setEnabled(false);
//        mForward.setEnabled(false);
    }

    @Override
    public void onHideStartPage() {
//        mUrlBar.showGoStopReloadButton();
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        mActionMode = mode;

//        if (mToolbarsAnimator.isToolbarsVisible()) {
//            mTopBar.animate().translationY(mTopBar.getHeight());
//        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        if (mActionMode != null) {
            mActionMode = null;

//            if (mToolbarsAnimator.isToolbarsVisible()) {
//                mTopBar.animate().translationY(0);
//            }

            InputMethodManager mgr = (InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(null, 0);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if ((!getCurrentWebViewFragment().isStartPageShown()) &&
                (event.getActionMasked() == MotionEvent.ACTION_DOWN)) {
            setToolbarsVisibility(false);
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void setupUI() {
        super.setupUI();

//        mActionBar.hide();

        mGestureDetector = new GestureDetector(mActivity, new GestureListener());

        updateToolbarsDisplayDuration();

        int buttonSize = mActivity.getResources().getInteger(R.integer.application_button_size);
        Drawable d = mActivity.getResources().getDrawable(R.drawable.ic_launcher);

        Bitmap bm = Bitmap.createBitmap(buttonSize, buttonSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        d.setBounds(0, 0, buttonSize, buttonSize);
        d.draw(canvas);

        mDefaultFavicon = new BitmapDrawable(mActivity.getResources(), bm);

//        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.WebViewProgress);

//        mUrlBar = (PhoneUrlBar) mActivity.findViewById(R.id.UrlBar);
//
//        mUrlBar.setEventListener(new OnPhoneUrlBarEventListener() {
//
//            @Override
//            public void onVisibilityChanged(boolean urlBarVisible) {
//
//            }
//
//            @Override
//            public void onUrlValidated() {
//                loadCurrentUrl();
//            }
//
//            @Override
//            public void onGoStopReloadClicked() {
//                if (mUrlBar.isUrlChangedByUser()) {
//                    // Use the UIManager to load urls, as it perform check on
//                    // them.
//                    loadCurrentUrl();
//                } else if (getCurrentWebView().isLoading()) {
//                    getCurrentWebView().stopLoading();
//                } else {
//                    getCurrentWebView().reload();
//                }
//            }

//            @Override
//            public void onMenuVisibilityChanged(boolean isVisible) {
//                mMenuVisible = isVisible;
//
//                if (!mMenuVisible) {
//                    startHideToolbarsThread();
//                }
//            }
//        });

//        mUrlBar.setTitle(R.string.ApplicationName);
//        mUrlBar.setSubtitle(R.string.UrlBarUrlDefaultSubTitle);

//        mFaviconView = (ImageView) mActivity.findViewById(R.id.FaviconView);
//        mFaviconView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mUrlBar.isUrlBarVisible()) {
//                    mUrlBar.hideUrl();
//                    startHideToolbarsThread();
//                } else {
//                    loadHomePage();
//                }
//            }
//        });

//        mFaviconView.setImageDrawable(mDefaultFavicon);

//        mTopBar = (RelativeLayout) mActivity.findViewById(R.id.TopBar);
//        mTopBar.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // Steal event from WebView.
//            }
//        });

//        mBottomBar = (LinearLayout) mActivity.findViewById(R.id.BottomBar);
//        mBottomBar.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // Steal event from WebView.
//            }
//        });

//        mBack = (ImageView) mActivity.findViewById(R.id.BtnBack);
//        mBack.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((!getCurrentWebViewFragment().isStartPageShown()) &&
//                        (getCurrentWebView().canGoBack())) {
//                    getCurrentWebView().goBack();
//                }
//            }
//        });
//        mBack.setEnabled(false);

//        mForward = (ImageView) mActivity.findViewById(R.id.BtnForward);
//        mForward.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((!getCurrentWebViewFragment().isStartPageShown()) &&
//                        (getCurrentWebView().canGoForward())) {
//                    getCurrentWebView().goForward();
//                }
//            }
//        });
//        mForward.setEnabled(false);

//        mBookmarks = (ImageView) mActivity.findViewById(R.id.BtnBookmarks);
//        mBookmarks.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                openBookmarksActivityForResult();
//            }
//        });

//        mAddTab = (ImageView) mActivity.findViewById(R.id.BtnAddTab);
//        mAddTab.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                addTab(true, false);
//            }
//        });

//        mCloseTab = (ImageView) mActivity.findViewById(R.id.BtnCloseTab);
//        mCloseTab.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                closeCurrentTab();
//            }
//        });

//        mShowPreviousTab = (ImageView) mActivity.findViewById(R.id.PreviousTabView);
//        mShowPreviousTab.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPreviousTab();
//            }
//        });

//        mShowNextTab = (ImageView) mActivity.findViewById(R.id.NextTabView);
//        mShowNextTab.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showNextTab();
//            }
//        });

//        mBubbleLeft = (ImageView) mActivity.findViewById(R.id.BubbleLeftView);
//        mBubbleLeft.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setToolbarsVisibility(true);
//                startHideToolbarsThread();
//            }
//        });

//        mBubbleRight = (ImageView) mActivity.findViewById(R.id.BubbleRightView);
//        mBubbleRight.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setToolbarsVisibility(true);
//                startHideToolbarsThread();
//            }
//        });

        updateBubblesVisibility();

//        mToolbarsAnimator = new ToolbarsAnimator(mTopBar, mBottomBar, mShowPreviousTab,
//                mShowNextTab);

        startHideToolbarsThread();
    }

    @Override
    protected String getCurrentUrl() {
//        return mUrlBar.getUrl();
        return "";
    }

    @Override
    public BaseWebViewFragment getCurrentWebViewFragment() {
        if (mCurrentTabIndex != -1) {
            return mFragmentsList.get(mCurrentTabIndex);
        } else {
            return null;
        }
    }

    @Override
    protected int getTabCount() {
        return mFragmentsList.size();
    }

    @Override
    protected BaseWebViewFragment getWebViewFragmentByUUID(UUID fragmentId) {
        return mFragmentsMap.get(fragmentId);
    }

//    public void hideToolbars() {
//        if ((!mUrlBar.isUrlBarVisible()) &&
//                (!getCurrentWebViewFragment().isStartPageShown()) &&
//                (!mMenuVisible) &&
//                (!getCurrentWebView().isLoading())) {
//            setToolbarsVisibility(false);
//        }
//
//        mHideToolbarsRunnable = null;
//    }

    private void updateBackForwardEnabled() {
        CustomWebView currentWebView = getCurrentWebView();

//        mBack.setEnabled(currentWebView.canGoBack());
//        mForward.setEnabled(currentWebView.canGoForward());
    }

    @Override
    protected void setApplicationButtonImage(Bitmap icon) {
//        BitmapDrawable image = ApplicationUtils.getApplicationButtonImage(mActivity, icon);
//
//        if (image != null) {
//            mFaviconView.setImageDrawable(image);
//        } else {
//            mFaviconView.setImageDrawable(mDefaultFavicon);
//        }
    }

    @Override
    protected void showStartPage(BaseWebViewFragment webViewFragment) {

        if ((webViewFragment != null) &&
                (!webViewFragment.isStartPageShown())) {

            webViewFragment.getWebView().onPause();
            webViewFragment.setStartPageShown(true);

            if (webViewFragment == getCurrentWebViewFragment()) {

                if (mLauncherFragment == null) {
                    createStartPageFragment();
                }

                setCurrentFragment(mLauncherFragment, AnimationType.FADE);

                onShowStartPage();
            }
        }
    }

    @Override
    protected void hideStartPage(BaseWebViewFragment webViewFragment) {

        if ((webViewFragment != null) &&
                (webViewFragment.isStartPageShown())) {

            webViewFragment.setStartPageShown(false);

            if (webViewFragment == getCurrentWebViewFragment()) {
                setCurrentFragment(webViewFragment, AnimationType.FADE);

                onHideStartPage();
            }
        }
    }

    @Override
    protected void resetUI() {
        updateUrlBar();
    }

    @Override
    protected void setFullScreenFromPreferences() {
        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        if (PreferenceManager.getDefaultSharedPreferences(mActivity).getBoolean(
                Constants.PREFERENCE_FULL_SCREEN, false)) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);
    }

    private void updateUrlBar() {
//        CustomWebView currentWebView;
//        BaseWebViewFragment currentFragment = getCurrentWebViewFragment();
//
//        if ((currentFragment != null) &&
//                (currentFragment.isStartPageShown())) {
//            currentWebView = null;
//
//            if (!mToolbarsAnimator.isToolbarsVisible()) {
//                setToolbarsVisibility(true);
//            }
//        } else {
//            currentWebView = getCurrentWebView();
//        }
//
//        if (currentWebView != null) {
//            String title = currentWebView.getTitle();
//            String url = currentWebView.getUrl();
//            Bitmap icon = currentWebView.getFavicon();
//
//            if ((title != null) &&
//                    (!title.isEmpty())) {
//                mUrlBar.setTitle(title);
//            } else {
//                mUrlBar.setTitle(R.string.ApplicationName);
//            }
//
//            if ((url != null) &&
//                    (!url.isEmpty())) {
//                mUrlBar.setSubtitle(url);
//                mUrlBar.setUrl(url);
//            } else {
//                mUrlBar.setSubtitle(R.string.UrlBarUrlDefaultSubTitle);
//                mUrlBar.setUrl(null);
//            }
//
//            setApplicationButtonImage(icon);
//
//            if (currentWebView.isLoading()) {
//                mProgressBar.setVisibility(View.VISIBLE);
//                mFaviconView.setVisibility(View.INVISIBLE);
//                mUrlBar.setGoStopReloadImage(R.drawable.ic_stop);
//            } else {
//                mFaviconView.setVisibility(View.VISIBLE);
//                mProgressBar.setVisibility(View.INVISIBLE);
//                mUrlBar.setGoStopReloadImage(R.drawable.ic_refresh);
//            }
//
//            updateBackForwardEnabled();
//        } else {
//            mUrlBar.setTitle(R.string.ApplicationName);
//            mUrlBar.setSubtitle(R.string.UrlBarUrlDefaultSubTitle);
//            mFaviconView.setImageDrawable(mDefaultFavicon);
//
//            mFaviconView.setVisibility(View.VISIBLE);
//            mProgressBar.setVisibility(View.INVISIBLE);
//
//            mUrlBar.setUrl(null);
//            mBack.setEnabled(false);
//            mForward.setEnabled(false);
//        }
//
//        mUrlBar.setPrivateBrowsingIndicator(currentFragment != null ? currentFragment
//                .isPrivateBrowsingEnabled() : false);
    }

    private void updateShowPreviousNextTabButtons() {
//        if (isSwitchTabsByButtonsEnabled()) {
//            if (mCurrentTabIndex == 0) {
//                mShowPreviousTab.setVisibility(View.GONE);
//            } else if (mToolbarsAnimator.isToolbarsVisible()) {
//                mShowPreviousTab.setTranslationX(0);
//                mShowPreviousTab.setVisibility(View.VISIBLE);
//            }
//
//            if (mCurrentTabIndex == mFragmentsList.size() - 1) {
//                mShowNextTab.setVisibility(View.GONE);
//            } else if (mToolbarsAnimator.isToolbarsVisible()) {
//                mShowNextTab.setTranslationX(0);
//                mShowNextTab.setVisibility(View.VISIBLE);
//            }
//        } else {
//            mShowPreviousTab.setVisibility(View.GONE);
//            mShowNextTab.setVisibility(View.GONE);
//        }
    }

    private void closeLastTab() {
        PhoneWebViewFragment fragment = mFragmentsList.get(mCurrentTabIndex);

        CustomWebView webView = fragment.getWebView();

        if (!webView.isPrivateBrowsingEnabled()) {
//            Controller.getInstance().getAddonManager().onTabClosed(mActivity, webView);
        }

        webView.onPause();

        loadHomePage();
//        updateUrlBar();
    }

    private void closeTabByIndex(int index) {
        boolean currentTab = index == mCurrentTabIndex;

        PhoneWebViewFragment fragment = mFragmentsList.get(index);

        CustomWebView webView = fragment.getWebView();

        if (!webView.isPrivateBrowsingEnabled()) {
//            Controller.getInstance().getAddonManager().onTabClosed(mActivity, webView);
        }

        webView.onPause();

        mFragmentsList.remove(index);
        mFragmentsMap.remove(fragment.getUUID());

        if (currentTab) {
            if (mCurrentTabIndex > 0) {
                mCurrentTabIndex--;
            }

            showCurrentTab(true);
        } else {
            if (index < mCurrentTabIndex) {
                mCurrentTabIndex--;
            }
        }
    }

    private void showCurrentTab(boolean notifyTabSwitched) {
//        PhoneWebViewFragment newFragment = mFragmentsList.get(mCurrentTabIndex);
//
//        if (newFragment.isStartPageShown()) {
//            setCurrentFragment(mLauncherFragment, AnimationType.FADE);
//            mUrlBar.hideGoStopReloadButton();
//        } else {
//            setCurrentFragment(newFragment, AnimationType.FADE);
//            mUrlBar.showGoStopReloadButton();
//        }
//
//        updateShowPreviousNextTabButtons();
//        updateUrlBar();
//
//        if (notifyTabSwitched) {
//            CustomWebView webView = getCurrentWebView();
//
//            if (!webView.isPrivateBrowsingEnabled()) {
//                Controller.getInstance().getAddonManager().onTabSwitched(mActivity, webView);
//            }
//        }
    }

    private void showPreviousTab() {
        if (mCurrentTabIndex > 0) {
//            mUrlBar.hideUrl();

            mCurrentTabIndex--;

            showCurrentTab(true);
            startHideToolbarsThread();
        }
    }

    private void showNextTab() {
        if (mCurrentTabIndex < mFragmentsList.size() - 1) {
//            mUrlBar.hideUrl();

            mCurrentTabIndex++;

            showCurrentTab(true);
            startHideToolbarsThread();
        }
    }

    /**
     * Change the tool bars visibility.
     * 
     * @param setVisible If True, the tool bars will be shown.
     */
    private void setToolbarsVisibility(boolean setVisible) {
//        if (setVisible) {
//            if (!mToolbarsAnimator.isToolbarsVisible()) {
//                mUrlBar.hideUrl();
//
//                boolean showTabsButtons = isSwitchTabsByButtonsEnabled();
//
//                mToolbarsAnimator.startShowAnimation(
//                        showTabsButtons && mCurrentTabIndex > 0,
//                        showTabsButtons && mCurrentTabIndex < mFragmentsList.size() - 1);
//            }
//        } else {
//            if (mToolbarsAnimator.isToolbarsVisible()) {
//                if (mHideToolbarsRunnable != null) {
//                    mHideToolbarsRunnable.disable();
//                }
//
//                mUrlBar.hideUrl(mActionMode == null);
//
//                mToolbarsAnimator.startHideAnimation();
//            }
//        }
    }

    private void startHideToolbarsThread() {
//        if (mHideToolbarsRunnable != null) {
//            mHideToolbarsRunnable.disable();
//        }
//
//        mHideToolbarsRunnable = new HideToolbarsRunnable(this, mToolbarsDisplayDuration * 1000);
//        new Thread(mHideToolbarsRunnable).start();
    }

    private void updateBubblesVisibility() {
//        String position = PreferenceManager.getDefaultSharedPreferences(mActivity).getString(
//                Constants.PREFERENCE_BUBBLE_POSITION, "RIGHT");
//
//        if ("RIGHT".equals(position)) {
//            mBubbleLeft.setVisibility(View.INVISIBLE);
//            mBubbleRight.setVisibility(View.VISIBLE);
//        } else if ("LEFT".equals(position)) {
//            mBubbleLeft.setVisibility(View.VISIBLE);
//            mBubbleRight.setVisibility(View.INVISIBLE);
//        } else if ("BOTH".equals(position)) {
//            mBubbleLeft.setVisibility(View.VISIBLE);
//            mBubbleRight.setVisibility(View.VISIBLE);
//        }
    }

    private void updateToolbarsDisplayDuration() {
        String duration = PreferenceManager.getDefaultSharedPreferences(mActivity).getString(
                Constants.PREFERENCE_TOOLBARS_AUTOHIDE_DURATION, "3");

        try {
            mToolbarsDisplayDuration = Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            mToolbarsDisplayDuration = 3;
        }

        if (mToolbarsDisplayDuration <= 0) {
            mToolbarsDisplayDuration = 3;
        }
    }

    private void updateSwitchTabsMethod() {
        String method = PreferenceManager.getDefaultSharedPreferences(mActivity).getString(
                Constants.PREFERENCES_SWITCH_TABS_METHOD, "BUTTONS");

        if (method.equals("BUTTONS")) {
            mSwitchTabsMethod = SwitchTabsMethod.BUTTONS;
        } else if (method.equals("FLING")) {
            mSwitchTabsMethod = SwitchTabsMethod.FLING;
        } else if (method.equals("BOTH")) {
            mSwitchTabsMethod = SwitchTabsMethod.BOTH;
        } else {
            mSwitchTabsMethod = SwitchTabsMethod.BUTTONS;
        }
    }

    private boolean isSwitchTabsByFlingEnabled() {
        return (mSwitchTabsMethod == SwitchTabsMethod.FLING)
                || (mSwitchTabsMethod == SwitchTabsMethod.BOTH);
    }

    private boolean isSwitchTabsByButtonsEnabled() {
        return (mSwitchTabsMethod == SwitchTabsMethod.BUTTONS)
                || (mSwitchTabsMethod == SwitchTabsMethod.BOTH);
    }

    private void createStartPageFragment() {
        mLauncherFragment = new LauncherFragment();
        mLauncherFragment.setOnStartPageItemClickedListener(new OnStartPageItemClickedListener() {
            @Override
            public void onStartPageItemClicked(String url) {
                loadUrl(url);
            }
        });
    }

    private void setCurrentFragment(Fragment fragment, AnimationType animationType) {
        if (fragment != mCurrentFragment) {
            mCurrentFragment = fragment;

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            switch (animationType) {
                case NONE:
                    break;
                case FADE:
                    fragmentTransaction
                            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                    break;
                default:
                    break;
            }

            fragmentTransaction.replace(R.id.WebViewContainer, mCurrentFragment);
            fragmentTransaction.commit();
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isSwitchTabsByFlingEnabled()) {
                if (e2.getEventTime() - e1.getEventTime() <= FLIP_TIME_THRESHOLD) {
                    if (e2.getX() > (e1.getX() + FLIP_PIXEL_THRESHOLD)) {

                        showPreviousTab();
                        return false;
                    }

                    // going forwards: pushing stuff to the left
                    if (e2.getX() < (e1.getX() - FLIP_PIXEL_THRESHOLD)) {

                        showNextTab();
                        return false;
                    }
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

}
