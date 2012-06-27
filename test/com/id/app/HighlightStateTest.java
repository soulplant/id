package com.id.app;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Patterns;

public class HighlightStateTest {
  private HighlightState state;

  @Before
  public void setup() {
    state = new HighlightState();
  }

  @Test
  public void nullDoesntGetAddedToHistory() {
    state.setHighlightPattern(Patterns.wholeWord("hi"));
    state.setHighlightPattern(null);
    assertEquals(1, state.getPreviousHighlights().size());
  }

  @Test
  public void duplicateQueriesGetMovedToTheFront() {
    state.setHighlightPattern(Patterns.wholeWord("hi"));
    state.setHighlightPattern(Patterns.wholeWord("abc"));
    state.setHighlightPattern(Patterns.wholeWord("hi"));
    List<HighlightPattern> highlights = state.getPreviousHighlights();
    assertEquals(2, highlights.size());
  }
}
