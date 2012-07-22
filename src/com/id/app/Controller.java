package com.id.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.id.editor.Editor;
import com.id.editor.Editor.EditorEnvironment;
import com.id.editor.EditorList;
import com.id.editor.Minibuffer;
import com.id.editor.Patterns;
import com.id.editor.Register;
import com.id.editor.Stack;
import com.id.editor.StackList;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;
import com.id.file.FileView;
import com.id.file.Range;
import com.id.fuzzy.Finder;
import com.id.fuzzy.FinderDriver;
import com.id.fuzzy.SubstringFinderDriver;
import com.id.git.Diff;
import com.id.git.Repository;
import com.id.platform.FileSystem;
import com.id.util.StringUtils;

public class Controller implements KeyStrokeHandler {
  private final EditorList editorList;
  private final FileSystem fileSystem;
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final Finder finder;
  private final Repository repository;
  private final HighlightState highlightState;
  private final Register register = new Register();
  private final StackList stackList;
  private final Minibuffer minibuffer;
  private final CommandExecutor commandExecutor;
  private final FinderDriver autocompleteDriver;

  private boolean isInMinibuffer = false;
  private boolean isStackVisible = false;
  private final List<Listener> listeners = new ArrayList<Listener>();

  public interface Listener {
    void onStackVisibilityChanged(boolean isStackVisible);
  }

  private final EditorEnvironment editorEnvironment = new EditorEnvironment() {
    @Override
    public void openFile(String filename) {
      Controller.this.openFile(filename);
    }

    @Override
    public void addSnippet(FileView fileView) {
      Controller.this.addSnippet(fileView);
    }

    @Override
    public void openFileMatchingPattern(String pattern) {
      Controller.this.openFileMatchingPattern(pattern);
    }

    @Override
    public void autocompleteStart(String query, Editor editor) {
      Controller.this.autocomplete(query, editor);
    }
  };
  private final FinderDriver fileFinderDriver;

  public Controller(EditorList editorList, FileSystem fileSystem,
      Finder fuzzyFinder, Repository repository, HighlightState highlightState,
      StackList stackList, Minibuffer minibuffer,
      CommandExecutor commandExecutor, FinderDriver autocompleteDriver,
      FinderDriver fileFinderDriver) {
    this.editorList = editorList;
    this.fileSystem = fileSystem;
    this.finder = fuzzyFinder;
    this.repository = repository;
    this.highlightState = highlightState;
    this.stackList = stackList;
    this.minibuffer = minibuffer;
    this.commandExecutor = commandExecutor;
    this.autocompleteDriver = autocompleteDriver;
    this.fileFinderDriver = fileFinderDriver;

    commandExecutor.setEnvironment(new CommandExecutor.Environment() {
      @Override
      public void openFile(String filename) {
        Controller.this.openFile(filename, true);
      }

      @Override
      public void reloadFile(String filename) {
        Controller.this.reloadFile(filename);
      }

      @Override
      public void jumpToLine(int lineNumber) {
        Controller.this.jumpToLine(lineNumber);
      }
    });
    stackList.setFocusLatest(false);
    minibuffer.addListener(new Minibuffer.Listener() {
      @Override
      public void onDone() {
        executeMinibufferCommand();
      }

      @Override
      public void onTextChanged() {
        // Do nothing.
      }

      @Override
      public void onQuit() {
        exitMinibuffer();
      }
    });
    editorList.focus();
    stackList.blur();
    shortcuts.setShortcut(KeyStroke.fromString("H"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        focusEditors();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("L"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        focusStack();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("B"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        goToTopFileInFileList();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("Q"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        closeCurrentStack();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("t"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        showFileFinder();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("q"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        closeCurrentFile();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("w"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        saveFile();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("1"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        importDiffs();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("@"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        openDeltasAsSnippets();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<CR>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        focusFromSnippet();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString(":"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        enterMinibuffer();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("?"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        selectPreviousHighlight();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<C-6>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        openOtherFiles();
      }
    });
  }

  protected void autocomplete(String query, final Editor editor) {
    finder.runFindAction(autocompleteDriver, new Finder.SelectionListener() {
      @Override
      public void onItemSelected(String item) {
        editor.autocompleteFinish(item);
      }
    });
  }

  private void enterMinibuffer() {
    isInMinibuffer = true;
  }

  private void exitMinibuffer() {
    isInMinibuffer = false;
    minibuffer.clear();
  }

  private void executeMinibufferCommand() {
    commandExecutor.execute(minibuffer.getText(), getCurrentEditor());
    exitMinibuffer();
  }

  private Editor getCurrentEditor() {
    if (editorList.isFocused()) {
      return editorList.getFocusedItem();
    } else {
      return stackList.getFocusedEditor();
    }
  }

  private void goToTopFileInFileList() {
    if (editorList.isEmpty()) {
      return;
    }
    editorList.setFocusedIndex(0);
  }

  private void openDeltasAsSnippets() {
    for (Editor editor : editorList) {
      openDeltasAsSnippetsFromEditor(editor);
    }
  }

  private void closeCurrentStack() {
    if (stackList.isEmpty()) {
      return;
    }
    stackList.removeFocused();
    if (stackList.isEmpty()) {
      focusEditors();
      updateStackVisibility();
    }
  }

  private void openDeltasAsSnippetsFromEditor(Editor editor) {
    List<Range> deltas = editor.getDeltas();
    List<Range> unconsumedDeltas = new ArrayList<Range>(deltas);
    for (Editor snippet : getSnippetsWithFilename(editor.getFilename())) {
      for (Range delta : deltas) {
        if (snippet.getRange().isOverlapping(delta)) {
          snippet.growToCover(delta);
          unconsumedDeltas.remove(delta);
        }
      }
    }

    for (Range delta : unconsumedDeltas) {
      editor.makeSnippetFromRange(delta);
    }
  }

  private List<Editor> getSnippetsWithFilename(String filename) {
    List<Editor> result = new ArrayList<Editor>();
    for (Stack stack : stackList) {
      for (Editor snippet : stack) {
        if (filename.equals(snippet.getFilename())) {
          result.add(snippet);
        }
      }
    }
    return result;
  }

  private void focusFromSnippet() {
    if (stackList.isFocused()) {
      Editor snippet = stackList.getFocusedEditor();
      String filename = snippet.getFilename();
      Editor editor = openFile(filename);
      editor.moveCursorTo(snippet.getRealCursorY(), snippet.getCursorPosition().getX());
      focusEditors();
    }
  }

  private void focusEditors() {
    if (editorList.isFocused()) {
      return;
    }
    stackList.blur();
    editorList.focus();
  }

  private void focusStack() {
    if (stackList.isFocused() || stackList.isEmpty()) {
      return;
    }
    editorList.blur();
    stackList.focus();
  }

  public void saveFile() {
    getFocusedList().getFocusedItem().save(fileSystem);
  }

  public void showFileFinder() {
    finder.runFindAction(fileFinderDriver, new Finder.SelectionListener() {
      @Override
      public void onItemSelected(String item) {
        openFile(item);
      }
    });
  }

  public FileView loadFileView(String filename, int start, int end) {
    File file = fileSystem.getFile(filename);
    if (file == null) {
      return null;
    }
    if (end != -1) {
      return new FileView(file, start, end);
    }
    return new FileView(file);
  }

  public Editor openFile(String filename) {
    return openFile(filename, true);
  }

  public Editor openFileMatchingPattern(String pattern) {
    String filename = finder.findFirstFileMatching(pattern);
    if (filename == null) {
      return null;
    }
    return openFile(filename, true);
  }

  // TODO(koz): Make this take an enum, rather than a boolean.
  public Editor openFile(String filename, boolean createNewFile) {
    if (filename == null) {
      throw new IllegalStateException("Don't pass null filenames.");
    }
    Editor existingEditor = attemptToFocusExistingEditor(filename);
    if (existingEditor != null) {
      return existingEditor;
    }
    FileView fileView = loadFileView(filename, 0, -1);
    if (fileView == null && createNewFile) {
      fileView = new FileView(File.createNewFile(filename));
    }
    if (fileView == null) {
      return null;
    }
    return openFileView(fileView);
  }

  private void openOtherFiles() {
    String filename = getFocusedEditor().getFilename();
    String parentPath = new java.io.File(filename).getParent();
    for (String subdir : fileSystem.getSubdirectories(parentPath)) {
      subdir = new java.io.File(parentPath, subdir).getPath();
      if (subdir.equals(filename)) {
        continue;
      }
      if (fileSystem.isFile(subdir)) {
        if (isSameButForExtension(subdir, filename)) {
          openFile(subdir, false);
        }
      }
    }
  }

  private boolean isSameButForExtension(String filename1, String filename2) {
    return removeExtension(filename1).equals(removeExtension(filename2));
  }

  private String removeExtension(String filename) {
    int i = filename.lastIndexOf('.');
    if (i == -1) {
      return filename;
    }
    return filename.substring(0, i);
  }

  private void reloadFile(String filename) {
    closeEditorsWithName(editorList, filename);
    // TODO(koz): This is terrible - we should handle reloads more gracefully
    // and bottom up in general.
    for (Stack stack : stackList) {
      closeEditorsWithName(stack, filename);
    }
    if (stackList.isEmpty()) {
      focusEditors();
      updateStackVisibility();
    }
    openFile(filename, false);
  }

  private void closeEditorsWithName(ListModel<Editor> editors, String filename) {
    Iterator<Editor> i = editors.iterator();
    while (i.hasNext()) {
      Editor editor = i.next();
      if (filename.equals(editor.getFilename())) {
        i.remove();
      }
    }
  }

  public Editor openFileView(FileView fileView) {
    Editor editor = makeEditor(fileView);
    editorList.insertAfterFocused(editor);
    return editor;
  }

  private Editor makeEditor(FileView fileView) {
    return new Editor(fileView, highlightState, register, editorEnvironment);
  }

  private Editor addSnippet(FileView fileView) {
    Editor editor = makeEditor(fileView);
    stackList.addSnippet(editor);
    updateStackVisibility();
    return editor;
  }

  private Editor attemptToFocusExistingEditor(String filename) {
    filename = StringUtils.normalizePath(filename);
    for (int i = 0; i < editorList.size(); i++) {
      if (filename.equals(editorList.get(i).getFilename())) {
        editorList.setFocusedIndex(i);
        return editorList.get(i);
      }
    }
    return null;
  }

  public void closeCurrentFile() {
    getFocusedList().removeFocused();
    while (!stackList.isEmpty() && stackList.getFocusedItem().isEmpty()) {
      stackList.removeFocused();
    }
    if (stackList.isEmpty()) {
      focusEditors();
      updateStackVisibility();
    }
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (finder.isVisible() && finder.handleKeyStroke(keyStroke)) {
      return true;
    }
    if (isInMinibuffer) {
      return minibuffer.handleKeyStroke(keyStroke);
    }
    if (editorList.isFocused()) {
      if (editorList.handleKeyStroke(keyStroke)) {
        return true;
      }
    }
    if (stackList.isFocused()) {
      if (stackList.handleKeyStroke(keyStroke)) {
        return true;
      }
    }
    return shortcuts.stepAndExecute(keyStroke);
  }

  private ListModel<Editor> getFocusedList() {
    return editorList.isFocused() ? editorList : stackList.getFocusedItem();
  }

  private Editor getFocusedEditor() {
    ListModel<Editor> focusedList = getFocusedList();
    if (focusedList.isEmpty()) {
      return null;
    }
    return focusedList.getFocusedItem();
  }


  private void jumpToLine(int lineNumber) {
    getFocusedEditor().jumpToLine(lineNumber);
  }

  public void importDiffs() {
    List<String> revisions = repository.getRevisionList();
    finder.runFindAction(new SubstringFinderDriver(new File(revisions)),
        new Finder.SelectionListener() {
      @Override
      public void onItemSelected(String item) {
        importDiffsRelativeTo(item.split(" ")[0]);
      }
    });
  }

  public void importDiffsRelativeTo(String revision) {
    Diff diff = repository.getDiffRelativeTo(revision);
    for (String filename : diff.getModifiedFiles()) {
      Editor editor = openFile(filename);
      if (editor == null) {
        continue;  // Probably a deleted file.
      }
      editor.setDiffMarkers(diff.getDelta(filename));
    }
  }

  private void updateStackVisibility() {
    isStackVisible = !stackList.isEmpty();
    for (Listener listener : listeners) {
      listener.onStackVisibilityChanged(isStackVisible);
    }
  }

  public boolean isStackVisible() {
    return isStackVisible;
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  private void selectPreviousHighlight() {
    List<HighlightPattern> previousHighlights = highlightState.getPreviousHighlights();
    List<String> items = new ArrayList<String>();
    for (HighlightPattern pattern : previousHighlights) {
      items.add(pattern.getText());
    }
    finder.runFindAction(new SubstringFinderDriver(new File(items)), new Finder.SelectionListener() {
      @Override
      public void onItemSelected(String item) {
        highlightState.setHighlightPattern(Patterns.wholeWord(item));
      }
    });
  }
}
