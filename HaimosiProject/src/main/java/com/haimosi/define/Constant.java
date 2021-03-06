package com.haimosi.define;

public class Constant {

	// ENCRYPT PASSWORD - DO NOT CHANGE THIS
	public static final String ENCRYPT_PASSWORD                    = "938de092fvhw30DERv";

	// TIME PATTERN
	public static final String PATTERN_DD_MM_YYYY                  = "dd-MM-yyyy";
	public static final String PATTERN_DD_MM_YYYY_HH_MM_SS         = "dd-MM-yyyy HH:mm:ss";
	public static final String PATTERN_HH_MM_SS                    = "HH:mm:ss";
	public static final String PATTERN_HH_MM_SS_MS                 = "HH:mm:ss:SSS";
	public static final String PATTERN_YYYY_MM_DD                  = "yyyy-MM-dd";
	public static final String PATTERN_YYYY_MM_DD_HH_MM            = "yyyy-MM-dd HH:mm";
	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS         = "yyyy-MM-dd HH:mm:ss";

	// PAYMENT METHOD
	public static final byte   PAYMENT_APP                         = 2;
	public static final byte   PAYMENT_CASH                        = 1;
	public static final byte   PAYMENT_UNCHOOSE                    = 0;

	// SYSTEM SEPERATOR
	public static final String SEPERATOR                           = System.getProperty("file.separator");

	// WEBSOCKET COMMAND
	public static final int    SOCKET_COMMAND_ACCEPT_TRANS         = 5;
	public static final int    SOCKET_COMMAND_CONFIRM_ACCEPT_TRANS = 6;
	public static final int    SOCKET_COMMAND_CONFIRM_TRANS        = 3;
	public static final int    SOCKET_COMMAND_LOGIN                = 1;
	public static final int    SOCKET_COMMAND_PUSH_TRANS           = 2;
	public static final int    SOCKET_COMMAND_UPDATE_ITEM          = 4;

	// TRANSACTION STATUS
	public static final byte   TRANS_STATUS_WAIT                          = 1;
	public static final byte   TRANS_STATUS_DENY                          = 3;
	public static final byte   TRANS_STATUS_DONE                          = 2;
	public static final byte   TRANS_STATUS_REFUND                        = 4;

	// TRANSACTION FILTER
	public static final byte   TRANS_FILTER_DAY                    = 1;
	public static final byte   TRANS_FILTER_FAVORITE               = 3;
	public static final byte   TRANS_FILTER_WEIGHT                 = 2;

	// USER ROLE
	public static final int    USER_ROLE_ADMIN                     = 1;
	public static final int    USER_ROLE_MEMBER                    = 2;

	// USER STATUS
	public static final byte   USER_STATUS_ACTIVATE                = 1;
	public static final byte   USER_STATUS_INACTIVATE              = 2;
	public static final byte   USER_STATUS_LOCKED                  = 0;
}
