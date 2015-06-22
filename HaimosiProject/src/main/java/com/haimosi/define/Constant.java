package com.haimosi.define;

public class Constant {

	// TIME PATTERN
	public static final String PATTERN_DD_MM_YYYY_HH_MM_SS  = "dd-MM-yyyy HH:mm:ss";
	public static final String PATTERN_DD_MM_YYYY           = "dd-MM-yyyy";
	public static final String PATTERN_HH_MM_SS             = "HH:mm:ss";
	public static final String PATTERN_HH_MM_SS_MS          = "HH:mm:ss:SSS";
	public static final String PATTERN_YYYY_MM_DD           = "yyyy-MM-dd";
	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS  = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTERN_YYYY_MM_DD_HH_MM     = "yyyy-MM-dd HH:mm";

	// PAYMENT METHOD
	public static final byte   PAYMENT_UNCHOOSE             = 0;
	public static final byte   PAYMENT_CASH                 = 1;
	public static final byte   PAYMENT_APP                  = 2;

	// WEBSOCKET COMMAND
	public static final int    SOCKET_COMMAND_LOGIN         = 1;
	public static final int    SOCKET_COMMAND_PUSH_TRANS    = 2;
	public static final int    SOCKET_COMMAND_CONFIRM_TRANS = 3;
	public static final int    SOCKET_COMMAND_UPDATE_ITEM   = 4;

	// SYSTEM SEPERATOR
	public static final String SEPERATOR                    = System.getProperty("file.separator");

	// USER STATUS
	public static final int    USER_ROLE_ADMIN              = 1;
	public static final int    USER_ROLE_MEMBER             = 2;

	// USER STATUS
	public static final byte   USER_STATUS_LOCKED           = 0;
	public static final byte   USER_STATUS_ACTIVATE         = 1;
	public static final byte   USER_STATUS_INACTIVATE       = 2;

	// TRANSACTION STATUS
	public static final byte   TRANS_WAIT                   = 1;
	public static final byte   TRANS_DONE                   = 2;

	// TRANSACTION FILTER
	public static final byte   TRANS_FILTER_DAY             = 1;
	public static final byte   TRANS_FILTER_WEIGHT          = 2;
	public static final byte   TRANS_FILTER_FAVORITE        = 3;
}
