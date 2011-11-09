package com.id.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.editor.Point;

public class File {
  public interface Listener {
    void onLineInserted(int y, String line);
    void onLineRemoved(int y, String line);
    void onLineChanged(int y, String oldLine, String newLine);
  }

  private final List<String> lines = new ArrayList<String>();
  private final List<Listener> listeners = new ArrayList<Listener>();
  private final Patchwork patchwork;
  private final Graveyard graveyard;

  public File() {
    this.patchwork = new Patchwork();
    this.graveyard = new Graveyard(Arrays.<String>asList());
    listeners.add(patchwork);
    listeners.add(graveyard);
  }

  public File(String... lines) {
    this();
    for (String line : lines) {
      insertLine(getLineCount(), line);
    }
    graveyard.reset();
    patchwork.reset();
  }

  // Lines
  public String getLine(int y) {
    return lines.get(y);
  }

  public int getLineCount() {
    return lines.size();
  }

  public void insertLine(int y, String line) {
    lines.add(y, line);
    fireLineInserted(y, line);
  }

  public void removeLine(int y) {
    String line = lines.remove(y);
    fireLineRemoved(y, line);
  }

  public void changeLine(int y, String line) {
    String oldLine = lines.set(y, line);
    fireLineChanged(y, oldLine, line);
  }

  private void fireLineInserted(int y, String line) {
    for (Listener l : listeners) {
      l.onLineInserted(y, line);
    }
  }

  private void fireLineRemoved(int y, String line) {
    for (Listener l : listeners) {
      l.onLineRemoved(y, line);
    }
  }

  private void fireLineChanged(int y, String oldLine, String newLine) {
    for (Listener l : listeners) {
      l.onLineChanged(y, oldLine, newLine);
    }
  }

  // Patches
  public void startPatchAt(Point point) {
    patchwork.startPatchAt(point);
  }

  public void breakPatch() {
    patchwork.breakPatch();
  }

  public boolean isInPatch() {
    return patchwork.inPatch();
  }

  public boolean isModified() {
    return patchwork.isModified();
  }

  public Point undo() {
    return patchwork.undo(this);
  }

  public Point redo() {
    return patchwork.redo(this);
  }

  public Tombstone.Status getStatus(int y) {
    return graveyard.getStatus(y);
  }

  public void startPatchAt(int y, int x) {
    patchwork.startPatchAt(new Point(y, x));
  }

  public void insertText(int y, int x, String text) {
    String line = getLine(y);
    String newLine = line.substring(0, x) + text + line.substring(x);
    changeLine(y, newLine);
  }

  public void removeText(int y, int x, int length) {
    String line = getLine(y);
    int substringMax = Math.min(line.length(), x + length);
    String newLine = line.substring(0, x - 1) + line.substring(substringMax);
    changeLine(y, newLine);
  }

  public void addListener(Listener listener) {
    this.listeners.add(listener);
  }
}
