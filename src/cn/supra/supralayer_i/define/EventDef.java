package cn.supra.supralayer_i.define;

/**
 * 事件定义类，
 * SPACE_*:定义事件空间,表示可以定义多少个同级事件；
 * MASK_*:定义事件掩码，表示一类事件；
 * EVT_*：定义元事件，表示不可再分类的事件；
 * 新定义事件根据需求随时增加；
 * @author supra
 *
 */
public final class EventDef {
	
//	总定义空间
	public static final int SPACE = 0x80000000;
	
//　ACTION定义空间，最多4096种ACTION掩码
	public static final int SPACE_ACTION = 0x1000;
	
//	ACTION_CMD定义空间，掩码ACTION_CMD，表示命令类事件，
	public static final int SPACE_ACTION_CMD = SPACE / SPACE_ACTION;
	public static final int MASK_ACTION_CMD = SPACE_ACTION_CMD * 0;
	public static final int EVT_CMD_OPEN_CALL = MASK_ACTION_CMD + 0;
	public static final int EVT_CMD_OPEN_SMS = MASK_ACTION_CMD + 1;
	public static final int EVT_CMD_OPEN_CONTACTS = MASK_ACTION_CMD + 2;
	public static final int EVT_CMD_OPEN_SEARCHE = MASK_ACTION_CMD + 3;
	public static final int EVT_CMD_OPEN_CAMERA = MASK_ACTION_CMD + 4;
	
	
}
