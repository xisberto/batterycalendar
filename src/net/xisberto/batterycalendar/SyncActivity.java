package net.xisberto.batterycalendar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;

public abstract class SyncActivity extends Activity {
	public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

	public static final int REQUEST_AUTHORIZATION = 1;

	public static final int REQUEST_ACCOUNT_PICKER = 2;
	
	public static final String TAG = "BatteryCalendar";

	protected Preferences prefs;

	public com.google.api.services.calendar.Calendar client;

	private HttpTransport transport = AndroidHttp.newCompatibleTransport();

	private JsonFactory jsonFactory = new GsonFactory();

	protected GoogleAccountCredential credential;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = new Preferences(this);
		
		credential = GoogleAccountCredential.usingOAuth2(this,
				CalendarScopes.CALENDAR);
		credential.setSelectedAccountName(prefs.getAccountName());
		
		// Calendar client
		client = new com.google.api.services.calendar.Calendar.Builder(
				transport, jsonFactory, credential).setApplicationName(
				"BatteryCalendar/1.0").build();
	}

	public void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, SyncActivity.this,
						REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}
	
	public abstract void refreshView();
}
