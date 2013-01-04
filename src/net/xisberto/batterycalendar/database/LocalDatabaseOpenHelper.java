package net.xisberto.batterycalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_EVENTS = "events";
	public static final String COLUMN_ID = "_id",
			COLUMN_GOOGLE_ID = "google_id",
			COLUMN_NAME = "name",
			COLUMN_DETAILS = "details",
			COLUMN_DATE_START = "date_start",
			COLUMN_DATE_END = "date_end",
			COLUMN_LEVEL_START = "level_start",
			COLUMN_LEVEL_END = "level_end";

	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 3;

	public LocalDatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE " + TABLE_EVENTS + "(" 
				+ COLUMN_ID	+ " integer primary key autoincrement, "
				+ COLUMN_GOOGLE_ID + " text, "
				+ COLUMN_NAME + " text not null, "
				+ COLUMN_DETAILS + " text, "
				+ COLUMN_DATE_START + " text not null, "
				+ COLUMN_DATE_END + " text not null, "
				+ COLUMN_LEVEL_START + " integer, "
				+ COLUMN_LEVEL_END + " integer "
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LocalDatabaseOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion
				+ " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS "+ TABLE_EVENTS);
		onCreate(database);
	}

}
