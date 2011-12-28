package com.id.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.editor.Point;
import com.id.git.FileDelta;
import com.id.platform.FileSystem;

public class File {
  public interface Listener {
    void onLineInserted(int y, String line);
    void onLineRemoved(int y, String line);
    void onLineChanged(int y, String oldLine, String newLine);
  }

  private final List<String> lines = new ArrayList<String>();
  private final List<Listener> listeners = new ArrayList<Listener>();
  private final List<ModifiedListener> modifiedListeners = new ArrayList<ModifiedListener>();
  private final Patchwork patchwork;
  private final Graveyard graveyard;
  private String filename;

  public File() {
    this.patchwork = new Patchwork();
    this.graveyard = new Graveyard(Arrays.<String>asList());
    listeners.add(patchwork);
    listeners.add(graveyard);
    patchwork.setListener(new ModifiedListener() {
      @Override
      public void onModifiedStateChanged() {
        fireModifiedStateChanged();
      }
    });
  }

  public File(String... lines) {
    this();
    for (String line : lines) {
      insertLine(getLineCount(), line);
    }
    reset();
  }

  public File(List<String> lines) {
    this();
    for (String line : lines) {
      insertLine(getLineCount(), line);
    }
    reset();
  }

  // Lines
  public String getLine(int y) {
    return lines.get(y);
  }

  public int getLineCount() {
    return lines.size();
  }

  public void insertLine(int y, String line) {
    if (isEmpty()) {
      y = 0;
    }
    lines.add(y, line);
    fireLineInserted(y, line);
  }

  public String removeLine(int y) {
    String line = lines.remove(y);
    fireLineRemoved(y, line);
    return line;
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

  private void fireModifiedStateChanged() {
    for (ModifiedListener l : modifiedListeners) {
      l.onModifiedStateChanged();
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
    if (isEmpty()) {
      insertLine(0, "");
    }
    String line = getLine(y);
    String newLine = line.substring(0, x) + text + line.substring(x);
    changeLine(y, newLine);
  }

  public boolean isEmpty() {
    return getLineCount() == 0;
  }

  public void addListener(Listener listener) {
    this.listeners.add(listener);
  }

  public void addModifiedListener(ModifiedListener listener) {
    this.modifiedListeners.add(listener);
  }

  public void removeModifiedListener(ModifiedListener listener) {
    this.modifiedListeners.remove(listener);
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public String[] getLines() {
    String[] result = new String[getLineCount()];
    return lines.toArray(result);
  }

  public boolean isLineNew(int y) {
    return graveyard.getStatus(y) == Tombstone.Status.NEW;
  }

  public Grave getGrave(int y) {
    return graveyard.getGrave(y);
  }

  public static File loadFrom(BufferedReader bufferedReader) throws IOException {
    List<String> lines = new ArrayList<String>();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      lines.add(line);
    }
    return new File(lines);
  }

  private void reset() {
    graveyard.reset();
    patchwork.reset();
  }

  public boolean hasUndo() {
    return !patchwork.pastPatches.isEmpty();
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public List<String> getLineList() {
    return new ArrayList<String>(lines);
  }

  public void setDiffMarkers(FileDelta delta) {
    graveyard.setDiffMarkers(delta);
  }

  public void save(FileSystem fileSystem) {
    fileSystem.save(this);
    patchwork.onSaved();
  }

  public boolean isMarkersClear() {
    return graveyard.isAllGravesEmpty() && graveyard.isAllStatusNormal();
  }

  public void undoLine(int y) {
    List<Tombstone> tombstones = getGrave(y).getTombstones();
    getGrave(y).clear();
    switch (getStatus(y)) {
    case NEW:
      removeLine(y);
      break;
    case MODIFIED:
      String originalLine = graveyard.getTombstone(y).getOriginal();
      changeLine(y, originalLine);
      break;
    case NORMAL:
      // Do nothing.
      break;
    }
    int i = 1;
    for (Tombstone tombstone : tombstones) {
      plantTombstone(y + i++, tombstone);
    }
  }

  private void plantTombstone(int y, Tombstone tombstone) {
    insertLine(y, tombstone.getOriginal());
    graveyard.getTombstone(y).reset();
  }

  public void dumpGraveyard() {
    System.out.println(graveyard);
  }
}
