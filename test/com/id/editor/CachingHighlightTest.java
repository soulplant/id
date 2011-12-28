package com.id.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.id.file.File;

public class CachingHighlightTest {
  @Test
  public void highlight() {
    File file = new File("abc", "dog");
    CachingHighlight highlight = CachingHighlight.forLiteralWord("dog", file.getLineList());
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
    CachingHighlight highlight = CachingHighlight.forLiteralWord("dog", file.getLineList());

    assertPointEquals(1, 0, highlight.getPreviousMatch(1, 1));
  }

  @Test
  public void itGoesToPreviousMatchesOnTheSameLine() {
    File file = new File("abc abc abc");
    CachingHighlight highlight = CachingHighlight.forLiteralWord("abc", file.getLineList());
    assertPointEquals(0, 4, highlight.getPreviousMatch(0, 8));
    assertPointEquals(0, 4, highlight.getPreviousMatch(0, 7));
  }

  @Test
  public void itGoesToNextMatchesOnTheSameLine() {
    File file = new File("abc abc abc");
    CachingHighlight highlight = CachingHighlight.forLiteralWord("abc", file.getLineList());
    assertPointEquals(0, 4, highlight.getNextMatch(0, 1));
    assertPointEquals(0, 4, highlight.getNextMatch(0, 0));
  }

  @Test
  public void itCountsOccurrencesOfTheHighlightTerm() {
    File file = new File("abc abc def", "abc");
    CachingHighlight highlight = CachingHighlight.forLiteralWord("abc", file.getLineList());
    assertEquals(3, highlight.getMatchCount());
  }

  @Test
  public void itCountsOccurrencesCorrectlyAfterModifications() {
    File file = new File("abc abc def", "abc");
    CachingHighlight highlight = CachingHighlight.forLiteralWord("abc", file.getLineList());
    file.addListener(highlight);
    file.removeLine(1);
    assertEquals(2, highlight.getMatchCount());
  }

  @Test
  public void itHandlesEmptyQueriesGracefully() {
    File file = new File("abc");
    CachingHighlight highlight = new CachingHighlight(null, file.getLineList());
    assertEquals(0, highlight.getMatchCount());
  }

  private void assertPointEquals(int y, int x, Point point) {
    assertNotNull(point);
    assertEquals(y, point.getY());
    assertEquals(x, point.getX());
  }
}
