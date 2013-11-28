
package cn.supra.supralayer_i.model;

/**
 * App的基类
 * 
 * @author supra
 */
public class AppImpl implements App {
    private String mAppID;
    private String mNane;
    private String mIconUri;

    public AppImpl(String appID) {
        this(appID, "");
    }

    public AppImpl(String appID, String name) {
        this(appID, name, "");
    }

    public AppImpl(String appID, String name, String iconUri) {
        mAppID = appID;
        mNane = name;
        mIconUri = iconUri;
    }

    @Override
    public String getAppID() {
        return mAppID;
    }

    @Override
    public String getName() {
        return mNane;
    }

    @Override
    public String getIconUri() {
        return mIconUri;
    }

}
