package cn.supra.supralayer_i.model;

/**
 * App的基类
 * @author supra
 *
 */
public class App {
	private int mAppID;
	private String mNane;
	private String mIconUri;
		
	public App(int appID) {
		this(appID, "");
	}
	
	public App(int appID, String name){
		this(appID, name, "");
	}
	
	public App(int appID, String name, String iconUri){
		mAppID = appID;
		mNane = name;
		mIconUri = iconUri;
	}
	
	public int getAppID(){
		return mAppID;
	}
	
	public String getName(){
		return mNane;
	}
	
	public String getIconUri(){
		return mIconUri;
	}
	
}
