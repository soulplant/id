package com.id.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Editor;
import com.id.events.KeyStroke;
import com.id.file.Tombstone;
import com.id.fuzzy.FuzzyFinder;
import com.id.fuzzy.FuzzyFinder.Listener;
import com.id.git.Diff;
import com.id.git.FileDelta;
import com.id.git.InMemoryRepository;
import com.id.platform.InMemoryFileSystem;


public class ControllerTest {
  private Controller controller;
  private ListModel<Editor> editors;
  private InMemoryFileSystem fileSystem;
  private FuzzyFinder fuzzyFinder;
  private Listener fuzzyListener;
  private InMemoryRepository repo;

  @Before
  public void setup() {
    editors = new ListModel<Editor>();
    fileSystem = new InMemoryFileSystem();
    fuzzyFinder = new FuzzyFinder(fileSystem);
    fuzzyListener = mock(FuzzyFinder.Listener.class);
    repo = new InMemoryRepository();
    controller = new Controller(editors, fileSystem, fuzzyFinder, repo);

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
    controller.showFuzzyFinder();
    verify(fuzzyListener).onSetVisible(true);
  }

  @Test
  public void typingGoesToTheFuzzyFinderWhenItsUp() {
    controller.showFuzzyFinder();
    fuzzyFinder.addListener(fuzzyListener);
    typeString("hi");
    verify(fuzzyListener, times(2)).onQueryChanged();
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

  @Test
  public void showFuzzyFinderClearsOldQuery() {
    typeString("ta");
    type(KeyStroke.enter());
    typeString("t");
    assertEquals("", fuzzyFinder.getCurrentQuery());
  }

  @Test
  public void closeCurrentFile() {
    controller.openFile("./a");
    controller.openFile("./b");
    controller.closeCurrentFile();
    assertEquals(1, editors.size());
  }

  @Test
  public void canImportDiffsFromGit() {
    Map<String, FileDelta> fileDeltas = new HashMap<String, FileDelta>();
    FileDelta fileDelta = new FileDelta();
    fileDelta.addNewLine(0, "aaa");  // First line of "./a" is new.
    fileDelta.addDeletedLine(0, "deleted 1");  // We deleted two lines from the end of a.
    fileDelta.addDeletedLine(0, "deleted 2");
    fileDeltas.put("./a", fileDelta);
    Diff diff = new Diff(fileDeltas);
    repo.setDiffResult(diff);
    controller.importDiffs();
    assertEquals(Tombstone.Status.NEW, editors.get(0).getStatus(0));
    assertEquals(2, editors.get(0).getGrave(0).size());
  }

  @Test
  public void openingNonExistentFileShouldntCrash() {
    controller.openFile("doesn't exist");
  }

  @Test
  public void filesGetSavedToTheFileSystem() {
    controller.openFile("./a");
    typeString("SXXX");
    type(KeyStroke.escape());
    type(KeyStroke.fromControlChar('s'));
    assertEquals("XXX", fileSystem.getFile("./a").getLine(0));
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
