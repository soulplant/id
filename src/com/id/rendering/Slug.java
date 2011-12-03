package com.id.rendering;

import java.util.ArrayList;
import java.util.List;

public class Slug {
  private class Entry {
    public char letter;
    public boolean isVisual = false;
    public boolean isHighlight = false;
//    public boolean isCursor = false;
//    public boolean isWideCursor = false;

    public Entry(char letter) {
      this.letter = letter;
    }
  }

  private final List<Entry> entries = new ArrayList<Entry>();

  public Slug(int width) {
    for (int i = 0; i < width; i++) {
      entries.add(new Entry(' '));
    }
  }

  public void setLetter(int i, char letter) {
    entries.get(i).letter = letter;
  }

  public void setVisual(int i, boolean visual) {
    entries.get(i).isVisual = visual;
  }

  public char getLetter(int i) {
    return entries.get(i).letter;
  }

  public boolean isVisual(int i) {
    return entries.get(i).isVisual;
  }

  public String getString() {
    StringBuffer buffer = new StringBuffer();
    for (Entry entry : entries) {
      buffer.append(entry.letter);
    }
    return buffer.toString();
  }

  @Override
  public String toString() {
    return getString();
  }

  public int getLength() {
    return entries.size();
  }

  public void setHighlight(int i, boolean highlight) {
    entries.get(i).isHighlight = highlight;
  }

  public boolean isHighlight(int i) {
    return entries.get(i).isHighlight;
  }
}
