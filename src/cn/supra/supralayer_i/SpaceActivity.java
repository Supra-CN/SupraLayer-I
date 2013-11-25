
package cn.supra.supralayer_i;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebIconDatabase;
import android.widget.FrameLayout;
import cn.supra.supralayer_i.controllers.Controller;
import cn.supra.supralayer_i.managers.ClientManager;
import cn.supra.supralayer_i.providers.BookmarksWrapper;
import cn.supra.supralayer_i.ui.PhoneUIManager;
import cn.supra.supralayer_i.ui.UIManager;
import cn.supra.supralayer_i.ui.UIManagerProvider;
import cn.supra.supralayer_i.ui.clients.BaseClient;
import cn.supra.supralayer_i.util.ApplicationUtils;
import cn.supra.supralayer_i.util.Constants;

/**
 * 充当应用空间的Activity，目前起到承载各种{@link BaseClient}的作用；
 * 
 * @author supra
 */
public class SpaceActivity extends Activity implements UIManagerProvider {
    private static final String LOG_TAG = "SpaceActivity";

    private ArrayList<BaseClient> clients = new ArrayList<BaseClient>();
    private FrameLayout mSpaceView;
    private ClientManager mClientManager;
    private UIManager mUIManager;

    private OnSharedPreferenceChangeListener mPreferenceChangeListener;

    private IntentFilter mPackagesFilter;
    private BroadcastReceiver mPackagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // App环境，Activity环境相关；
        Log.i("supra", "SpaceActivity on Create");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

        setContentView(R.layout.phone_space_activity);
        mUIManager = new PhoneUIManager(this);

        Controller.getInstance().init(mUIManager, this);

        initializeWebIconDatabase();

        mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                mUIManager.onSharedPreferenceChanged(sharedPreferences, key);

                // If the user changed the history size, reset the last history
                // truncation date.
                if (Constants.PREFERENCE_HISTORY_SIZE.equals(key)) {
                    Editor prefEditor = sharedPreferences.edit();
                    prefEditor.putLong(Constants.TECHNICAL_PREFERENCE_LAST_HISTORY_TRUNCATION, -1);
                    prefEditor.commit();
                }
            }
        };

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        mPackagesFilter = new IntentFilter();
        mPackagesFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mPackagesFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        mPackagesFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mPackagesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mPackagesFilter.addDataScheme("package");

        registerReceiver(mPackagesReceiver, mPackagesFilter);

        boolean firstRun = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.TECHNICAL_PREFERENCE_FIRST_RUN, true);
        if (firstRun) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(Constants.TECHNICAL_PREFERENCE_FIRST_RUN, false);
            editor.putInt(Constants.TECHNICAL_PREFERENCE_LAST_RUN_VERSION_CODE,
                    ApplicationUtils.getApplicationVersionCode(this));
            editor.commit();

            BookmarksWrapper.fillDefaultBookmaks(
                    getContentResolver(),
                    getResources().getStringArray(R.array.DefaultBookmarksTitles),
                    getResources().getStringArray(R.array.DefaultBookmarksUrls));

        } else {
            int currentVersionCode = ApplicationUtils.getApplicationVersionCode(this);
            int savedVersionCode = PreferenceManager.getDefaultSharedPreferences(this).getInt(
                    Constants.TECHNICAL_PREFERENCE_LAST_RUN_VERSION_CODE, -1);

            if (currentVersionCode != savedVersionCode) {
                Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt(Constants.TECHNICAL_PREFERENCE_LAST_RUN_VERSION_CODE,
                        currentVersionCode);
                editor.commit();

                // TODO: Do something on new version.
            }
        }

        mUIManager.onNewIntent(getIntent());

        // mSpaceView相关；
        // mSpaceView = new FrameLayout(this);
        // mSpaceView.setLayoutParams(new
        // LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // setContentView(mSpaceView);

        // ClientManager相关；
        // mClientManager = ClientManager.getInstance(getApplicationContext());

        // LauncherClientView相关；
        // mSpaceView.removeAllViews();
        // mSpaceView.addView(mClientManager.getLauncherClient());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUIManager.onMainActivityResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);

    }

    @Override
    protected void onPause() {
        super.onPause();

        mUIManager.onMainActivityPause();
    }

    @Override
    protected void onDestroy() {
        WebIconDatabase.getInstance().close();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        unregisterReceiver(mPackagesReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        // Controller.getInstance().getAddonManager().bindAddons();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("supra", "SpaceActivity on Stop");
        if (null != mSpaceView) {
            // mSpaceView.removeAllViews();
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mUIManager.onEventBack()) {
                    return true;
                } else {
                    moveTaskToBack(true);
                    return true;
                }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mUIManager.onNewIntent(intent);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        mUIManager.onActionModeFinished(mode);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        mUIManager.onActionModeStarted(mode);
    }

    @Override
    public UIManager getUIManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Initialize the Web icons database.
     */
    private void initializeWebIconDatabase() {

        final WebIconDatabase db = WebIconDatabase.getInstance();
        db.open(getDir("icons", 0).getPath());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Do nothing for now, as default implementation mess up with
        // tabs/fragment management.
        // In the future, save and restore tabs.
        // super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Do nothing for now, as default implementation mess up with
        // tabs/fragment management.
        // In the future, save and restore tabs.
        // super.onRestoreInstanceState(savedInstanceState);
    }

}
