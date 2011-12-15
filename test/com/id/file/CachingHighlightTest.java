package com.id.file;

import static org.junit.Assert.*;

import org.junit.Test;

import com.id.editor.Point;

public class CachingHighlightTest {
  @Test
  public void highlight() {
    File file = new File("abc", "dog");
    CachingHighlight highlight = new CachingHighlight("dog", file.getLineList());
    file.addListener(highlight);
    assertFalse(highlight.isHighlighted(0, 0));
    assertTrue(highlight.isHighlighted(1, 0));
    file.insertLine(0, "dog");
    assertTrue(highlight.isHighlighted(0, 0));
    assertFalse(highlight.isHighlighted(1, 0));
    assertTrue(highlight.isHighlighted(0, 0));
  }

  @Test
  public void previous() {
    File file = new File("abc", "dog");
    CachingHighlight highlight = new CachingHighlight("dog", file.getLineList());

    assertPointEquals(1, 0, highlight.getPreviousMatch(1, 1));
  }

  @Test
  public void itGoesToPreviousMatchesOnTheSameLine() {
    File file = new File("abc", "abc abc abc");
    CachingHighlight highlight = new CachingHighlight("abc", file.getLineList());
    assertPointEquals(1, 4, highlight.getPreviousMatch(1, 8));
  }

  private void assertPointEquals(int y, int x, Point point) {
    assertNotNull(point);
    assertEquals(y, point.getY());
    assertEquals(x, point.getX());
  }
}
