package com.id.fuzzy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.fuzzy.FuzzyFinder.Listener;
import com.id.fuzzy.FuzzyFinder.SelectionListener;

public class FuzzyFinderTest {
  private FuzzyFinder fuzzyFinder;
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
    fuzzyFinder = new FuzzyFinder(file);
    listener = mock(FuzzyFinder.Listener.class);
    selectionListener = mock(FuzzyFinder.SelectionListener.class);
  }

  @Test
  public void simpleMatchTest() {
    fuzzyFinder.setQuery("chrome");
    assertTrue(fuzzyFinder.contains("src/browser/ui/chrome.h"));
    assertEquals(2, fuzzyFinder.getMatches().size());
    fuzzyFinder.setQuery("gyp");
    assertEquals(1, fuzzyFinder.getMatches().size());
  }

  @Test
  public void sendsQueryChanged() {
    fuzzyFinder.addListener(listener);
    fuzzyFinder.handleKeyStroke(KeyStroke.fromChar('c'));
    verify(listener).onQueryChanged();
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

  private void type(KeyStroke keyStroke) {
    fuzzyFinder.handleKeyStroke(keyStroke);
  }

  private void typeString(String string) {
    for (int i = 0; i < string.length(); i++) {
      fuzzyFinder.handleKeyStroke(KeyStroke.fromChar(string.charAt(i)));
    }
  }
}
