package net.xisberto.batterycalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class Preferences {
	public static final String LAST_EVENT_ID = "last_event_id",
			ACCOUNT_NAME = "account_name",
			CALENDAR_ID = "calendar_id";

	private SharedPreferences preferences;

	public Preferences(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public long getLastEventId() {
		return preferences.getLong(LAST_EVENT_ID, 0);
	}

	public void setLastEventId(long id) {
		apply(preferences.edit().putLong(LAST_EVENT_ID, id));
	}

	public String getAccountName() {
		return preferences.getString(ACCOUNT_NAME, null);
	}

	public void setAccountName(String account_name) {
		apply(preferences.edit().putString(ACCOUNT_NAME, account_name));
	}
	
	public String getCalendarId() {
		return preferences.getString(CALENDAR_ID, null);
	}
	
	public void setCalendarId(String id) {
		apply(preferences.edit().putString(CALENDAR_ID, id));
	}

	@SuppressLint("NewApi")
	public void apply(Editor editor) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

}
