package com.id.perf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTracker {
  static Map<String, List<Integer>> times = new HashMap<String, List<Integer>>();
  static private int timesTracked = 0;

  public static void trackTime(String eventName, int duration) {
    if (!times.containsKey(eventName)) {
      times.put(eventName, new ArrayList<Integer>());
    }
    times.get(eventName).add(duration);
  }

  private static void dumpTrackedTimeAverages() {
    for (Map.Entry<String, List<Integer>> entry : times.entrySet()) {
      dumpAverage(entry.getKey(), entry.getValue());
    }
  }

  private static void dumpAverage(String eventName, List<Integer> values) {
    int total = 0;
    for (int value : values) {
      total += value;
    }
    int average = (int) (((float) total) / ((float) values.size()));
    System.out.println("average time for " + eventName + " event: " + average);
  }
}
