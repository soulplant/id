package com.id.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

  @Test
  public void isModified() {
    patchwork.startPatchAt(new Point(0, 0));
    assertFalse(patchwork.isModified());
    patchwork.onLineInserted(0, "hi");
    assertTrue(patchwork.isModified());
  }

  @Test
  public void dontNotifyOfModificationForStartPatch() {
    patchwork.setListener(listener);
    patchwork.startPatchAt(new Point(0, 0));
    verify(listener, never()).onModifiedStateChanged();
  }

  @Test
  public void notifyOfModificationForFileChange() {
    patchwork.setListener(listener);
    patchwork.startPatchAt(new Point(0, 0));
    patchwork.onLineInserted(0, "hi");
    verify(listener).onModifiedStateChanged();
  }
}
