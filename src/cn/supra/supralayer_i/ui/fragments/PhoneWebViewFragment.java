package cn.supra.supralayer_i.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.supra.supralayer_i.R;

public class PhoneWebViewFragment extends BaseWebViewFragment {
    
    public PhoneWebViewFragment() { 
        super();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mParentView == null) {
            mParentView = (ViewGroup) inflater.inflate(R.layout.webview_container_fragment, container, false);
        }
        
        onViewCreated();
        
        return mParentView;
    }

    @Override
    public void cleanData() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean canGoBack() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canForward() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void goBack() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void goForward() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getFlag() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void onActive() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterActive() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object clientSnapshot(boolean fullcontent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onClientViewResult(Intent intent, int resultCode, int requestCode) {
        // TODO Auto-generated method stub
        
    }
}
