package cn.supra.supralayer_i.controllers;

import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.ui.UIManager;
import cn.supra.supralayer_i.util.MD4;

public class Controller {
	
	private UIManager mUiManager;
	private SpaceActivity mSpaceActivity;

	/**
	 * Private Constructor.
	 */
	private Controller(){}
	
	/**
	 * Holder for singleton implementation.
	 */
	private static final class ControllerHolder{
		private static final Controller INSTANCE = new Controller();
		private ControllerHolder(){};
	}
	
	public static Controller getInstance(){
		return ControllerHolder.INSTANCE;
	}
	
	public void init(UIManager uiManager, SpaceActivity activity){
		mUiManager = uiManager;
		mSpaceActivity = activity;
	}
	
}
