package com.id.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.id.data.Data;
import com.id.data.Data.Session.Builder;
import com.id.editor.Editor;
import com.id.editor.Editor.EditorEnvironment;
import com.id.editor.Minibuffer;
import com.id.editor.Register;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;
import com.id.file.FileView;
import com.id.file.Range;
import com.id.fuzzy.Finder;
import com.id.fuzzy.FinderDriver;
import com.id.git.Diff;
import com.id.git.Repository;
import com.id.platform.FileSystem;
import com.id.util.StringUtils;

public class Controller implements KeyStrokeHandler, Finder.SelectionListener {
  private final ListModel<Editor> editors;
  private final FileSystem fileSystem;
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final Finder finder;
  private final Repository repository;
  private final HighlightState highlightState;
  private final Register register = new Register();
  private final ListModel<Editor> stack;
  private final Minibuffer minibuffer;
  private final CommandExecutor commandExecutor;
  private final FinderDriver autocompleteDriver;
  private Editor autocompletingEditor = null;

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

  public Controller(ListModel<Editor> editors, FileSystem fileSystem,
      Finder fuzzyFinder, Repository repository,
      HighlightState highlightState, ListModel<Editor> stack, Minibuffer minibuffer,
      CommandExecutor commandExecutor, FinderDriver autocompleteDriver) {
    this.editors = editors;
    this.fileSystem = fileSystem;
    this.finder = fuzzyFinder;
    this.repository = repository;
    this.highlightState = highlightState;
    this.stack = stack;
    this.minibuffer = minibuffer;
    this.commandExecutor = commandExecutor;
    this.autocompleteDriver = autocompleteDriver;
    commandExecutor.setEnvironment(new CommandExecutor.Environment() {
      @Override
      public void openFile(String filename) {
        Controller.this.openFile(filename, true);
      }

      @Override
      public void jumpToLine(int lineNumber) {
        Controller.this.jumpToLine(lineNumber);
      }
    });
    stack.setFocusLatest(false);
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
    fuzzyFinder.setSelectionListener(this);
    editors.focus();
    shortcuts.setShortcut(KeyStroke.fromString("J"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveFocusDown();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<C-j>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveFocusedItemDown();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("K"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveFocusUp();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<C-k>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveFocusedItemUp();
      }
    });
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
        closeAllSnippets();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("t"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        showFuzzyFinder();
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
    shortcuts.setShortcut(KeyStroke.fromString("3"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        saveState();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("4"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        loadState();
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
  }

  protected void autocomplete(String query, Editor editor) {
    finder.setDriver(autocompleteDriver);
    finder.setVisible(true);
    autocompletingEditor = editor;
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
    if (editors.isFocused()) {
      return editors.getFocusedItemOrNull();
    } else {
      return stack.getFocusedItemOrNull();
    }
  }

  private void goToTopFileInFileList() {
    if (editors.isEmpty()) {
      return;
    }
    editors.setFocusedIndex(0);
  }

  private void openDeltasAsSnippets() {
    for (Editor editor : editors) {
      openDeltasAsSnippetsFromEditor(editor);
    }
  }

  private void closeAllSnippets() {
    closeEditors(stack);
    focusEditors();
    updateStackVisibility();
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
    for (Editor snippet : stack) {
      if (filename.equals(snippet.getFilename())) {
        result.add(snippet);
      }
    }
    return result;
  }

  private void focusFromSnippet() {
    if (stack.isFocused()) {
      Editor snippet = stack.getFocusedItem();
      String filename = snippet.getFilename();
      Editor editor = openFile(filename);
      editor.moveCursorTo(snippet.getRealCursorY(), snippet.getCursorPosition().getX());
      focusEditors();
    }
  }

  private void saveState() {
    OutputStream outputStream;
    try {
      outputStream = new FileOutputStream("state");
      getSerialized().writeTo(outputStream);
      System.out.println(getSerialized());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadState() {
    InputStream inputStream;
    try {
      inputStream = new FileInputStream("state");
      Data.Session session = Data.Session.parseFrom(inputStream);
      restoreState(session);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void restoreState(Data.Session session) {
    resetState();
    for (Data.Editor editorData : session.getEditorsList()) {
      Editor editor = openFile(editorData.getFilename());
      editor.moveCursorTo(editorData.getCursorY(), editorData.getCursorX());
      editor.setTopLineVisible(editorData.getTop());
    }
    for (Data.Editor snippetData : session.getStackList()) {
      Editor editor = openSnippet(snippetData.getFilename(),
          snippetData.getStart(), snippetData.getEnd());
      editor.moveCursorTo(snippetData.getCursorY(), snippetData.getCursorX());
      editor.setTopLineVisible(snippetData.getTop());
    }
  }

  private void resetState() {
    closeEditors(stack);
    closeEditors(editors);
  }

  private void closeEditors(ListModel<Editor> toClose) {
    while (!toClose.isEmpty()) {
      toClose.removeFocused();
    }
  }

  private void focusEditors() {
    if (editors.isFocused()) {
      return;
    }
    stack.blur();
    editors.focus();
  }

  private void focusStack() {
    if (stack.isFocused() || stack.isEmpty()) {
      return;
    }
    editors.blur();
    stack.focus();
  }

  public void saveFile() {
    getFocusedList().getFocusedItem().save(fileSystem);
  }

  public void showFuzzyFinder() {
    finder.clearQuery();
    finder.setVisible(true);
  }

  private void moveFocusUp() {
    getFocusedList().moveUp();
  }

  private void moveFocusDown() {
    getFocusedList().moveDown();
  }

  private void moveFocusedItemUp() {
    getFocusedList().moveFocusedItemUp();
  }

  private void moveFocusedItemDown() {
    getFocusedList().moveFocusedItemDown();
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

  public Editor openFileView(FileView fileView) {
    Editor editor = makeEditor(fileView);
    editors.insertAfterFocused(editor);
    return editor;
  }

  private Editor openSnippet(String filename, int start, int end) {
    FileView fileView = loadFileView(filename, start, end);
    if (fileView == null) {
      return null;
    }
    return addSnippet(fileView);
  }

  private Editor makeEditor(FileView fileView) {
    return new Editor(fileView, highlightState, register, editorEnvironment);
  }

  private Editor addSnippet(FileView fileView) {
    Editor editor = makeEditor(fileView);
    stack.add(editor);
    updateStackVisibility();
    return editor;
  }

  private Editor attemptToFocusExistingEditor(String filename) {
    filename = StringUtils.normalizePath(filename);
    for (int i = 0; i < editors.size(); i++) {
      if (filename.equals(editors.get(i).getFilename())) {
        editors.setFocusedIndex(i);
        return editors.get(i);
      }
    }
    return null;
  }

  public void closeCurrentFile() {
    getFocusedList().removeFocused();
    if (stack.isEmpty()) {
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
    ListModel<Editor> focusedList = getFocusedList();
    if (!focusedList.isEmpty() && focusedList.getFocusedItem().handleKeyStroke(keyStroke)) {
      return true;
    }
    return shortcuts.stepAndExecute(keyStroke);
  }

  private ListModel<Editor> getFocusedList() {
    return editors.isFocused() ? editors : stack;
  }

  private Editor getFocusedEditor() {
    ListModel<Editor> focusedList = getFocusedList();
    if (focusedList.isEmpty()) {
      return null;
    }
    return focusedList.getFocusedItem();
  }

  @Override
  public void onItemSelected(String item) {
    if (autocompletingEditor != null) {
      autocompletingEditor.autocompleteFinish(item);
      autocompletingEditor = null;
    } else {
      openFile(item);
    }
    finder.setVisible(false);
  }

  private void jumpToLine(int lineNumber) {
    getFocusedEditor().jumpToLine(lineNumber);
  }

  public void importDiffs() {
    Diff diff = repository.getDiffTo(repository.getHead());
    for (String filename : diff.getModifiedFiles()) {
      Editor editor = openFile(filename);
      if (editor == null) {
        continue;  // Probably a deleted file.
      }
      editor.setDiffMarkers(diff.getDelta(filename));
    }
  }

  public Data.Session getSerialized() {
    Builder builder = Data.Session.newBuilder();
    for (Editor editor : editors) {
      builder.addEditors(editor.getSerialized());
    }
    for (Editor editor : stack) {
      builder.addStack(editor.getSerialized());
    }
    return builder.build();
  }

  private void updateStackVisibility() {
    isStackVisible = !stack.isEmpty();
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
}
