package cn.supra.supralayer_i.managers;

import android.content.Context;
import cn.supra.supralayer_i.ui.clients.BaseClient;
import cn.supra.supralayer_i.ui.clients.LauncherClient;

/**
 * Client管理器；
 * 
 * @author supra
 *
 */
public class ClientManager {
	
	private static ClientManager mInstance;
	private static LauncherClient mLauncherClient;
	
	private static Context mContext;
	
	private ClientManager(Context context) {
		mContext = context;
	}
	
	/**
	 * 由于{@link BaseClient}是webAPP和其他用户交互单元的最底层容器，
	 * 其生命周期可能包含在整个app生命周期内的任意时刻，需要applicationContext，
	 * 所以此处要求{@link ClientManager}需要有applicationContext
	 * @param applicationContext
	 * @return
	 */
	public static ClientManager getInstance(Context applicationContext){
		if(null == mInstance){
			mInstance = new ClientManager(applicationContext);
		}
		return mInstance;
	}
	
	public LauncherClient getLauncherClient(){
		//TODO 实现得到Client的方法；
		if(null == mLauncherClient){
			mLauncherClient = new LauncherClient(mContext);
		}
		return mLauncherClient;
	}
	
}
