package com.id.file;


public class Pair {
  private final Tombstone tombstone;
  private final Grave grave;

  public Pair(Tombstone tombstone, Grave grave) {
    this.tombstone = tombstone;
    this.grave = grave;
  }

  public Tombstone getTombstone() {
    return tombstone;
  }

  public Grave getGrave() {
    return grave;
  }
}
