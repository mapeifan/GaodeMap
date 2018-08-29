package com.test.map.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.test.map.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class UserDataBaseOperate {

	private static final String TAG = "DBMap";
	private static final boolean DEBUG = true;

	protected SQLiteDatabase mDB = null;

	public UserDataBaseOperate(SQLiteDatabase db) {
		if (null == db) {
			throw new NullPointerException("The db cannot be null.");
		}
		mDB = db;
	}
	public long insertToHistory(MapHistoryBean bean) {
		ContentValues values = new ContentValues();
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_NAME, bean.getName());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_ADDRESS, bean.getAddress());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_DISTANCE, bean.getDistance());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_LAT, bean.getLat());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_LON, bean.getLon());
		return mDB.insert(UserSQLiteOpenHelper.DATABASE_TABLE_USER, null,
				values);
	}

	public long updateHistory(MapHistoryBean bean) {
		ContentValues values = new ContentValues();
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_NAME, bean.getName());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_ADDRESS, bean.getAddress());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_DISTANCE, bean.getDistance());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_LAT, bean.getLat());
		values.put(UserSQLiteOpenHelper.MAP_HISTORY_LON, bean.getLon());
		return mDB.update(UserSQLiteOpenHelper.DATABASE_TABLE_USER, values,
				"_id=?", new String[] { ""+bean.get_id() });
	}

	public void deleteAll() {
		mDB.delete(UserSQLiteOpenHelper.DATABASE_TABLE_USER, null, null);
		LogUtil.e("userDataBaseOperate.deleteAll();22222222222222");
	}

	public long deleteUserByname(String name){
		
		return mDB.delete("user_info", "name=?", new String[]{name});
	}

	public long getCount(String conditions, String[] args) {
		long count = 0;
		if (TextUtils.isEmpty(conditions)) {
			conditions = " 1 = 1 ";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT COUNT(1) AS count FROM ");
		builder.append(UserSQLiteOpenHelper.DATABASE_TABLE_USER).append(" ");
		builder.append("WHERE ");
		builder.append(conditions);
		if (DEBUG)
			Log.d(TAG, "SQL: " + builder.toString());
		Cursor cursor = mDB.rawQuery(builder.toString(), args);
		if (null != cursor) {
			if (cursor.moveToNext()) {
				count = cursor.getLong(cursor.getColumnIndex("count"));
			}
			cursor.close();
		}
		return count;
	}
	public List<MapHistoryBean> findAll() {
		List<MapHistoryBean> userList = new ArrayList<MapHistoryBean>();
		//order by modifytime desc
		Cursor cursor = mDB.query(UserSQLiteOpenHelper.DATABASE_TABLE_USER,
				null, null, null, null, null, null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				MapHistoryBean bean = new MapHistoryBean();
				bean.set_id(cursor.getLong(cursor
						.getColumnIndex(UserSQLiteOpenHelper.MAP_HISTORY_ID)));
				bean.setName(cursor.getString(cursor
						.getColumnIndex(UserSQLiteOpenHelper.MAP_HISTORY_NAME)));
				bean.setAddress(cursor.getString(cursor
						.getColumnIndex(UserSQLiteOpenHelper.MAP_HISTORY_ADDRESS)));
				bean.setDistance(cursor.getString(cursor
						.getColumnIndex(UserSQLiteOpenHelper.MAP_HISTORY_DISTANCE)));
				bean.setLat(cursor.getString(cursor
						.getColumnIndex(UserSQLiteOpenHelper.MAP_HISTORY_LAT)));
				bean.setLon(cursor.getString(cursor
						.getColumnIndex(UserSQLiteOpenHelper.MAP_HISTORY_LON)));
				userList.add(bean);
			}
			cursor.close();
		}
		return userList;
	}

	// 删除全部数据
	public boolean delete() {

		String whereClause = "id=?";
		try {
			mDB.delete("map_info", whereClause, null);
		} catch (SQLException e) {

			return false;
		}
		LogUtil.e("userDataBaseOperate.deleteAll();111");
		return true;
	}
}
