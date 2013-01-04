package net.xisberto.batterycalendar;

import java.util.Calendar;
import java.util.TimeZone;

import net.xisberto.batterycalendar.database.LocalDatabase;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.samples.calendar.android.AsyncInsertEvent;
import com.google.api.services.samples.calendar.android.AsyncUpdateEvent;
import com.google.api.services.samples.calendar.android.EventInfo;

public class BatteryEventActivity extends SyncEventActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_NoDisplay);

		synchronized (this) {
			try {
				Preferences prefs = new Preferences(getApplicationContext());
				LocalDatabase db = new LocalDatabase(getApplicationContext());
				db.open();

				Intent battery_intent = registerReceiver(null, new IntentFilter(
						Intent.ACTION_BATTERY_CHANGED));

				Log.i(TAG, getIntent().getAction());
				if (getIntent().getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
					startEvent(db, prefs, battery_intent.getExtras());
				} else if (getIntent().getAction().equals(
						Intent.ACTION_POWER_DISCONNECTED)) {
					endEvent(db, prefs, battery_intent.getExtras());
				}

				for (String msg : db.logEvents()) {
					Log.i(TAG, msg);
				}

				db.close();
			} catch (Exception e) {
			}	
		}
		finish();
	}

	/**
	 * This is called when the event is inserted or updated
	 */
	@Override
	public void refreshView() {
		EventInfo[] events = model.toSortedArray();
		LocalDatabase db = new LocalDatabase(getApplicationContext());
		db.open();
		long last_event_id = prefs.getLastEventId();
		
		Log.i(TAG, "Saving Google id " + events[events.length-1].id + " on event with id " + last_event_id);
		
		db.saveGoogleId(last_event_id, events[events.length-1].id);
		db.close();
	}

	private void startEvent(LocalDatabase db, Preferences prefs, Bundle extras) {
		String charging = getResources().getString(R.string.charging)+ " " + prefs.getDeviceName();
		
		String plugged = getResources().getString(R.string.plugged) + " ";
		int plugged_by = extras.getInt(BatteryManager.EXTRA_PLUGGED, 0);
		switch (plugged_by) {
		case BatteryManager.BATTERY_PLUGGED_AC:
			plugged = plugged + getResources().getString(R.string.plugged_ac);
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:
			plugged = plugged + getResources().getString(R.string.plugged_usb);
			break;
		case BatteryManager.BATTERY_PLUGGED_WIRELESS:
			plugged = plugged + getResources().getString(R.string.plugged_wireless);
			break;
		default:
			break;
		}
		
		int level = extras.getInt(BatteryManager.EXTRA_LEVEL, 0);
		String starting_level = getResources().getString(R.string.starting_level) + " " + level + "%";
		
		long last_event_id = db.startEvent(charging, "details", Calendar.getInstance(), level);
		prefs.setLastEventId(last_event_id);
		
		EventDateTime start = new EventDateTime();
		start.setDateTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		start.setTimeZone(TimeZone.getDefault().getID());
		EventDateTime end = new EventDateTime();
		end.setDateTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		end.setTimeZone(TimeZone.getDefault().getID());
		
		Event event = new Event()
			.setSummary(charging)
			.setDescription(plugged+"\n"+starting_level)
			.setStart(start)
			.setEnd(end);
		
		new AsyncInsertEvent(this, prefs.getCalendarId(), event).execute();
	}

	private void endEvent(LocalDatabase db, Preferences prefs, Bundle extras) {
		Log.i(TAG, "Device disconnected");
		int level = extras.getInt(BatteryManager.EXTRA_LEVEL, 0);
		String ending_level = getResources().getString(R.string.ending_level) + " " + level + "%";
		long last_event_id = prefs.getLastEventId();
		db.endEvent(last_event_id, Calendar.getInstance(), level);

		String google_id = db.getGoogleId(last_event_id);
		Log.i(TAG, "Ending GoogleId " + google_id + " in TimeZone " + TimeZone.getDefault().getID());
		
		EventInfo event_info = model.get(google_id);
		EventDateTime end = new EventDateTime();
		end.setDateTime(event_info.end);
		end.setTimeZone(TimeZone.getDefault().getID());
		
		Log.i(TAG, "Ending event at " + end.getDateTime().toStringRfc3339() + " " + ending_level);
		
		Event event = new Event()
			.setId(google_id)
			.setDescription(event_info.description + "\n" + ending_level)
			.setEnd(end);
		
		new AsyncUpdateEvent(this, prefs.getCalendarId(), google_id, event).execute();
	}

}
