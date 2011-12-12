package com.id.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.id.file.File;
import com.id.file.FileView;

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

  @Test
  public void removeFrom() {
    visual.toggleMode(Visual.Mode.CHAR);
    cursor.moveTo(0, 2);
    assertEquals(0, visual.getStartPoint().getX());
    assertEquals(2, visual.getEndPoint().getX());
    File file = new File("abcd");
    visual.removeFrom(new FileView(file));
    assertEquals("d", file.getLine(0));
  }

  @Test
  public void removeFromCharsOverLines() {
    cursor.moveTo(0, 1);
    visual.toggleMode(Visual.Mode.CHAR);
    cursor.moveTo(1, 2);
    File file = new File("abcd", "efgh", "hijk");
    visual.removeFrom(new FileView(file));
    assertEquals(2, file.getLineCount());
    assertEquals("ah", file.getLine(0));
  }

  @Test
  public void pullFromFileCharwise() {
    cursor.moveTo(0, 1);
    visual.toggleMode(Visual.Mode.CHAR);
    cursor.moveTo(1, 1);
    File file = new File("abc", "def");
    Register register = visual.getRegister(new FileView(file));
    assertEquals(2, register.getLineCount());
    assertEquals("bc", register.getLine(0));
    assertEquals("de", register.getLine(1));
  }

  @Test
  public void pullFromFileCharwiseOnSingleLine() {
    cursor.moveTo(0, 1);
    visual.toggleMode(Visual.Mode.CHAR);
    cursor.moveTo(0, 2);
    File file = new File("abc");
    Register register = visual.getRegister(new FileView(file));
    assertEquals(1, register.getLineCount());
    assertEquals("bc", register.getLine(0));
  }

  @Test
  public void pullFromFileLinewise() {
    cursor.moveTo(0, 1);
    visual.toggleMode(Visual.Mode.LINE);
    cursor.moveTo(1, 1);
    File file = new File("abc", "def");
    Register register = visual.getRegister(new FileView(file));
    assertEquals(2, register.getLineCount());
    assertEquals("abc", register.getLine(0));
    assertEquals("def", register.getLine(1));
  }
}
