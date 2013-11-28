package cn.supra.supralayer_i.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import cn.supra.supralayer_i.model.App;
import cn.supra.supralayer_i.ui.UIManager;
import cn.supra.supralayer_i.ui.components.CustomWebView;

import java.util.UUID;

public class BaseAppFragment extends Fragment implements App{
    
    //==========================================================================
    // 
    //==========================================================================

    protected UUID mUUID;
    private App mApp;
    
    protected UIManager mUIManager;
    protected ViewGroup mParentView;
    protected CustomWebView mWebView;
    
    public BaseAppFragment() {
        mUUID = UUID.randomUUID();
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    
    @Override
    public String getName() {
        mApp.getName();
        return null;
    }
    
    @Override
    public String getIconUri() {
        mApp.getIconUri();
        return null;
    }
    
    @Override
    public String getAppID() {
        mApp.getAppID();
        return null;
    }
    
    public UUID getUUID() {
        return mUUID;
    }
    
    public void init(App app){
        mApp = app;
    }
    
}