package net.xisberto.batterycalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
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

public class CalendarListActivity extends SyncActivity implements OnItemClickListener {

	private ArrayAdapter<CalendarInfo> adapter;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendarlist);

		list = (ListView) findViewById(R.id.list);
		
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
				return view;
			}
		};
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setVisibility(View.VISIBLE);
		findViewById(R.id.progress).setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CalendarInfo calendar = (CalendarInfo) parent.getAdapter().getItem(position);
		Toast.makeText(this, calendar.id, Toast.LENGTH_SHORT).show();
		//Preferences prefs = new Preferences(getApplicationContext());
		//prefs.setCalendarId(calendar.id);
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
		new AsyncInsertCalendar(this, calendar).execute();
		prepareUI();
	}
	
}
