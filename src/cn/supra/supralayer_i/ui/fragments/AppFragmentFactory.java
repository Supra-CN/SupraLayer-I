package cn.supra.supralayer_i.ui.fragments;

import cn.supra.supralayer_i.model.App;
import cn.supra.supralayer_i.model.AppImpl;

public class AppFragmentFactory {
    
    private static AppFragmentFactory instance;
    
    public static AppFragmentFactory getInstance() {
        if(null == instance){
            instance = new AppFragmentFactory();
        }
        return instance;
    }
    
    
    private AppFragmentFactory() {
        // TODO Auto-generated constructor stub
    }
    
    public BaseAppFragment createAppFragment(App app){
        BaseAppFragment fragment = new BaseAppFragment();
        fragment.init(app);
        return fragment;
    }
    
    public LauncherFragment creatLauncherFragment(){
        App launcherApp = new AppImpl("launcher", "launcher");
        LauncherFragment launcher = new LauncherFragment();
        launcher.init(launcherApp);
        return launcher;
    }
    
    
}
