package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.id.editor.Point;

public class PatchworkTest {
  private Patchwork patchwork;
  private ModifiedListener listener;

  @Before
  public void setup() {
    patchwork = new Patchwork();
    listener = Mockito.mock(ModifiedListener.class);
  }

  private void patch() {
    patchwork.startPatchAt(new Point(0, 0));
    patchwork.onLineInserted(0, "hi");
    patchwork.breakPatch();
  }

  @Test
  public void isModified() {
    patchwork.startPatchAt(new Point(0, 0));
    assertFalse(patchwork.isModified());
    patchwork.onLineInserted(0, "hi");
    assertTrue(patchwork.isModified());
  }

  @Test
  public void notifyOfModificationForFileChange() {
    patchwork.setListener(listener);
    patchwork.startPatchAt(new Point(0, 0));
    patchwork.onLineInserted(0, "hi");
    verify(listener, atLeastOnce()).onModifiedStateChanged();
  }

  @Test
  public void startsSaved() {
    assertTrue(patchwork.isSaved());
  }

  @Test
  public void startsNotDogEared() {
    assertFalse(patchwork.isDogEared());
  }

  @Test
  public void notDogEaredIfInMiddleOfPatch() {
    patchwork.dogEar();
    assertTrue(patchwork.isDogEared());
    patchwork.startPatchAt(new Point(0, 0));
    assertTrue(patchwork.isDogEared());
    patchwork.onLineInserted(0, "hi");
    assertFalse(patchwork.isDogEared());
  }

  @Test
  public void dogEaringAtSavedPointShouldNotify() {
    assertTrue(patchwork.isSaved());
    assertFalse(patchwork.isDogEared());
    patchwork.setListener(listener);
    patchwork.dogEar();
    verify(listener, atLeastOnce()).onModifiedStateChanged();
    assertTrue(patchwork.isDogEared());
  }

  @Test
  public void undoingToDogEaredPatches() {
    patch();  // patch 1
    assertFalse(patchwork.isDogEared());
    patch();  // patch 2
    assertFalse(patchwork.isDogEared());

    patchwork.dogEar();
    assertTrue("patch 2 just got dog eared", patchwork.isDogEared());

    File file = Mockito.mock(File.class);
    patchwork.undo(file);
    assertFalse("patch 1 should not be dog eared", patchwork.isDogEared());
    patchwork.redo(file);
    assertTrue("patch 2 should be dog eared", patchwork.isDogEared());
  }

  @Test
  public void notifyOfModificationForDogEar() {
    patch();
    assertTrue(patchwork.isModified());
    patchwork.setListener(listener);
    patchwork.dogEar();
    verify(listener, atLeastOnce()).onModifiedStateChanged();
  }

  @Test
  public void stateIsSavedOnSaved() {
    patch();
    assertTrue(patchwork.isModified());
    patchwork.onSaved();
    assertFalse(patchwork.isModified());
  }

  @Test
  public void dogEarOverridesModified() {
    patch();
    patch();
    patchwork.onSaved();
    patchwork.dogEar();
    patchwork.undo(Mockito.mock(File.class));
    assertTrue(patchwork.isModified());
    patchwork.onSaved();
    patchwork.redo(Mockito.mock(File.class));
    assertTrue(patchwork.isDogEared());
  }

  @Test
  public void droppingUndoHistoryAlsoDropsDogEars() {
    patch();
    patchwork.onSaved();
    patchwork.dogEar();
    patchwork.undo(Mockito.mock(File.class));
    patch();
    assertFalse(patchwork.isDogEared());
  }

  @Test
  public void clearDogEar() {
    patch();
    patchwork.dogEar();
    assertTrue(patchwork.isDogEared());
    patchwork.clearDogEar();
    assertFalse(patchwork.isDogEared());
  }
}
