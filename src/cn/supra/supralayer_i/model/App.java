package cn.supra.supralayer_i.model;

/**
 * App的基类
 * @author supra
 *
 */
public class App {
	private String mAppID;
	private String mNane;
	private String mIconUri;
		
	public App(String appID) {
		this(appID, "");
	}
	
	public App(String appID, String name){
		this(appID, name, "");
	}
	
	public App(String appID, String name, String iconUri){
		mAppID = appID;
		mNane = name;
		mIconUri = iconUri;
	}
	
	public String getAppID(){
		return mAppID;
	}
	
	public String getName(){
		return mNane;
	}
	
	public String getIconUri(){
		return mIconUri;
	}
	
}
