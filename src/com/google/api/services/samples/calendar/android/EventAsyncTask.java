package com.google.api.services.samples.calendar.android;

import net.xisberto.batterycalendar.SyncEventActivity;

abstract class EventAsyncTask extends MyAsyncTask {
	final EventModel model;
	final com.google.api.services.calendar.Calendar client;

	EventAsyncTask(SyncEventActivity activity) {
		super(activity);
		model = activity.model;
		client = activity.client;
	}

}
