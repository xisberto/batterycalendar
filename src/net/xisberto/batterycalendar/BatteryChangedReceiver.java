package net.xisberto.batterycalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryChangedReceiver extends BroadcastReceiver {
	public BatteryChangedReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent_service = new Intent(context, BatteryCalendarService.class);
		intent_service.setAction(intent.getAction());
		context.startService(intent_service);
		
		Intent intent_information = new Intent(InformationActivity.ACTION_INFORM);
		context.sendBroadcast(intent_information);
	}
}
