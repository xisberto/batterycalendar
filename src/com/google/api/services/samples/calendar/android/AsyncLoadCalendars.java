/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.calendar.android;

import java.io.IOException;

import net.xisberto.batterycalendar.InformationActivity;

import com.google.api.services.calendar.model.CalendarList;

/**
 * Asynchronously load the calendars.
 *
 * @author Yaniv Inbar
 */
public class AsyncLoadCalendars extends CalendarAsyncTask {

  AsyncLoadCalendars(InformationActivity informationActivity) {
    super(informationActivity);
  }

  @Override
  protected void doInBackground() throws IOException {
    CalendarList feed = client.calendarList().list().setFields(CalendarInfo.FEED_FIELDS).execute();
    model.reset(feed.getItems());
  }

  public static void run(InformationActivity informationActivity) {
    new AsyncLoadCalendars(informationActivity).execute();
  }
}
