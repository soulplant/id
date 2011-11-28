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
    file.addListener(highlight);

    Point point = highlight.getPreviousMatch(1, 1);
    assertNotNull(point);
    assertEquals(1, point.getY());
    assertEquals(0, point.getX());
  }
}
