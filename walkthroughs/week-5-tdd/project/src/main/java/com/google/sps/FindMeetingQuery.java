// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<String> attendees = new ArrayList<String>(request.getAttendees());
    long longDuration = request.getDuration();
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();

    // Check if meeting can fit within a day (otherwise impossible to hold it)
    if (longDuration > TimeRange.WHOLE_DAY.duration()) {
      return possibleTimes;
    }
    // No attendees, hold meeting anytime
    if (attendees.isEmpty()) {
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }
 
    // Since we know longDuration is less than one day, it can fit in an int
    int duration = (int)longDuration;

    // Filter events so that it only includes events that have one or more
    // attendees who are coming to this event
    List<Event> eventsByStart = new ArrayList<>(getRelevantEvents(attendees, events));
    Collections.sort(eventsByStart, Event.ORDER_BY_START_TIME);   

    // If there are no events including our meeting attendees, can hold anytime
    if (eventsByStart.isEmpty() ) {
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    // Check if we can have a meeting before the earliest-starting event
    int earliestStart = eventsByStart.get(0).getWhen().start();
    if (TimeRange.START_OF_DAY < earliestStart && 
      earliestStart - TimeRange.START_OF_DAY >= duration) {
      possibleTimes.add(TimeRange.fromStartEnd(
          TimeRange.START_OF_DAY, earliestStart, false));
    }

    // Check in between events
    Event currentEvent = eventsByStart.get(0);
    int latestEnd = currentEvent.getWhen().end();

    for (int i = 1; i < eventsByStart.size(); i++) {
      Event nextEvent = eventsByStart.get(i);
      int nextEventEnd = nextEvent.getWhen().end();
      int nextEventStart = nextEvent.getWhen().start();
      int currentEventEnd = currentEvent.getWhen().end();

      if (nextEventEnd < latestEnd) {
        continue; //there is still another event ongoing
      }
      latestEnd = nextEventEnd;

      if (nextEventStart - currentEventEnd >= duration) {
        possibleTimes.add(TimeRange.fromStartEnd(
            currentEventEnd, nextEventStart, false));
      }
      currentEvent = nextEvent;
    }


    // Lastly, see if we can have a meeting between the last event end and the end of the day
    if (TimeRange.END_OF_DAY - latestEnd >= duration) {
      possibleTimes.add(TimeRange.fromStartEnd(
        latestEnd, TimeRange.END_OF_DAY, true));
    }

    return possibleTimes;
  }

  // Given a list of events and a list of attendees, return a filtered list
  // of events such that all events have at least one attendee from attendees
  private Collection<Event> getRelevantEvents(Collection<String> attendees, Collection<Event> events) {
    ArrayList<Event> filteredEvents = new ArrayList<Event>();
    for (Event event : events) {
      HashSet<String> eventAttendees = new HashSet<String>(event.getAttendees());
      HashSet<String> meetingAttendees = new HashSet<String>(attendees);
      eventAttendees.retainAll(meetingAttendees); // set intersection
      if (!eventAttendees.isEmpty()) {
        filteredEvents.add(event);
      }
    }
    return filteredEvents;
  }
} 
