package com.test.map.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLiteOpenHelper extends SQLiteOpenHelper {

	private static String REMOTE_LIVE_DATABASE_NAME = "mapHistory.db";
	private static int version = 1;
	public static final String DATABASE_TABLE_USER = "map_info";
	public static final String MAP_HISTORY_ID = "_id";
	public static final String MAP_HISTORY_NAME = "mapHistoryName";
	public static final String MAP_HISTORY_ADDRESS = "mapHistoryAddress";
	public static final String MAP_HISTORY_DISTANCE = "mapHistoryDistance";
	public static final String MAP_HISTORY_LAT = "mapHistoryLat";
	public static final String MAP_HISTORY_LON = "mapHistoryLon";

	private final String REMOTE_LIVE_DATABASE_CREATE="create table if not exists "
			+ DATABASE_TABLE_USER
			+ " (_id integer primary key autoincrement, "
			+ "mapHistoryName text not null,"
			+ "mapHistoryAddress text not null,"
			+ "mapHistoryDistance text not null,"
			+"mapHistoryLat text not null,"
			+"mapHistoryLon text not null);";
	private static UserSQLiteOpenHelper mInstance = null;
	private static Context mContext;

	public static UserSQLiteOpenHelper getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new UserSQLiteOpenHelper(context);
			mContext = context;
		}
		return mInstance;
	}

	private UserSQLiteOpenHelper(Context context) {
		super(context, REMOTE_LIVE_DATABASE_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(REMOTE_LIVE_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {


	}

	
}
