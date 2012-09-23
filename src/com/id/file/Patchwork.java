package com.id.file;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.id.editor.Point;

public class Patchwork implements File.Listener {
  private final Stack<Patch> pastPatches = new Stack<Patch>();
  private final Stack<Patch> futurePatches = new Stack<Patch>();
  private Patch currentPatch = null;
  private int savedAtDepth = 0;
  private ModifiedListener listener;

  public Patchwork() {}

  private void stateChanged() {
    if (listener != null) {
      listener.onModifiedStateChanged();
    }
  }

  public void startPatchAt(Point point) {
    if (currentPatch != null) {
      throw new IllegalStateException("Attempt to overwrite partial patch.");
    }
    currentPatch = new Patch(point);
    stateChanged();
  }

  public void breakPatch() {
    if (currentPatch == null) {
      return;
    }
    if (currentPatch.isEmpty()) {
      currentPatch = null;
      return;
    }
    if (!futurePatches.isEmpty()) {
      clearFuturePatches();
    }
    pastPatches.add(currentPatch);
    currentPatch = null;
    stateChanged();
  }

  private void clearFuturePatches() {
    if (pastPatches.size() < savedAtDepth) {
      // We've lost the point we came from (modified the file from a point
      // earlier than when it was last saved at).
      savedAtDepth = -1;
    }
    futurePatches.clear();
  }

  public boolean inPatch() {
    return currentPatch != null;
  }

  public void onSaved() {
    savedAtDepth = pastPatches.size();
    stateChanged();
  }


  public boolean isModified() {
    return isInMiddleOfPatch() || !isAtSavedDepth();
  }

  private boolean isInMiddleOfPatch() {
    return currentPatch != null && !currentPatch.isEmpty();
  }

  private boolean isAtSavedDepth() {
    return pastPatches.size() == savedAtDepth;
  }

  public boolean isSaved() {
    return !isModified();
  }

  public boolean hasUndo() {
    return !pastPatches.isEmpty();
  }

  public Point undo(File file) {
    if (inPatch()) {
      throw new IllegalStateException("discarding info in undo");
    }
    if (pastPatches.isEmpty()) {
      return null;
    }
    Patch patch = pastPatches.pop();
    futurePatches.push(patch);
    patch.applyInverse(file);
    stateChanged();
    return patch.getPosition();
  }

  public Point redo(File file) {
    if (inPatch()) {
      throw new IllegalStateException("discarding info in redo");
    }
    if (futurePatches.isEmpty()) {
      return null;
    }
    Patch patch = futurePatches.pop();
    pastPatches.push(patch);
    patch.apply(file);
    stateChanged();
    return patch.getPosition();
  }

  @Override
  public void onLineInserted(int y, String line) {
    if (currentPatch != null) {
      currentPatch.onLineInserted(y, line);
      stateChanged();
    }
  }

  @Override
  public void onLineRemoved(int y, String line) {
    if (currentPatch != null) {
      currentPatch.onLineRemoved(y, line);
      stateChanged();
    }
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    if (currentPatch != null) {
      currentPatch.onLineChanged(y, oldLine, newLine);
      stateChanged();
    }
  }

  public void reset() {
    futurePatches.clear();
    pastPatches.clear();
    currentPatch = null;
    savedAtDepth = 0;
  }

  public void setModified() {
    savedAtDepth = -1;
    stateChanged();
  }

  public void setListener(ModifiedListener listener) {
    this.listener = listener;
  }
}
