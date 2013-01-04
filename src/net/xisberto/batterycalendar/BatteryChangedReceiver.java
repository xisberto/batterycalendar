package net.xisberto.batterycalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryChangedReceiver extends BroadcastReceiver {
	public BatteryChangedReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent_event = new Intent(context, BatteryEventActivity.class);
		intent_event.setAction(intent.getAction());
		intent_event.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent_event);
		
		Intent intent_information = new Intent(InformationActivity.ACTION_INFORM);
		context.sendBroadcast(intent_information);
	}
}
