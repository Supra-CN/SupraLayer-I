package cn.supra.supralayer_i;

import android.content.Context;
import android.widget.FrameLayout;

public class BaseClientView extends FrameLayout {

	public BaseClientView(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

}
