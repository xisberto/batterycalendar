package com.google.api.services.samples.calendar.android;

import java.io.IOException;

import net.xisberto.batterycalendar.SyncActivity;
import net.xisberto.batterycalendar.SyncEventActivity;

import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.Event;

public class AsyncUpdateEvent extends EventAsyncTask {
	private final String calendarId;
	private final Event event;

	public AsyncUpdateEvent(SyncEventActivity eventActivity, String calendarId,
			Event entry) {
		super(eventActivity);
		this.calendarId = calendarId;
		this.event = entry;
	}

	@Override
	protected void doInBackground() throws IOException {
		try {
			Log.i(SyncActivity.TAG,	"Starting background for event " + event.getId());
			Log.i(SyncActivity.TAG, " - summary "	+ event.getSummary());
			Log.i(SyncActivity.TAG, " - start "	+ event.getStart());
			Log.i(SyncActivity.TAG, " - end " + event.getEnd());
			Event updatedEvent = client.events()
					.patch(calendarId, event.getId(), event)
					.setFields(EventInfo.FIELDS).execute();
			model.add(updatedEvent);
		} catch (GoogleJsonResponseException e) {
			// 404 Not Found would happen if user tries to delete an already
			// deleted calendar
			if (e.getStatusCode() != 404) {
				throw e;
			}
			model.remove(event.getId());
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		// When updating, there's nothing to do in main thread
		super.onPostExecute(false);
	}
}
