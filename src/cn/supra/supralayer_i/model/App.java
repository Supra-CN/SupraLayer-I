package cn.supra.supralayer_i.model;

import android.R.integer;
import android.graphics.Bitmap;



public abstract class App {
	private int mAppID;
	private String mNane;
	private Bitmap 
		
	public App(int appID) {
		this(appID, "");
	}
	
	public App(int appId, String name){
		mAppID = appId;
		mNane = name;
	}
	
	public int getAppID(){
		return mAppID;
	}
	
	public String getName(){
		return mNane;
	}
	
	public Bitmap getIcon(){
		return getDefaultIcon();
	}
	
	protected abstract Bitmap getDefaultIcon();
		
}
