package com.id.file;

import java.util.Stack;

import com.id.editor.Point;

public class Patchwork implements File.Listener {
  final Stack<Patch> pastPatches = new Stack<Patch>();
  final Stack<Patch> futurePatches = new Stack<Patch>();
  Patch currentPatch = null;
  int unmodifiedAtDepth = 0;
  private boolean wasModified = false;
  private ModifiedListener listener;

  public Patchwork() {}

  private void notifyListenersOfModification() {
    if (isModified() != wasModified) {
      wasModified = isModified();
      if (listener != null) {
        listener.onModifiedStateChanged();
      }
    }
  }

  public void startPatchAt(Point point) {
    if (currentPatch != null) {
      throw new IllegalStateException("Attempt to overwrite partial patch.");
    }
    currentPatch = new Patch(point);
    notifyListenersOfModification();
  }

  public void breakPatch() {
    if (currentPatch == null) {
      return;
    }
    if (currentPatch.isEmpty()) {
      currentPatch = null;
      return;
    }
    pastPatches.add(currentPatch);
    if (futurePatches.size() > 0 && pastPatches.size() < unmodifiedAtDepth) {
      // We've lost the point we came from (modified the file from an earlier
      // point in history).
      unmodifiedAtDepth = -1;
    }
    futurePatches.clear();
    currentPatch = null;
    notifyListenersOfModification();
  }

  public boolean inPatch() {
    return currentPatch != null;
  }

  public boolean isModified() {
    if (currentPatch != null) {
      return !currentPatch.isEmpty();
    }
    return pastPatches.size() != unmodifiedAtDepth;
  }

  public void onSaved() {
    unmodifiedAtDepth = pastPatches.size();
    notifyListenersOfModification();
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
    notifyListenersOfModification();
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
    notifyListenersOfModification();
    return patch.getPosition();
  }

  @Override
  public void onLineInserted(int y, String line) {
    if (currentPatch != null) {
      currentPatch.onLineInserted(y, line);
      notifyListenersOfModification();
    }
  }

  @Override
  public void onLineRemoved(int y, String line) {
    if (currentPatch != null) {
      currentPatch.onLineRemoved(y, line);
      notifyListenersOfModification();
    }
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    if (currentPatch != null) {
      currentPatch.onLineChanged(y, oldLine, newLine);
      notifyListenersOfModification();
    }
  }

  public void reset() {
    futurePatches.clear();
    pastPatches.clear();
    currentPatch = null;
    unmodifiedAtDepth = 0;
  }

  public void setListener(ModifiedListener listener) {
    this.listener = listener;
  }
}
