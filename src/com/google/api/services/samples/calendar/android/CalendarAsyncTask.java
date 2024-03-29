package com.google.api.services.samples.calendar.android;

import net.xisberto.batterycalendar.SyncCalendarActivity;

abstract class CalendarAsyncTask extends MyAsyncTask {
	final CalendarModel model;
	final com.google.api.services.calendar.Calendar client;

	CalendarAsyncTask(SyncCalendarActivity activity) {
		super(activity);
		model = activity.model;
		client = activity.client;
	}

}
