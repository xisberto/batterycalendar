package net.xisberto.batterycalendar;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class InformationActivity extends SyncCalendarActivity implements OnClickListener {
	public static String ACTION_INFORM = "net.xisberto.batterycalendar.INFORMATION";

	private BroadcastReceiver information_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateBatteryInfo();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);

		findViewById(R.id.btn_select_account).setOnClickListener(this);
		findViewById(R.id.btn_sync_off).setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(information_receiver, new IntentFilter(ACTION_INFORM));
		prepareUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(information_receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_GOOGLE_PLAY_SERVICES:
			if (resultCode == Activity.RESULT_OK) {
				haveGooglePlayServices();
			} else {
				checkGooglePlayServicesAvailable();
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				startActivity(new Intent(this, CalendarListActivity.class));
			} else {
				chooseAccount();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == Activity.RESULT_OK && data != null
					&& data.getExtras() != null) {
				String accountName = data.getExtras().getString(
						AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					prefs.setAccountName(accountName);
					startActivity(new Intent(this, CalendarListActivity.class));
				}
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_information, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_account:
			if (checkGooglePlayServicesAvailable()) {
				haveGooglePlayServices();
			}
			prepareUI();
			break;
		case R.id.btn_sync_off:
			credential.setSelectedAccountName(null);
			prefs.setAccountName(null);
			prepareUI();
		default:
			break;
		}
	}

	@Override
	public void refreshView() {
	}

	private void prepareUI() {
		updateBatteryInfo();
		String account_name = prefs.getAccountName();
		TextView sync_status = (TextView) findViewById(R.id.txt_sync_status);
		if (account_name == null) {
			findViewById(R.id.btn_sync_off).setVisibility(View.GONE);
			sync_status.setText(R.string.txt_sync_status);
		} else {
			findViewById(R.id.btn_sync_off).setVisibility(View.VISIBLE);
			sync_status.setText(R.string.txt_sync_configured);
			sync_status.append(" " + account_name);
		}
	}

	private void updateBatteryInfo() {
		IntentFilter battery_filter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		Intent intent = registerReceiver(null, battery_filter);

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

	/** Check that Google Play services APK is installed and up to date. */
	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	private void haveGooglePlayServices() {
		// check if there is already an account selected
		if (credential.getSelectedAccountName() == null) {
			// ask user to choose account
			chooseAccount();
		} else {
			// load calendars
			startActivity(new Intent(this, CalendarListActivity.class));
		}
	}

	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(),
				REQUEST_ACCOUNT_PICKER);
	}

}
