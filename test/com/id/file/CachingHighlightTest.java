package com.id.file;

import static org.junit.Assert.*;

import org.junit.Test;

import com.id.editor.Point;

public class CachingHighlightTest {
  @Test
  public void highlight() {
    File file = new File("abc", "dog");
    CachingHighlight highlight = new CachingHighlight("dog", file);
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
    CachingHighlight highlight = new CachingHighlight("dog", file);

    assertPointEquals(1, 0, highlight.getPreviousMatch(1, 1));
  }

  @Test
  public void itGoesToPreviousMatchesOnTheSameLine() {
    File file = new File("abc abc abc");
    CachingHighlight highlight = new CachingHighlight("abc", file);
    assertPointEquals(0, 4, highlight.getPreviousMatch(0, 8));
    assertPointEquals(0, 4, highlight.getPreviousMatch(0, 7));
  }

  @Test
  public void itGoesToNextMatchesOnTheSameLine() {
    File file = new File("abc abc abc");
    CachingHighlight highlight = new CachingHighlight("abc", file);
    assertPointEquals(0, 4, highlight.getNextMatch(0, 1));
    assertPointEquals(0, 4, highlight.getNextMatch(0, 0));
  }

  @Test
  public void itCountsOccurrencesOfTheHighlightTerm() {
    File file = new File("abc abc def", "abc");
    CachingHighlight highlight = new CachingHighlight("abc", file);
    assertEquals(3, highlight.getMatchCount());
  }

  @Test
  public void itCountsOccurrencesCorrectlyAfterModifications() {
    File file = new File("abc abc def", "abc");
    CachingHighlight highlight = new CachingHighlight("abc", file);
    file.addListener(highlight);
    file.removeLine(1);
    assertEquals(2, highlight.getMatchCount());
  }

  @Test
  public void itHandlesEmptyQueriesGracefully() {
    File file = new File("abc");
    CachingHighlight highlight = new CachingHighlight("", file);
    assertEquals(0, highlight.getMatchCount());
  }

  private void assertPointEquals(int y, int x, Point point) {
    assertNotNull(point);
    assertEquals(y, point.getY());
    assertEquals(x, point.getX());
  }
}
