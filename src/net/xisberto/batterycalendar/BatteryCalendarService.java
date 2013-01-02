package net.xisberto.batterycalendar;

import java.util.Calendar;

import net.xisberto.batterycalendar.database.LocalDatabase;
import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryCalendarService extends IntentService {
	
	public BatteryCalendarService() {
		super("BatteryCalendarService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		synchronized (this) {
			try {
				Preferences prefs = new Preferences(getApplicationContext());
				LocalDatabase db = new LocalDatabase(getApplicationContext());
				db.open();
				
				Intent battery_intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
				int level = battery_intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				
				if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
					startEvent(db, prefs, level);
				} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
					endEvent(db, prefs, level);
				}
				
				for (String msg : db.logEvents()) {
					Log.i(getClass().getCanonicalName(), msg);
				}
				
				db.close();
			} catch (Exception e) {
			}
		}
	}
	
	private void startEvent(LocalDatabase db, Preferences prefs, int level) {
		long last_event_id = db.startEvent("Tests", "details", Calendar.getInstance(), level);
		prefs.setLastEventId(last_event_id);
	}
	
	private void endEvent(LocalDatabase db, Preferences prefs, int level) {
		long last_event_id = prefs.getLastEventId();
		db.endEvent(last_event_id, Calendar.getInstance(), level);
	}

}
