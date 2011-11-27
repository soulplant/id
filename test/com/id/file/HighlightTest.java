package com.id.file;

import static org.junit.Assert.*;

import org.junit.Test;

public class HighlightTest {
  @Test
  public void test() {
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
}
