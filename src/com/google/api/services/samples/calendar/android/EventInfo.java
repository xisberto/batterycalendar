package com.google.api.services.samples.calendar.android;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

public class EventInfo implements Comparable<EventInfo>, Cloneable {

	public static final String FIELDS = "id,summary,description,start,end";
	public static final String FEED_FIELDS = "items(" + FIELDS + ")";

	public String id, summary, description;
	public DateTime start, end;

	public EventInfo(String id, String summary, String description,
			String start, String end) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.start = new DateTime(start);
		this.end = new DateTime(end);
	}

	public EventInfo(String id, String summary, String description,
			DateTime start, DateTime end) {
		super();
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.start = start;
		this.end = end;
	}

	public EventInfo(Event event) {
		update(event);
	}

	@Override
	public int compareTo(EventInfo other) {
		String start_time = start.toStringRfc3339();
		String other_start_time = start.toStringRfc3339();
		return start_time.compareTo(other_start_time);
	}

	@Override
	public EventInfo clone() {
		try {
			return (EventInfo) super.clone();
		} catch (CloneNotSupportedException exception) {
			// should not happen
			throw new RuntimeException(exception);
		}
	}

	public void update(Event event) {
		id = event.getId();
		summary = event.getSummary();
		description = event.getDescription();
		start = event.getStart().getDateTime();
		end = event.getEnd().getDateTime();
	}

}
