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
import com.id.editor.EditorList;
import com.id.editor.Minibuffer;
import com.id.editor.Point;
import com.id.editor.StackList;
import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.file.ModifiedListener;
import com.id.file.Tombstone;
import com.id.fuzzy.Finder;
import com.id.fuzzy.Finder.Listener;
import com.id.fuzzy.FuzzyFinderDriver;
import com.id.git.Diff;
import com.id.git.FileDelta;
import com.id.git.InMemoryRepository;
import com.id.platform.InMemoryFileSystem;


public class ControllerTest {
  private Controller controller;
  private EditorList editors;
  private InMemoryFileSystem fileSystem;
  private Finder fuzzyFinder;
  private Listener fuzzyListener;
  private InMemoryRepository repo;
  private HighlightState highlightState;
  private StackList stackList;
  private Minibuffer minibuffer;
  private CommandExecutor commandExecutor;

  @Before
  public void setup() {
    File files = new File("a", "b", "src/c.h", "src/c.cc", "src/d.cc");
    editors = new EditorList();
    stackList = new StackList();
    fileSystem = new InMemoryFileSystem();
    fuzzyFinder = new Finder(files);
    fuzzyListener = mock(Finder.Listener.class);
    repo = new InMemoryRepository();
    highlightState = new HighlightState();
    minibuffer = new Minibuffer();
    commandExecutor = new CommandExecutor();
    controller = new Controller(editors, fileSystem, fuzzyFinder, repo,
        highlightState, stackList, minibuffer, commandExecutor, null,
        new FuzzyFinderDriver(files));

    fileSystem.insertFile("a", "aaa");
    fileSystem.insertFile("b", "bbb");
    fileSystem.insertFile("src/c.cc", "ccc");
    fileSystem.insertFile("src/c.h", "chc");
    fileSystem.insertFile("src/d.cc", "ddd");
  }

  @Test
  public void moveBetweenFilesEditingThem() {
    controller.openFile("a");
    controller.openFile("b");
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
    controller.showFileFinder();
    verify(fuzzyListener).onSetVisible(true);
  }

  @Test
  public void typingGoesToTheFuzzyFinderWhenItsUp() {
    controller.showFileFinder();
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
    typeString("ta<CR>");
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
    controller.openFile("a");
    controller.openFile("b");
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
    fileDeltas.put("a", fileDelta);
    Diff diff = new Diff(fileDeltas);
    repo.setDiffResult(diff);
    controller.importDiffsRelativeTo("HEAD");
    assertEquals(Tombstone.Status.NEW, editors.get(0).getStatus(0));
    assertEquals(2, editors.get(0).getGrave(0).size());
  }

  @Test
  public void openingNonExistentFileShouldntCrash() {
    controller.openFile("doesn't exist");
  }

  @Test
  public void filesGetSavedToTheFileSystem() {
    Editor editor = controller.openFile("a");
    typeString("SXXX");
    type(KeyStroke.escape());
    assertTrue(editor.isModified());
    ModifiedListener listener = mock(ModifiedListener.class);
    editor.addFileModifiedListener(listener);
    type(KeyStroke.fromChar('w'));
    verify(listener).onModifiedStateChanged();
    assertFalse(editor.isModified());
    assertEquals("XXX", fileSystem.getFile("a").getLine(0));
  }

  @Test
  public void regression_bringUpFuzzyFinderTwice() {
    typeString("ta");
    type(KeyStroke.enter());
    typeString("ta");
  }

  @Test
  public void highlightIsGlobal() {
    controller.openFile("a");
    typeString("*");  // Sets highlight to 'aaa'.
    controller.openFile("b");
    typeString("Saaa");
    assertEquals("aaa", editors.get(1).getLine(0));
    assertTrue(editors.get(1).isHighlight(0, 0));
  }

  @Test
  public void openingTheSameFileAgainRefocusesTheSpotlightOntoThatEditor() {
    controller.openFile("a");
    controller.openFile("b");
    controller.openFile("a");
    assertEquals(0, editors.getFocusedIndex());
  }

  @Test
  public void gf() {
    controller.openFile("a");
    typeString("Ssrc/c.cc<ESC>gf");
    assertEquals("src/c.cc", editors.getFocusedItem().getFilename());
  }

  @Test
  public void gF() {
    controller.openFile("a");
    typeString("Sd<ESC>gF");
    assertEquals("src/d.cc", editors.getFocusedItem().getFilename());
  }

  @Test
  public void gFOperatesOnWordNotFilename() {
    controller.openFile("a");
    typeString("Sa.d<ESC>gF");
    assertEquals("src/d.cc", editors.getFocusedItem().getFilename());
  }

  @Test
  public void yankRegisterIsGlobal() {
    controller.openFile("a");
    typeString("Vy");
    Editor b = controller.openFile("b");
    typeString("P");
    assertEquals("aaa", b.getLine(0));
  }

  @Test
  public void addSnippet() {
    controller.openFile("a");
    typeString("V;");
    assertEquals(1, stackList.size());
  }

  @Test
  public void typeInSnippet() {
    controller.openFile("a");
    typeString("oabc<CR>abc<ESC>");
    assertSpotlightFocused();
    typeString("V;");  // Make a snippet out of the last line.
    typeString("k");   // Move up a line in the editors.
    typeString("L");   // Move focus to stack.
    assertStackFocused();
    typeString("oend");
    assertEquals(4, editors.get(0).getLineCount());
    assertEquals("end", editors.get(0).getLine(3));
  }

  @Test
  public void moveFocusBetweenSnippets() {
    controller.openFile("a");
    typeString("V;V;");
    assertEquals(2, stackList.getFocusedItem().size());
    typeString("L");
    assertTrue(stackList.isFocused());
    assertEquals(0, stackList.getFocusedItem().getFocusedIndex());
    typeString("K");
    assertEquals(0, stackList.getFocusedItem().getFocusedIndex());
    typeString("J");
    assertEquals(1, stackList.getFocusedItem().getFocusedIndex());
  }

  @Test
  public void qClosesSnippetWhenFocused() {
    controller.openFile("a");
    typeString("V;Lq");
    assertEquals(1, editors.size());
    assertEquals(0, stackList.size());
  }

  @Test
  public void focusMovesBackToEditorWhenFinalSnippetClosed() {
    controller.openFile("a");
    typeString("V;Lq");
    assertTrue(editors.isFocused());
  }

  @Test
  public void cantMoveFocusToEmptyStack() {
    controller.openFile("a");
    typeString("L");
    assertTrue(editors.isFocused());
  }

  @Test
  public void addSnippetsFromSnippet() {
    controller.openFile("a");
    createSnippetFromCurrentLine();
    typeString("L");
    createSnippetFromCurrentLine();
    assertEquals(2, stackList.getFocusedItem().size());
  }

  @Test
  public void addingASnippetShouldntFocusTheMostRecentlyAddedOne() {
    controller.openFile("a");
    createSnippetFromCurrentLine();
    typeString("L");
    createSnippetFromCurrentLine();
    assertEquals(0, stackList.getFocusedIndex());
  }

  @Test
  public void closingASnippetShouldMoveFocusToTheNextOneDown() {
    controller.openFile("a");
    createSnippetFromCurrentLine();
    createSnippetFromCurrentLine();
    createSnippetFromCurrentLine();
    typeString("Lq");
    assertEquals(0, stackList.getFocusedIndex());
  }

  @Test
  public void enterInASnippetShouldJumpToThatPointInTheRealFile() {
    controller.openFile("a");
    controller.openFile("b");
    assertEquals(1, editors.getFocusedIndex());
    createSnippetFromCurrentLine();
    typeString("K");
    assertEquals(0, editors.getFocusedIndex());
    typeString("L<CR>");
    assertEquals(1, editors.getFocusedIndex());
  }

  @Test
  public void openDeltasAsSnippets() {
    controller.openFile("a");
    typeString("o<ESC>@");
    assertEquals(1, stackList.size());
  }

  @Test
  public void openDeltasAsSnippetsDoesntCreateDupes() {
    controller.openFile("a");
    typeString("o<ESC>@@");
    assertEquals(1, stackList.size());
  }

  @Test
  public void commandsGetExecutedWhenTyped() {
    typeString(":e b<CR>");
    assertEquals(1, editors.size());
  }

  @Test
  public void eOpensNewFiles() {
    typeString(":e doesnt-exist<CR>");
    assertEquals(1, editors.size());
    assertEquals("doesnt-exist", editors.getFocusedItem().getFilename());
  }

  @Test
  public void ctrlKMovesFileUpInFilelist() {
    typeString(":e a<CR>:e b<CR><C-k>");
    assertEquals(2, editors.size());
    assertEquals("b", editors.getFocusedItem().getFilename());
    assertEquals(0, editors.getFocusedIndex());
  }

  @Test
  public void ctrlJMovesFileDownInFilelist() {
    typeString(":e a<CR>:e b<CR>K<C-j>");
    assertEquals(2, editors.size());
    assertEquals("a", editors.getFocusedItem().getFilename());
    assertEquals(1, editors.getFocusedIndex());
  }

  @Test
  public void BGoesToTopFileInFileList() {
    typeString(":e a<CR>:e b<CR>");
    assertEquals(1, editors.getFocusedIndex());
    typeString("B");
    assertEquals(0, editors.getFocusedIndex());
  }

  @Test
  public void QClosesAllSnippets() {
    typeString(":e a<CR>ihello<CR>world<ESC>");
    typeString("V;kV;");
    assertEquals(1, stackList.size());
    typeString("Q");
    assertEquals(0, stackList.size());
  }

  @Test
  public void QPutsFocusBackOnSpotlight() {
    typeString(":e a<CR>ihello<CR>world<ESC>");
    typeString("V;LQ");
    assertSpotlightFocused();
  }

  @Test
  public void outdentDoesntLeaveCursorPastEndOfLine() {
    typeString(":e a<CR>ia<CR>b<CR>c<CR>d<CR><ESC>");
    typeString(":3<CR>");
    Point cursor = editors.get(0).getCursorPosition();
    assertEquals(2, cursor.getY());
  }

  @Test
  public void gfOpensFilesThatDontExist() {
    typeString(":e a<CR>");
    typeString("A abc<ESC>gf");
    assertEquals(2, editors.size());
    assertEquals("abc", editors.getFocusedItem().getFilename());
  }

  @Test
  public void newFilesStartModified() {
    typeString(":e doesnt-exist<CR>");
    assertEquals(1, editors.size());
    assertTrue(editors.getFocusedItem().isModified());
    typeString("w");
    assertFalse(editors.getFocusedItem().isModified());
  }

  @Test
  public void openingAFilePutsItUnderneathTheCurrentOne() {
    typeString(":e a<CR>:e b<CR>K");
    assertEquals(0, editors.getFocusedIndex());
    typeString(":e c<CR>");
    assertEquals(1, editors.getFocusedIndex());
  }

  @Test
  public void controllerStartsWithStackInvisible() {
    assertTrue(stackList.isHidden());
  }

  @Test
  public void creatingASnippetMakesTheStackVisible() {
    typeString(":e a<CR>V;");
    assertFalse(stackList.isHidden());
  }

  @Test
  public void previousHighlightsAccessibleWithQuestionMark() {
    typeString(":e doesnt-exist<CR>");
    typeString("ia b b c<ESC>*hh*");
    // 'b' should be highlighted, so there should be two matches.
    assertEquals(2, editors.get(0).getHighlightMatchCount());
    typeString("?<DOWN><CR>");
    // 'c' should be highlighted, so there should be one match.
    assertEquals(1, editors.get(0).getHighlightMatchCount());
  }

  @Test
  public void closingTheFinalSnippetMakesTheStackInvisible() {
    typeString(":e a<CR>V;Lq");
    assertTrue(stackList.isHidden());
  }

  @Test
  public void ctrl6OpensOtherFilesWithDifferentExtensions() {
    typeString(":e src/c.h<CR><C-6>");
    assertEquals(2, editors.size());
  }

  @Test
  public void reloadCurrentFile() {
    typeString(":e src/c.h<CR>");
    String startContents = editors.getFocusedItem().getLine(0);
    typeString("Stest<ESC>");
    assertEquals("test", editors.getFocusedItem().getLine(0));
    typeString(":e<CR>");
    String endContents = editors.getFocusedItem().getLine(0);
    assertEquals(startContents, endContents);
  }

  @Test
  public void multipleStacks() {
    typeString(":e a<CR>");
    typeString("V;]V;");
    assertEquals(2, stackList.size());
  }

  private void createSnippetFromCurrentLine() {
    typeString("V;");
  }

  private void assertSpotlightFocused() {
    assertTrue(editors.isFocused());
    assertFalse(stackList.isFocused());
  }

  private void assertStackFocused() {
    assertTrue(stackList.isFocused());
    assertFalse(editors.isFocused());
  }

  private void type(KeyStroke keyStroke) {
    controller.handleKeyStroke(keyStroke);
  }

  private void typeString(String string) {
    for (KeyStroke keyStroke : KeyStroke.fromString(string)) {
      type(keyStroke);
    }
  }
}
