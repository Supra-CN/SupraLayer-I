package cn.supra.supralayer_i.managers;

public class ClientViewManager {
	
	private static ClientViewManager mInstance;
	
	private ClientViewManager() {}
	
	public ClientViewManager getInstance(){
		if(null == mInstance){
			mInstance = new ClientViewManager();
		}
		return mInstance;
	}
	
}
