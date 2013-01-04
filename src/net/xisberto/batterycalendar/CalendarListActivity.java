package net.xisberto.batterycalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.samples.calendar.android.AsyncInsertCalendar;
import com.google.api.services.samples.calendar.android.AsyncLoadCalendars;
import com.google.api.services.samples.calendar.android.CalendarInfo;

public class CalendarListActivity extends SyncCalendarActivity implements android.view.View.OnClickListener, OnItemClickListener {

	private ArrayAdapter<CalendarInfo> adapter;
	private ListView list;
	private String selected_id = null;
	public static final int REQUEST_DEVICE_NAME = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendarlist);

		list = (ListView) findViewById(R.id.list);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		
		prepareUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_calendarlist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_new_calendar:
			final EditText edit_text = new EditText(this);
			AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.btn_new_calendar)
				.setView(edit_text)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						createCalendar(edit_text.getText().toString());
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create();
			dialog.show();
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public void refreshView() {
		Preferences prefs = new Preferences(getApplicationContext());
		final String saved_id = prefs.getCalendarId();
		adapter = new ArrayAdapter<CalendarInfo>(this,
				android.R.layout.simple_list_item_single_choice,
				model.toSortedArray()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// by default it uses toString; override to use summary instead
				TextView view = (TextView) super.getView(position, convertView,
						parent);
				CalendarInfo calendarInfo = getItem(position);
				view.setText(calendarInfo.summary);
				Log.i(TAG, calendarInfo.id);
				if (calendarInfo.id.equals(saved_id)) {
					list.setItemChecked(position, true);
					selected_id = calendarInfo.id;
					Log.i(TAG, " is selected");
				}
				return view;
			}
		};
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setVisibility(View.VISIBLE);
		findViewById(R.id.progress).setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_DEVICE_NAME) {
			if (resultCode == Activity.RESULT_OK) {
				prefs.setDeviceName(data.getStringExtra(DeviceNameActivity.EXTRA_DEVICE_NAME));
			} else {
				prefs.setDeviceName(android.os.Build.MODEL);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CalendarInfo calendar = (CalendarInfo) parent.getAdapter().getItem(position);
		selected_id = calendar.id;
		Toast.makeText(this, calendar.id, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_ok:
			if (selected_id != null) {
				Preferences prefs = new Preferences(getApplicationContext());
				prefs.setCalendarId(selected_id);
				startActivityForResult(new Intent(this, DeviceNameActivity.class), REQUEST_DEVICE_NAME);
				finish();
			} else {
				finish();
			}
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	private void prepareUI() {
		list.setVisibility(View.INVISIBLE);
		list.setOnItemClickListener(this);
		
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		
		AsyncLoadCalendars.run(this);
	}
	
	protected void createCalendar(String string) {
		Calendar calendar = new Calendar();
		calendar.setSummary(string);
		calendar.setTimeZone(java.util.Calendar.getInstance().getTimeZone().getID());
		new AsyncInsertCalendar(this, calendar).execute();
		prepareUI();
	}
	
}
