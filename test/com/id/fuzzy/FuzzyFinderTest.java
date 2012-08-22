package com.id.fuzzy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.fuzzy.Finder.Listener;
import com.id.fuzzy.Finder.SelectionListener;

public class FuzzyFinderTest {
  private Finder fuzzyFinder;
  private Listener listener;
  private SelectionListener selectionListener;

  @Before
  public void setup() {
    setupWithFiles(
        "src/browser/ui/chrome.h",
        "src/browser/ui/chrome.cc",
        "src/test",
        "src/blah.gyp");
  }

  private void setupWithFiles(String... filenames) {
    File file = new File(filenames);
    fuzzyFinder = new Finder(file);
    selectionListener = mock(Finder.SelectionListener.class);
    fuzzyFinder.runFindAction(new FuzzyFinderDriver(file), selectionListener);
    listener = mock(Finder.Listener.class);
  }

  @Test
  public void simpleMatchTest() {
    fuzzyFinder.setQuery("chrome");
    assertTrue(fuzzyFinder.contains("src/browser/ui/chrome.h"));
    assertEquals(2, fuzzyFinder.getMatches().size());
    fuzzyFinder.setQuery("gyp");
    assertEquals(1, fuzzyFinder.getMatches().size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void sendsQueryChanged() {
    fuzzyFinder.addListener(listener);
    fuzzyFinder.handleKeyStroke(KeyStroke.fromChar('c'));
    verify(listener).onMatchesChanged(any(List.class));
  }

  @Test
  public void enterMakesSelection() {
    fuzzyFinder.setVisible(true);
    typeString("chrome");
    fuzzyFinder.setSelectionListener(selectionListener);
    type(KeyStroke.enter());
    verify(selectionListener).onItemSelected(any(String.class));
  }

  @Test
  public void enterOnEmptyDismissesIt() {
    setupWithFiles();
    fuzzyFinder.setVisible(true);
    type(KeyStroke.enter());
    assertFalse(fuzzyFinder.isVisible());
  }

  @Test
  public void bareWords() {
    setupWithFiles("a", "b");
    typeString("a");
    assertEquals(1, fuzzyFinder.getMatches().size());
  }

  @Test
  public void startsWithAllAsResults() {
    setupWithFiles("aaa", "aa", "a");
    assertEquals(3, fuzzyFinder.getMatches().size());
  }

  @Test
  public void down() {
    setupWithFiles("aaa", "aa", "a");
    fuzzyFinder.setVisible(true);
    assertEquals(3, fuzzyFinder.getMatches().size());
    fuzzyFinder.setSelectionListener(selectionListener);
    typeString("<DOWN><CR>");
    verify(selectionListener).onItemSelected("aa");
  }

  @Test
  public void downDownUp() {
    setupWithFiles("aaa", "aa", "a");
    fuzzyFinder.setVisible(true);
    fuzzyFinder.setSelectionListener(selectionListener);
    typeString("<DOWN><DOWN><UP><CR>");
    verify(selectionListener).onItemSelected("aa");
  }

  @Test
  public void resetCursorToStartWhenQueryChanges() {
    setupWithFiles("aaa", "aa", "a");
    fuzzyFinder.setVisible(true);
    typeString("<DOWN>");
    assertEquals(1, fuzzyFinder.getCursorIndex());
    typeString("a");
    assertEquals(0, fuzzyFinder.getCursorIndex());
  }

  @Test
  public void upStaysInBounds() {
    setupWithFiles("abc");
    fuzzyFinder.setVisible(true);
    fuzzyFinder.setSelectionListener(selectionListener);
    typeString("<UP><CR>");
    verify(selectionListener).onItemSelected("abc");
  }

  @Test
  public void downStaysInBounds() {
    setupWithFiles("aaa");
    fuzzyFinder.setSelectionListener(selectionListener);
    typeString("<DOWN><DOWN><CR>");
    verify(selectionListener).onItemSelected("aaa");
  }

  private void type(KeyStroke keyStroke) {
    fuzzyFinder.handleKeyStroke(keyStroke);
  }

  private void typeString(String string) {
    for (KeyStroke keyStroke : KeyStroke.fromString(string)) {
      fuzzyFinder.handleKeyStroke(keyStroke);
    }
  }
}
