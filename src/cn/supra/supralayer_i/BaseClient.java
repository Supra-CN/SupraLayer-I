package cn.supra.supralayer_i;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

public class BaseClient extends FrameLayout {

	public BaseClient(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	protected void onViewCreate(Bundle savedInstanceState){}
	protected void onViewStart(){}
	protected void onViewRestart(){}
	protected void onViewResume(){}
	protected void onViewPause(){}
	protected void onViewStop(){}
	protected void onViewDestroy(){}

}
