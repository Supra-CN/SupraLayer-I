package cn.supra.supralayer_i;

import android.content.Context;
import android.widget.FrameLayout;

public class BaseClientView extends FrameLayout {

	public BaseClientView(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
//	protected void onCreate(Bundle savedInstanceState);
//	protected void onStart();
//	protected void onRestart();
//	protected void onResume();
//	protected void onPause();
//	protected void onStop();
//	protected void onDestroy();

}
