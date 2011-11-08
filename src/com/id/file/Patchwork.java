package com.id.file;

import java.util.Stack;

import com.id.editor.Point;

public class Patchwork implements File.Listener {
  final Stack<Patch> pastPatches = new Stack<Patch>();
  final Stack<Patch> futurePatches = new Stack<Patch>();
  Patch currentPatch = null;
  int unmodifiedAtDepth = 0;

  public Patchwork() {}

  public void startPatchAt(Point point) {
    if (currentPatch != null) {
      throw new IllegalStateException("Attempt to overwrite partial patch.");
    }
    currentPatch = new Patch(point);
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
  }

  public void undo(File file) {
    if (inPatch()) {
      throw new IllegalStateException("discarding info in undo");
    }
    if (pastPatches.isEmpty()) {
      return;
    }
    Patch patch = pastPatches.pop();
    futurePatches.push(patch);
    patch.applyInverse(file);
  }

  public void redo(File file) {
    if (inPatch()) {
      throw new IllegalStateException("discarding info in redo");
    }
    if (futurePatches.isEmpty()) {
      return;
    }
    Patch patch = futurePatches.pop();
    pastPatches.push(patch);
    patch.apply(file);
  }

  @Override
  public void onLineInserted(int y, String line) {
    if (currentPatch != null) {
      currentPatch.onLineInserted(y, line);
    }
  }

  @Override
  public void onLineRemoved(int y, String line) {
    if (currentPatch != null) {
      currentPatch.onLineRemoved(y, line);
    }
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    if (currentPatch != null) {
      currentPatch.onLineChanged(y, oldLine, newLine);
    }
  }

}
