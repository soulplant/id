package com.id.file;

import java.util.ArrayList;
import java.util.List;

public class Grave {
  private List<Tombstone> tombstones = new ArrayList<Tombstone>();
  private List<Integer> history = new ArrayList<Integer>();

  public Grave() {
  }

  public Grave(List<Tombstone> tombstones, List<Integer> history) {
    this.tombstones = tombstones;
    this.history = history;
  }

  @Override
  public String toString() {
    return "Grave" + tombstones;
  }

  public Pair split(String line) {
    if (isEmpty()) {
      return null;
    }
    Tombstone candidate = getFreshestTombstone();
    if (candidate.getCurrent().equals(line)) {
      return splitAt(getFreshestTombstoneIndex());
    }
    return null;
  }

  public static <T> List<T> splice(int n, List<T> list) {
    List<T> result = new ArrayList<T>();
    while (n < list.size()) {
      result.add(list.remove(n));
    }
    return result;
  }

  private Pair splitAt(int i) {
    List<Tombstone> splitTombstones = splice(i, tombstones);
    Tombstone tombstone = splitTombstones.remove(0);
    if (i != history.remove(history.size() - 1)) {
      throw new IllegalStateException("We should only split where the history tells us to");
    }
    List<Integer> splitHistory = splice(i, history);
    return new Pair(tombstone, new Grave(splitTombstones, splitHistory));
  }

  private int getFreshestTombstoneIndex() {
    return history.get(history.size() - 1);
  }

  public Tombstone getFreshestTombstone() {
    if (history.isEmpty()) {
      return null;
    }
    return tombstones.get(history.get(history.size() - 1));
  }

  public boolean isEmpty() {
    return tombstones.isEmpty();
  }

  public void inherit(Tombstone tombstone, Grave grave) {
    int index = tombstones.size();
    tombstones.add(tombstone);
    tombstones.addAll(grave.getTombstones());
    history.addAll(grave.getHistory());
    history.add(index);
  }

  private List<Integer> getHistory() {
    return history;
  }

  public List<Tombstone> getTombstones() {
    return new ArrayList<Tombstone>(tombstones);
  }

  public int size() {
    return tombstones.size();
  }

  public void clear() {
    tombstones.clear();
  }
}
