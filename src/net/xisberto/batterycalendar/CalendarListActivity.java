package net.xisberto.batterycalendar;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.services.samples.calendar.android.AsyncLoadCalendars;
import com.google.api.services.samples.calendar.android.CalendarInfo;

public class CalendarListActivity extends SyncActivity {


	private ArrayAdapter<CalendarInfo> adapter;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_calendarlist);
		
		list = (ListView) findViewById(R.id.list);
		list.setVisibility(View.INVISIBLE);
		
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		
		AsyncLoadCalendars.run(this);
	}

	@Override
	public void refreshView() {
		adapter = new ArrayAdapter<CalendarInfo>(this,
				android.R.layout.simple_list_item_1,
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
		list.setVisibility(View.VISIBLE);
		findViewById(R.id.progress).setVisibility(View.GONE);
	}

}
