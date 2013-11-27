
package cn.supra.supralayer_i.ui;

import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.model.App;
import cn.supra.supralayer_i.ui.fragments.BaseAppFragment;
import cn.supra.supralayer_i.ui.fragments.LauncherFragment;

import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

public class UIManager {
    
    private SpaceActivity mActivity;
    
    private LauncherFragment mLauncher;
    private Stack<UUID> mTaskStack;
    private HashMap<UUID, BaseAppFragment> mFragmentMap;
    
    public UIManager(SpaceActivity activity) {
        mActivity = activity;
    }

    SpaceActivity getMainActivity(){
        return null;
    }
    
    BaseAppFragment getAppFragment(UUID fragmentID){
        return null;
    }
    
    App getAPP(UUID fragmentID){
        return null;
    }
    
    UUID ActiveAPP(App app){
        return null;
    }
    
    /**
     * Browser management.  
     */
    
    
    /**
     * Events.
     */ 
    boolean onEventBack(){
        return false;
    }
    boolean onEventHome(){
        return false;
    }
    boolean onEventRecent(){
        return false;
    }

    

}
