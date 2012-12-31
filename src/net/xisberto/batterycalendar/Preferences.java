package net.xisberto.batterycalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class Preferences {
	public static final String LAST_EVENT_ID = "last_event_id";
	
	private Context context;

	public Preferences(Context context) {
		this.context = context;
	}

	public long getLastEventId() {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getLong(LAST_EVENT_ID, 0);
	}
	
	public void setLastEventId(long id) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putLong(LAST_EVENT_ID, id);
		apply(editor);
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
