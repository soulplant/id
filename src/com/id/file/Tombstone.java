package com.id.file;

import java.util.ArrayList;
import java.util.List;


public class Tombstone {
  public enum Status {
    NORMAL,
    NEW,
    MODIFIED,
  }

  private String original;
  private String current;

  public Tombstone(String current, String original) {
    this.current = current;
    this.original = original;
  }

  @Override
  public String toString() {
    return "Tombstone['" + current + "', '" + original + "']";
  }

  public Status getStatus() {
    if (original == null) {
      return Status.NEW;
    } else if (current.equals(original)) {
      return Status.NORMAL;
    } else {
      return Status.MODIFIED;
    }
  }

  public String getOriginal() {
    return original;
  }

  public String getCurrent() {
    return current;
  }

  public void setCurrent(String current) {
    this.current = current;
  }

  public void reset() {
    original = current;
  }

  public static List<Tombstone> deletionsFromLines(List<String> lines) {
    List<Tombstone> result = new ArrayList<Tombstone>();
    for (String line : lines) {
      result.add(new Tombstone(line, line));
    }
    return result;
  }
}
