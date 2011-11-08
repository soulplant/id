package com.id.editor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Cursor;
import com.id.editor.Point;
import com.id.editor.Range;

public class RangeTest {
  private Cursor cursor;
  private Range range;
  @Before
  public void setup() {
    cursor = new Cursor();
    range = new Range(cursor);
  }

  @Test
  public void rangeContainsCursorPoint() {
    range.toggleMode(Range.Mode.LINE);
    assertEquals(true, range.contains(cursor.getPoint()));
  }

  @Test
  public void lineContains() {
    range.toggleMode(Range.Mode.LINE);
    assertTrue(range.contains(new Point(0, 0)));
    assertTrue(range.contains(new Point(0, 100)));
    assertFalse(range.contains(new Point(1, 0)));

    cursor.moveTo(new Point(5, 0));
    assertTrue(range.contains(new Point(5, 0)));
    assertFalse(range.contains(new Point(6, 0)));
  }
}
