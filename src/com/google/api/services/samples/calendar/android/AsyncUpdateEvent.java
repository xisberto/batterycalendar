package com.google.api.services.samples.calendar.android;

import java.io.IOException;

import net.xisberto.batterycalendar.SyncEventActivity;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.Event;

public class AsyncUpdateEvent extends EventAsyncTask {
	private final String calendarId;
	private final String eventId;
	private final Event entry;

	public AsyncUpdateEvent(SyncEventActivity eventActivity, String calendarId, String eventId, Event entry) {
		super(eventActivity);
		this.calendarId = calendarId;
		this.eventId = eventId;
		this.entry = entry;
	}

	@Override
	protected void doInBackground() throws IOException {
		try {
			Event updatedEvent = client.events().patch(calendarId, eventId, entry)
					.setFields(EventInfo.UPDATE_FIELDS).execute();
			model.add(updatedEvent);
		} catch (GoogleJsonResponseException e) {
			// 404 Not Found would happen if user tries to delete an already
			// deleted calendar
			if (e.getStatusCode() != 404) {
				throw e;
			}
			model.remove(eventId);
		}
	}
}
