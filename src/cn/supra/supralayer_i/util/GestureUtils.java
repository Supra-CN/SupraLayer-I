package cn.supra.supralayer_i.util;

import android.view.MotionEvent;

public class GestureUtils {
	//the distance was considered the minimum sliding 
	public final static int MIN_SCROLL_DISTANCE = 10;
	
	public final static int SCROLL_RIGHT = 0;
	public final static int SCROLL_LEFT = 1;
	public final static int SCROLL_DOWN = 2;
	public final static int SCROLL_UP = 3;
	public final static int SCROLL_UNKNOWN = -1;
	
	public static final int DetectorScrollDirectoin(MotionEvent e1, MotionEvent e2) {
		float startX = e1.getX();
		float startY = e1.getY();

		float endX = e2.getX();
		float endY = e2.getY();

		float dx = endX - startX;
		float dy = endY - startY;

		// Horizontal scrolling is considered
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0 && dx > MIN_SCROLL_DISTANCE) {
				return SCROLL_RIGHT;
			} else if (dx < 0 && Math.abs(dx) > MIN_SCROLL_DISTANCE) {
				return SCROLL_LEFT;
			}

		} else if (Math.abs(dy) > Math.abs(dx)) {
			if (dy > 0 && dy > MIN_SCROLL_DISTANCE) {
				return SCROLL_DOWN;
			} else if (dy < 0 && Math.abs(dy) > MIN_SCROLL_DISTANCE) {
				return SCROLL_UP;
			}
		}

		return SCROLL_UNKNOWN;
	}
}
