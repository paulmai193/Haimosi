package com.haimosi.define;

/**
 * The Class Config.
 * 
 * @author Paul Mai
 */
public class Config {

	/* Data encrypt password */
	public static String encrypt_password        = "";
	public static int    interval_validate_pool  = 10;
	/* DAO pool */
	public static int    num_core_dao_in_pool    = 1;

	/* Thread pool */
	public static int    num_core_thread_in_pool = 1;
	public static int    num_max_dao_in_pool     = 5;
	public static int    num_max_thread_in_pool  = 5;

	/* Resource path */
	public static String resource_avatar_path    = "";
	public static String resource_config_path    = "";
	public static String resource_item_path      = "";
	public static String resource_lucene_index   = "";
	public static String resource_template_path  = "";
	public static String resource_trans_path     = "";

	public static int    thread_priority         = 1;
}
