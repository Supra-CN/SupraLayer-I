package cn.supra.supralayer_i.ui.fragments;

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

}
