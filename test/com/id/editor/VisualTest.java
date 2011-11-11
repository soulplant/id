package com.id.editor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Cursor;
import com.id.editor.Point;
import com.id.editor.Visual;

public class VisualTest {
  private Cursor cursor;
  private Visual visual;
  @Before
  public void setup() {
    cursor = new Cursor();
    visual = new Visual(cursor);
  }

  @Test
  public void rangeContainsCursorPoint() {
    visual.toggleMode(Visual.Mode.LINE);
    assertEquals(true, visual.contains(cursor.getPoint()));
  }

  @Test
  public void lineContains() {
    visual.toggleMode(Visual.Mode.LINE);
    assertTrue(visual.contains(new Point(0, 0)));
    assertTrue(visual.contains(new Point(0, 100)));
    assertFalse(visual.contains(new Point(1, 0)));

    cursor.moveTo(5, 0);
    assertTrue(visual.contains(new Point(5, 0)));
    assertFalse(visual.contains(new Point(6, 0)));
  }

  @Test
  public void charContains() {
    visual.toggleMode(Visual.Mode.CHAR);
    assertTrue(visual.contains(new Point(0, 0)));
  }
}
