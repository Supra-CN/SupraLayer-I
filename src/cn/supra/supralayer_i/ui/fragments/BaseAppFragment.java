package cn.supra.supralayer_i.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import cn.supra.supralayer_i.ui.UIManager;
import cn.supra.supralayer_i.ui.components.CustomWebView;

import java.util.UUID;

public abstract class BaseAppFragment extends Fragment {
    
    //==========================================================================
    // 
    //==========================================================================

    protected UUID mUUID;
    
    protected UIManager mUIManager;
    protected ViewGroup mParentView;
    protected CustomWebView mWebView;
    
    protected BaseAppFragment() {
        mUUID = UUID.randomUUID();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    
    public UUID getUUID() {
        return mUUID;
    }
    
    public int getAppID(){
        return 0;
    }
    
    /**
     * 清除和客户视图相关的用户数据缓存等
     */
    public abstract void cleanData();
    public abstract boolean canGoBack() ;
    public abstract boolean canForward() ;
    public abstract void  goBack();
    public abstract void goForward();
    
    /**
     * 返回客户视图的一些控制参数
     * @return
     */
    public abstract int getFlag() ;
    //在客户视图布局之前调用
    public abstract void onActive();
    //在客户区视图布局后调用
    public abstract void afterActive();
    /**
     * 获取客户区域视图
     * @param fullcontent 整个内容还是可视区域
     * @return
     */
    public abstract Object clientSnapshot(boolean fullcontent);
    
    /**
     * 需要获得从外部Activity返回信息时使用此接口
     * @param intent
     */
    public abstract void onClientViewResult(Intent intent,int resultCode,int requestCode);
    
    
    //==========================================================================
    // MxClientView
    //==========================================================================

    public int mFlags = 0;

    /**
     * 标识当前视图所在群组id
     */
    String mGroupId = null;
}