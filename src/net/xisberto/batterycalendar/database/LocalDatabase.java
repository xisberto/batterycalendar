package net.xisberto.batterycalendar.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

public class LocalDatabase {
	private SQLiteDatabase database;
	private LocalDatabaseOpenHelper dbHelper;
	private String[] columns = { LocalDatabaseOpenHelper.COLUMN_ID,
			LocalDatabaseOpenHelper.COLUMN_NAME,
			LocalDatabaseOpenHelper.COLUMN_DETAILS,
			LocalDatabaseOpenHelper.COLUMN_DATE_START,
			LocalDatabaseOpenHelper.COLUMN_DATE_END,
			LocalDatabaseOpenHelper.COLUMN_LEVEL_START,
			LocalDatabaseOpenHelper.COLUMN_LEVEL_END };

	public LocalDatabase(Context context) {
		dbHelper = new LocalDatabaseOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public long startEvent(String name, String details, Calendar date_start,
			int level_start) {
		String formated_date_start = DateFormat.format("yyyy-MM-dd kk:mm",
				date_start).toString();
		date_start.add(Calendar.MINUTE, 20);
		String formated_date_end = DateFormat.format("yyyy-MM-dd kk:mm",
				date_start).toString();

		ContentValues cv = new ContentValues();
		cv.put(LocalDatabaseOpenHelper.COLUMN_NAME, name);
		cv.put(LocalDatabaseOpenHelper.COLUMN_DETAILS, details);
		cv.put(LocalDatabaseOpenHelper.COLUMN_DATE_START, formated_date_start);
		cv.put(LocalDatabaseOpenHelper.COLUMN_DATE_END, formated_date_end);
		cv.put(LocalDatabaseOpenHelper.COLUMN_LEVEL_START, level_start);

		return database.insert(LocalDatabaseOpenHelper.TABLE_EVENTS, null, cv);
	}

	public void endEvent(long event_id, Calendar date_end, int level_end) {
		String formated_date_end = DateFormat.format("yyyy-MM-dd kk:mm",
				date_end).toString();

		ContentValues values = new ContentValues();
		values.put(LocalDatabaseOpenHelper.COLUMN_DATE_END, formated_date_end);
		values.put(LocalDatabaseOpenHelper.COLUMN_LEVEL_END, level_end);

		database.update(LocalDatabaseOpenHelper.TABLE_EVENTS, values,
				LocalDatabaseOpenHelper.COLUMN_ID + " = " + event_id, null);
	}

	public void saveGoogleId(long event_id, String googleId) {
		ContentValues values = new ContentValues();
		values.put(LocalDatabaseOpenHelper.COLUMN_GOOGLE_ID, googleId);
		database.update(LocalDatabaseOpenHelper.TABLE_EVENTS, values,
				LocalDatabaseOpenHelper.COLUMN_ID + " = " + event_id, null);
	}
	
	public String getGoogleId(long event_id) {
		String result = null;
		Cursor cursor = database.query(LocalDatabaseOpenHelper.TABLE_EVENTS,
				new String[] {LocalDatabaseOpenHelper.COLUMN_GOOGLE_ID},
				LocalDatabaseOpenHelper.COLUMN_ID + " = ?",
				new String[] {Long.toString(event_id)},
				null, null, null);
		cursor.moveToFirst();
		if (! cursor.isAfterLast()) {
			result = cursor.getString(0);
		}
		cursor.close();
		return result;
	}

	public ArrayList<String> logEvents() {
		ArrayList<String> log_msg = new ArrayList<String>();
		Cursor cursor = database.query(LocalDatabaseOpenHelper.TABLE_EVENTS,
				columns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String msg_line = "Event: " + cursor.getLong(0) + ", "
					+ cursor.getString(1) + ", " + cursor.getString(2)
					+ ", started " + cursor.getString(3) + " with level "
					+ cursor.getString(5) + " ended " + cursor.getString(4)
					+ " with level " + cursor.getString(6);
			log_msg.add(msg_line);
			cursor.moveToNext();
		}
		cursor.close();
		return log_msg;
	}

}
