package net.xisberto.batterycalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryChangedReceiver extends BroadcastReceiver {
	public BatteryChangedReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(context.getPackageName(), "Received system broadcast");
		Intent information = intent.cloneFilter();
		information.setAction(InformationActivity.ACTION_INFORM);
		information.setClass(context, InformationActivity.class);
		information.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(information);
	}
}
