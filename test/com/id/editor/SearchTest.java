package com.id.editor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Search.Listener;
import com.id.events.KeyStroke;
import com.id.test.EditorTestBase;

public class SearchTest extends EditorTestBase {
  private Search search;
  private Minibuffer minibuffer;
  private Listener listener;

  @Override
  protected void setFileContents(String... lines) {
    super.setFileContents(lines);
    minibuffer = new Minibuffer();
    listener = mock(Search.Listener.class);
    search = new Search(minibuffer, file, new Point(0, 0), listener);
  }

  @Before
  public void setup() {
    setFileContents();
  }

  @Test
  public void itCountsTheOccurrencesOfTheSearchTerm() {
    setFileContents("this", "is", "a", "test");
    typeString("t");
    assertEquals(3, search.getOccurrences());
    typeString("est");
    assertEquals(1, search.getOccurrences());
  }

  @Test
  public void itChangesWithTheFileThatItsGiven() {
    setFileContents("dog");
    typeString("cat");
    assertEquals(0, search.getOccurrences());
    fileView.changeLine(0, "cat");
    assertEquals(1, search.getOccurrences());
  }

  @Test
  public void typingQueryMovesCursorToFirstMatch() {
    setFileContents("abc", "def", "ghi", "gho");
    typeString("g");
    verify(listener).onMoveTo(2, 0);
    typeString("h");
    verify(listener, never()).onMoveTo(3, 0);
    typeString("o");
    verify(listener).onMoveTo(3, 0);
  }

  @Test
  public void typingQueryOnlyFirstMoveEventsForChangeInCursorPosition() {
    setFileContents("abc", "dog");
    typeString("dog");
    verify(listener).onMoveTo(1, 0);
  }

  @Test
  public void upMovesToNextThing() {
    setFileContents("dog", "cat", "dog");
    typeString("dog");
    type(KeyStroke.down());
    verify(listener).onMoveTo(2, 0);
    type(KeyStroke.up());
    verify(listener).onMoveTo(0, 0);
  }

  @Test
  public void downMovesToNextThing() {
    setFileContents("dog", "cat", "cat");
    typeString("cat");
    type(KeyStroke.down());
    verify(listener).onMoveTo(2, 0);
  }

  @Test
  public void escapeGoesBackToOriginalPosition() {
    setFileContents("dog", "cat");
    typeString("cat");
    type(KeyStroke.down());
    type(KeyStroke.escape());
    verify(listener).onMoveTo(0, 0);
  }

  @Test
  public void enterDoesntReturnToStartingPosition() {
    setFileContents("dog", "cat");
    typeString("cat");
    type(KeyStroke.down());
    type(KeyStroke.enter());
    verify(listener).onMoveTo(1, 0);
    verify(listener, never()).onMoveTo(0, 0);
  }

  @Test
  public void escapeCancelsSearch() {
    setFileContents("dog");
    typeString("do");
    type(KeyStroke.escape());
    verify(listener).onSearchCancelled();
  }

  @Override
  protected void type(KeyStroke keyStroke) {
    search.handleKeyStroke(keyStroke);
  }
}
