package com.id.app;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
