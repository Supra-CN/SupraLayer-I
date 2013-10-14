package cn.supra.supralayer_i.ui.clients;

import cn.supra.supralayer_i.R;
import android.content.Context;
import android.view.View;

public class LauncherClient extends BaseClient{

	public LauncherClient(Context context) {
		super(context);
		View.inflate(context, R.layout.launcher, this);
	}

}
