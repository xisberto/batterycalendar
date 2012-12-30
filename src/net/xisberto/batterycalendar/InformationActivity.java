package net.xisberto.batterycalendar;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class InformationActivity extends Activity {
	public static String ACTION_INFORM = "net.xisberto.batterycalendar.INFORMATION";
	private BroadcastReceiver information_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(context.getPackageName(), "Received broadcast in activity");
			updateInfo(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		IntentFilter battery_filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent battery_intent = registerReceiver(null, battery_filter);
		updateInfo(battery_intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(information_receiver, new IntentFilter(ACTION_INFORM));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(information_receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_information, menu);
		return true;
	}

	private void updateInfo(Intent intent) {
		TextView info_text = (TextView) findViewById(R.id.text_info);
		CharSequence text = "No information available";
		switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS,
				BatteryManager.BATTERY_STATUS_UNKNOWN)) {
		case BatteryManager.BATTERY_STATUS_FULL:
			text = "Battery charged";
			break;
		case BatteryManager.BATTERY_STATUS_CHARGING:
			text = "Battery charging";
			switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
			case BatteryManager.BATTERY_PLUGGED_AC:
				text = text + " on AC";
				break;
			case BatteryManager.BATTERY_PLUGGED_USB:
				text = text + " on USB";
				break;
			case BatteryManager.BATTERY_PLUGGED_WIRELESS:
				text = text + " via Wirless";
				break;
			default:
				text = text + "\n but not plugged (!)";
				break;
			}
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
			text = "Battery discharging";
			break;
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
			text = "Battery not charging";
			break;
		case BatteryManager.BATTERY_STATUS_UNKNOWN:
		default:
			text = "Battery status unknown";
			break;
		}
		text = text + "\nCurrent level: "
				+ intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
				+ ", maximum is "
				+ intent.getIntExtra(BatteryManager.EXTRA_SCALE, 101);
		
		info_text.setText(text);
	}

}
