
package cn.supra.supralayer_i.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

import cn.supra.supralayer_i.R;
import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.model.App;
import cn.supra.supralayer_i.ui.fragments.AppFragmentFactory;
import cn.supra.supralayer_i.ui.fragments.BaseAppFragment;
import cn.supra.supralayer_i.ui.fragments.LauncherFragment;

import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

public class UIManager {
    
    private SpaceActivity mActivity;
    private FragmentManager mFragmentManager;
    

    private UUID mLauncherId;
    private Stack<UUID> mTaskStack = new Stack<UUID>();
    private HashMap<UUID, BaseAppFragment> mFragmentMap = new HashMap<UUID, BaseAppFragment>();
    
    public UIManager(SpaceActivity activity) {
        mActivity = activity;
        mFragmentManager = mActivity.getFragmentManager();
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
    
    UUID openAPP(App app){
        BaseAppFragment fragment = AppFragmentFactory.getInstance().createAppFragment(app);
        UUID id = fragment.getUUID();
        mFragmentMap.put(id, fragment);
        mTaskStack.add(id);
        return fragment.getUUID();
    }
    
    
    
    
    
    public LauncherFragment getLauncher(){
        if(null == mLauncherId){
            LauncherFragment launcherFragment = AppFragmentFactory.getInstance().creatLauncherFragment();
            mLauncherId = launcherFragment.getUUID();
            mFragmentMap.put(mLauncherId, launcherFragment);
        }
        return (LauncherFragment)mFragmentMap.get(mLauncherId);
    }
    
    public void showLauncher(){
        setCurrentFragment(mLauncherId);
    }
    
    private void setCurrentFragment(UUID fragmentId) {
        Fragment fragment;
        if (mFragmentMap.containsKey(fragmentId)) {
            fragment = mFragmentMap.get(fragmentId);               
        }else{
            fragment = getLauncher();
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();          
        fragmentTransaction.replace(R.id.AppClientContainer, fragment);               
        fragmentTransaction.commit();
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
