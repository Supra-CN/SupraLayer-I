package cn.supra.supralayer_i;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import cn.supra.supralayer_i.managers.ClientManager;
import cn.supra.supralayer_i.ui.clients.BaseClient;

/**
 * 充当应用空间的Activity，目前起到承载各种{@link BaseClient}的作用；
 * @author supra
 *
 */
public class SpaceActivity extends Activity {
	private static final String LOG_TAG = "SpaceActivity";
	
	private ArrayList<BaseClient> clients = new ArrayList<BaseClient>();
	private FrameLayout mSpaceView;
	private ClientManager mClientManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//App环境，Activity环境相关；
		Log.i("supra", "SpaceActivity on Create");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

		//mSpaceView相关；
		mSpaceView = new FrameLayout(this);
		mSpaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(mSpaceView);
		
		//ClientManager相关；
		mClientManager = ClientManager.getInstance(getApplicationContext());
		
		//LauncherClientView相关；
		mSpaceView.removeAllViews();
		mSpaceView.addView(mClientManager.getLauncherClient());
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("supra", "SpaceActivity on Stop");
		if(null != mSpaceView){
//			mSpaceView.removeAllViews();
		}
		
	}

}
