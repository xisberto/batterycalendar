package com.google.api.services.samples.calendar.android;

import java.io.IOException;

import net.xisberto.batterycalendar.SyncEventActivity;

import com.google.api.services.calendar.model.Event;

public class AsyncInsertEvent extends EventAsyncTask {
	private final Event entry;
	private final String calendarId;

	public AsyncInsertEvent(SyncEventActivity activity, String calendarId, Event entry) {
		super(activity);
		this.entry = entry;
		this.calendarId = calendarId;
	}

	@Override
	protected void doInBackground() throws IOException {
		Event event = client.events().insert(calendarId, entry).execute();
		model.add(event);
	}

}
