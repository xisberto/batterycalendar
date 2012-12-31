package net.xisberto.batterycalendar;

import java.util.Calendar;

import net.xisberto.batterycalendar.database.LocalDatabase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class BatteryChangedReceiver extends BroadcastReceiver {
	public BatteryChangedReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		LocalDatabase db = new LocalDatabase(context);
		Preferences prefs = new Preferences(context);
		db.open();
		Log.i(BatteryChangedReceiver.class.getName(), "Intent action: "+intent.getAction());
		
		IntentFilter battery_filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent battery_intent = context.registerReceiver(null, battery_filter);
		int level = battery_intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		
		if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			long last_event_id = db.startEvent("Tests", "details", Calendar.getInstance(), level);
			prefs.setLastEventId(last_event_id);
			Toast.makeText(context, "Started charging event "+ last_event_id, Toast.LENGTH_SHORT).show();
		} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			long last_event_id = prefs.getLastEventId();
			db.endEvent(last_event_id, Calendar.getInstance(), level);
			Toast.makeText(context, "Stoped charging event "+ last_event_id, Toast.LENGTH_SHORT).show();
		}
		db.logEvents();
		db.close();
	}
}
