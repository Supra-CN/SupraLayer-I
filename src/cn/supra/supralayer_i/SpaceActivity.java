
package cn.supra.supralayer_i;

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
import android.view.WindowManager;
import android.webkit.WebIconDatabase;
import android.widget.FrameLayout;

import cn.supra.supralayer_i.managers.ClientManager;
import cn.supra.supralayer_i.providers.BookmarksWrapper;
import cn.supra.supralayer_i.ui.UIManager;
import cn.supra.supralayer_i.ui.clients.BaseClient;
import cn.supra.supralayer_i.util.ApplicationUtils;
import cn.supra.supralayer_i.util.Constants;

import java.util.ArrayList;

/**
 * 充当应用空间的Activity，目前起到承载各种{@link BaseClient}的作用；
 * 
 * @author supra
 */
public class SpaceActivity extends Activity {
    private static final String LOG_TAG = "SpaceActivity";
           
    private ArrayList<BaseClient> clients = new ArrayList<BaseClient>();
    private FrameLayout mSpaceView;
    private ClientManager mClientManager;
    private UIManager mUIManager;

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
        mUIManager = new UIManager(this);
        mUIManager.showLauncher();
        
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
    protected void onStart() {
        super.onStart();
        mUIManager.showLauncher();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    

    public UIManager getUIManager() {
        return null;
    }

}
