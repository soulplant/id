package com.id.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
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
  public void stateIsSavedOnSaved() {
    patch();
    assertTrue(patchwork.isModified());
    patchwork.onSaved();
    assertFalse(patchwork.isModified());
  }

  @Test
  public void setModified() {
    assertFalse(patchwork.isModified());
    patchwork.setModified();
    assertTrue(patchwork.isModified());
  }
}
