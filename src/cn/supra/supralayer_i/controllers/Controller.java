package cn.supra.supralayer_i.controllers;

import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.ui.UIManager;

public class Controller { 
    
    /**
     * Holder for singleton implementation.
     */
    private static final class ControllerHolder {
        private static final Controller INSTANCE = new Controller();
        /**
         * Private Constructor.
         */
        private ControllerHolder() { }
    }
    
    /**
     * Get the unique instance of the Controller.
     * @return The instance of the Controller
     */
    public static Controller getInstance() {
        return ControllerHolder.INSTANCE;
    }
    
    /**
     * Private Constructor.
     */
    private Controller() {
    }
    
    private UIManager mUIManager;
    private SpaceActivity mSpaceActivity;
    
    public void init(UIManager uiManager, SpaceActivity activity) {
        mUIManager = uiManager;
        mSpaceActivity = activity;
    }
    
    public UIManager getUIManager() {
        return mUIManager;
    }
    
    public SpaceActivity getMainActivity() {
        return mSpaceActivity;
    }
}
