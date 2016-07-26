package tw.supra.ani;

import android.app.Application;

import tw.supra.lib.supower.Supra;

/**
 * Application
 * Created by supra on 16-7-26.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Supra.init(this);
    }
}
