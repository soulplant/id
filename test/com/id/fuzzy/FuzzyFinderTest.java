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
import com.id.fuzzy.FuzzyFinder.Listener;
import com.id.fuzzy.FuzzyFinder.SelectionListener;
import com.id.platform.InMemoryFileSystem;

public class FuzzyFinderTest {
  private InMemoryFileSystem fileSystem;
  private FuzzyFinder fuzzyFinder;
  private Listener listener;
  private SelectionListener selectionListener;
  @Before
  public void setup() {
    fileSystem = new InMemoryFileSystem();
    fuzzyFinder = new FuzzyFinder(fileSystem);
    listener = mock(FuzzyFinder.Listener.class);
    selectionListener = mock(FuzzyFinder.SelectionListener.class);
    fileSystem.insertFile("src/browser/ui/chrome.h");
    fileSystem.insertFile("src/browser/ui/chrome.cc");
    fileSystem.insertFile("src/test");
    fileSystem.insertFile("src/blah.gyp");
  }

  @Test
  public void simpleMatchTest() {
    fuzzyFinder.addPathToIndex("src");
    fuzzyFinder.setQuery("chrome");
    assertTrue(fuzzyFinder.contains("src/browser/ui/chrome.h"));
    assertEquals(2, fuzzyFinder.getMatches().size());
    fuzzyFinder.setQuery("gyp");
    assertEquals(1, fuzzyFinder.getMatches().size());
  }

  @Test
  public void sendsQueryChanged() {
    fuzzyFinder.addPathToIndex("src");
    fuzzyFinder.addListener(listener);
    fuzzyFinder.handleKeyStroke(KeyStroke.fromChar('c'));
    verify(listener).onQueryChanged();
  }

  @Test
  public void enterMakesSelection() {
    fuzzyFinder.addPathToIndex("src");
    fuzzyFinder.setVisible(true);
    typeString("chrome");
    fuzzyFinder.setSelectionListener(selectionListener);
    type(KeyStroke.enter());
    verify(selectionListener).onItemSelected(any(String.class));
  }

  @Test
  public void enterOnEmptyDismissesIt() {
    fuzzyFinder.setVisible(true);
    type(KeyStroke.enter());
    assertFalse(fuzzyFinder.isVisible());
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
