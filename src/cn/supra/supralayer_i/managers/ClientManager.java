package cn.supra.supralayer_i.managers;

import cn.supra.supralayer_i.LauncherClient;

public class ClientManager {
	
	private static ClientManager mInstance;
	
	private ClientManager() {}
	
	public static ClientManager getInstance(){
		if(null == mInstance){
			mInstance = new ClientManager();
		}
		return mInstance;
	}
	
	public LauncherClient getLauncherClient(){
		//TODO 实现得到Client的方法；
		return null;
	}
	
}
