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
import java.util.Optional;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<String> attendees = new ArrayList<String>(request.getAttendees());
    long longDuration = request.getDuration();
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();

    // Deal with special cases:
    // Meeting is too long, impossible to ever hold it
    if (longDuration > TimeRange.WHOLE_DAY.duration()) {
      return possibleTimes;
    }
    // No events/no attendees, hold meeting anytime
    if (events.size() == 0 || attendees.size() == 0) {
      possibleTimes.add(TimeRange.fromStartDuration(TimeRange.START_OF_DAY, TimeRange.WHOLE_DAY.duration()));
      return possibleTimes;
    }
 
    // Since we know longDuration is less than one day, it can fit in an int
    int duration = (int)longDuration;

    // Filter events so that it only includes events that have one or more
    // attendees who are coming to this event
    ArrayList<Event> eventsByStart = new ArrayList<Event>();
    for (Event event : events) {
      HashSet<String> eventAttendees = new HashSet<String>(event.getAttendees());
      HashSet<String> meetingAttendees = new HashSet<String>(attendees);
      eventAttendees.retainAll(meetingAttendees); // set intersection
      if (eventAttendees.size() > 0) {
        eventsByStart.add(event);
      }
    }
    Collections.sort(eventsByStart, Event.ORDER_BY_START_TIME);   

    // Check again to see if there are any events
    if (eventsByStart.size() == 0 ) {
      possibleTimes.add(TimeRange.fromStartDuration(TimeRange.START_OF_DAY, TimeRange.WHOLE_DAY.duration()));
      return possibleTimes;
    }

    // Check if we can have a meeting before the earliest-starting event
    int earliestStart = eventsByStart.get(0).getWhen().start();
    if (TimeRange.START_OF_DAY < earliestStart && earliestStart - TimeRange.START_OF_DAY >= duration) {
      possibleTimes.add(TimeRange.fromStartEnd(
          TimeRange.START_OF_DAY, earliestStart, false));
    }

    // Check in between events - we know we can't have anything until
    // the earliest-starting event ends, so find the earliest-starting event
    // after that end time and see if the meeting fits between them
    Event currentEarliestEvent = eventsByStart.get(0);
    Event nextEvent;
    while (true) {
      // Try to get event
      Optional<Event> potentialNextEvent = getNextEarliestEvent(
          currentEarliestEvent.getWhen().end(), eventsByStart, 0, eventsByStart.size()-1);

      if (!potentialNextEvent.isPresent()) { 
        break; // No event starts after current one ends
      }

      // Check if we can fit the meeting between the two events
      // ***BUG***
      nextEvent = potentialNextEvent.get();
      int nextEventStart = nextEvent.getWhen().start();
      int currentEventEnd = currentEarliestEvent.getWhen().end();
      if (nextEventStart - currentEventEnd >= duration) {
        possibleTimes.add(TimeRange.fromStartEnd(
            currentEventEnd, nextEventStart, false));
      }
      currentEarliestEvent = nextEvent;
    }


    // Lastly, check if we can have a meeting before the latest-ending event
    ArrayList<Event> eventsByEnd = new ArrayList<Event>(eventsByStart);
    Collections.sort(eventsByEnd, Event.ORDER_BY_END_TIME);
    int latestEventEnd = eventsByEnd.get(eventsByEnd.size()-1).getWhen().end();
    if (TimeRange.END_OF_DAY - latestEventEnd >= duration) {
      possibleTimes.add(TimeRange.fromStartEnd(
        latestEventEnd, TimeRange.END_OF_DAY, true));
    }

    return possibleTimes;
  }

  // Parameters: int endTime, representing a time in minutes 
  //     List<Event>, a list of events which must be already sorted by start time
  //     ints low and high, marking off the section of list of events to be searched
  // Returns: the earliest-starting event that starts at or after endTime,
  // or an empty optional if there is no such event listed
  private Optional<Event> getNextEarliestEvent(int endTime, 
      List<Event> events, int low, int high) {
    
    // Base cases
    if (events.get(low).getWhen().start() >= endTime) {
      // The first event is the earliest, so if it works, we're done
      return Optional.of(events.get(low));
    }
    if (low == high || events.get(high).getWhen().start() < endTime) {
      // If low == high, only one event (which we know doesn't work
      // or we'd have returned already); if latest-starting event 
      // doesn't work, we know none will
      return Optional.empty();
    }

    // Neither of our base cases conditions are met, 
    // so binary search the list recursively
    int mid = (low + high) / 2;
    if (events.get(mid).getWhen().start() < endTime) {
      return getNextEarliestEvent(endTime, events, mid+1, high);
    } else {
      return getNextEarliestEvent(endTime, events, low, mid);
    }
  }
}
