package com.id.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.id.editor.Point;

public class PatchworkTest {

  @Test
  public void test() {
    Patchwork patchwork = new Patchwork();
    patchwork.startPatchAt(new Point(0, 0));
    assertFalse(patchwork.isModified());
    patchwork.onLineInserted(0, "hi");
    assertTrue(patchwork.isModified());
  }
}
