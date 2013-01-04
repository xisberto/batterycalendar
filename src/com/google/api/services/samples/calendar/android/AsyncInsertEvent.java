package com.google.api.services.samples.calendar.android;

import java.io.IOException;

import net.xisberto.batterycalendar.SyncEventActivity;

import com.google.api.services.calendar.model.Event;

public class AsyncInsertEvent extends EventAsyncTask {
	private final Event entry;
	private final String calendar_id;

	AsyncInsertEvent(SyncEventActivity activity, String calendar_id, Event entry) {
		super(activity);
		this.entry = entry;
		this.calendar_id = calendar_id;
	}

	@Override
	protected void doInBackground() throws IOException {
		Event event = client.events().insert(calendar_id, entry).execute();
		model.add(event);
	}

}
