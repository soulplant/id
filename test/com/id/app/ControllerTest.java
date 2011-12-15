package com.id.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Editor;
import com.id.events.KeyStroke;
import com.id.fuzzy.FuzzyFinder;
import com.id.fuzzy.FuzzyFinder.Listener;
import com.id.platform.InMemoryFileSystem;


public class ControllerTest {
  private Controller controller;
  private ListModel<Editor> editors;
  private InMemoryFileSystem fileSystem;
  private FuzzyFinder fuzzyFinder;
  private Listener fuzzyListener;

  @Before
  public void setup() {
    editors = new ListModel<Editor>();
    fileSystem = new InMemoryFileSystem();
    fuzzyFinder = new FuzzyFinder(fileSystem);
    fuzzyListener = mock(FuzzyFinder.Listener.class);
    controller = new Controller(editors, fileSystem, fuzzyFinder);

    fileSystem.insertFile("./a", "aaa");
    fileSystem.insertFile("./b", "bbb");
    fuzzyFinder.addPathToIndex(".");
  }

  @Test
  public void moveBetweenFilesEditingThem() {
    controller.openFile("./a");
    controller.openFile("./b");
    assertEquals("aaa", editors.get(0).getLine(0));
    assertEquals("bbb", editors.get(1).getLine(0));
    typeString("SxKx");
    type(KeyStroke.escape());
    typeString("KSzJz");
    assertEquals("zJz", editors.get(0).getLine(0));
    assertEquals("xKx", editors.get(1).getLine(0));
  }

  @Test
  public void controllerCanBringUpTheFuzzyFinder() {
    fuzzyFinder.addListener(fuzzyListener);
    controller.openFuzzyFinder();
    verify(fuzzyListener).onSetVisible(true);
  }

  @Test
  public void typingGoesToTheFuzzyFinderWhenItsUp() {
    controller.openFuzzyFinder();
    fuzzyFinder.addListener(fuzzyListener);
    typeString("hi");
    verify(fuzzyListener, times(2)).onQueryChanged();
  }

  @Test
  public void typingEnterMakesTheFuzzyFinderDismiss() {
    controller.openFuzzyFinder();
  }

  @Test
  public void tBringsUpFuzzyFinder() {
    typeString("t");
    assertTrue(fuzzyFinder.isVisible());
  }

  @Test
  public void escapeQuitsFuzzyFinder() {
    typeString("t");
    type(KeyStroke.escape());
    assertFalse(fuzzyFinder.isVisible());
  }

  @Test
  public void selectFromFuzzyFinderOpensFile() {
    typeString("ta");
    type(KeyStroke.enter());
    assertFalse(fuzzyFinder.isVisible());
    assertEquals(1, editors.size());
    assertEquals(0, editors.getFocusedIndex());
    assertEquals("a", editors.get(0).getFilename());
  }

  private void type(KeyStroke keyStroke) {
    controller.handleKeyStroke(keyStroke);
  }

  private void typeString(String string) {
    for (KeyStroke keyStroke : KeyStroke.fromString(string)) {
      controller.handleKeyStroke(keyStroke);
    }
  }
}
