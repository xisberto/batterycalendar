package net.xisberto.batterycalendar.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class LocalDatabase {
	private SQLiteDatabase database;
	private LocalDatabaseOpenHelper dbHelper;
	private String[] columns = { LocalDatabaseOpenHelper.COLUMN_ID,
			LocalDatabaseOpenHelper.COLUMN_GOOGLE_ID,
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
		DateTime date_time = new DateTime(date_start.getTimeInMillis());
		String formated_date_start = date_time.toStringRfc3339();
		date_start.add(Calendar.MINUTE, 20);
		date_time = new DateTime(date_start.getTimeInMillis());
		String formated_date_end = date_time.toStringRfc3339();

		ContentValues cv = new ContentValues();
		cv.put(LocalDatabaseOpenHelper.COLUMN_NAME, name);
		cv.put(LocalDatabaseOpenHelper.COLUMN_DETAILS, details);
		cv.put(LocalDatabaseOpenHelper.COLUMN_DATE_START, formated_date_start);
		cv.put(LocalDatabaseOpenHelper.COLUMN_DATE_END, formated_date_end);
		cv.put(LocalDatabaseOpenHelper.COLUMN_LEVEL_START, level_start);

		return database.insert(LocalDatabaseOpenHelper.TABLE_EVENTS, null, cv);
	}

	public Event endEvent(long event_id, Calendar date_end, int level_end) {
		DateTime date_time = new DateTime(date_end.getTimeInMillis());
		String formated_date_end = date_time.toStringRfc3339();

		ContentValues values = new ContentValues();
		values.put(LocalDatabaseOpenHelper.COLUMN_DATE_END, formated_date_end);
		values.put(LocalDatabaseOpenHelper.COLUMN_LEVEL_END, level_end);

		database.update(LocalDatabaseOpenHelper.TABLE_EVENTS, values,
				LocalDatabaseOpenHelper.COLUMN_ID + " = " + event_id, null);
		
		return getEvent(event_id);
	}
	
	public void updateDetails(long event_id, String details) {
		ContentValues values = new ContentValues();
		values.put(LocalDatabaseOpenHelper.COLUMN_DETAILS, details);
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
	
	public Event getEvent(long event_id) {
		Event result = null;
		Cursor cursor = database.query(LocalDatabaseOpenHelper.TABLE_EVENTS,
				columns,
				LocalDatabaseOpenHelper.COLUMN_ID + " = ?",
				new String[] {Long.toString(event_id)},
				null, null, null);
		cursor.moveToFirst();
		if (! cursor.isAfterLast()) {
			result = new Event();
			DateTime date_start = new DateTime(cursor.getString(4));
			DateTime date_end = new DateTime(cursor.getString(5));
			result
				.setId(cursor.getString(1))
				.setSummary(cursor.getString(2))
				.setDescription(cursor.getString(3))
				.setStart(new EventDateTime().setDateTime(date_start))
				.setEnd(new EventDateTime().setDateTime(date_end));
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
					+ cursor.getString(2) + ", " + cursor.getString(3)
					+ ", started at " + cursor.getString(4) + " with level "
					+ cursor.getString(6) + " ended " + cursor.getString(5)
					+ " with level " + cursor.getString(7);
			log_msg.add(msg_line);
			cursor.moveToNext();
		}
		cursor.close();
		return log_msg;
	}

}
