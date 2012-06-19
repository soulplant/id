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
      if (y > 1) {
        throw new IllegalStateException();
      } else if (y == 1) {
        insertLine(0, "");
      }
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

  public boolean isDogEared() {
    return patchwork.isDogEared();
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

  public String getBaseFilename() {
    String[] parts = filename.split("/");
    return parts[parts.length - 1];
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
    return patchwork.hasUndo();
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
    if (patchwork.isModified()) {
      patchwork.onSaved();
    } else {
      if (patchwork.isDogEared()) {
        patchwork.clearDogEar();
      } else {
        patchwork.dogEar();
      }
    }
  }

  public boolean isMarkersClear() {
    return graveyard.isAllGravesEmpty() && graveyard.isAllStatusNormal();
  }

  public void undoLine(int y) {
    Grave grave = getGrave(y);
    if (grave.isEmpty()) {
      if (y != -1) {
        // Nothing to do but change the line back to what it was.
        revertStatus(y);
      }
      if (y == 0) {
        undoLine(-1);
      }
      return;
    }
    Tombstone tombstone = grave.getFreshestTombstone();
    insertLine(y + 1, tombstone.getCurrent());
    undoLine(y + 1);
    undoLine(y);
  }

  private void revertStatus(int y) {
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
  }

  public void dumpGraveyard() {
    System.out.println(graveyard);
  }

  public void wipe(int y) {
    graveyard.resetRange(y, y);
  }

  public List<String> getLineRange(int startY, int endY) {
    List<String> result = new ArrayList<String>();
    for (int i = startY; i <= endY; i++) {
      result.add(getLine(i));
    }
    return result;
  }

  public FileView makeView(int start, int end) {
    return new FileView(this, start, end);
  }

  public void setModified() {
    patchwork.setModified();
  }
}
