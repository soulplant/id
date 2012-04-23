package com.id.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.id.data.Data;
import com.id.data.Data.Session.Builder;
import com.id.editor.Editor;
import com.id.editor.Editor.EditorEnvironment;
import com.id.editor.Register;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;
import com.id.file.FileView;
import com.id.fuzzy.FuzzyFinder;
import com.id.git.Diff;
import com.id.git.Repository;
import com.id.platform.FileSystem;

public class Controller implements KeyStrokeHandler, FuzzyFinder.SelectionListener {
  private final ListModel<Editor> editors;
  private final FileSystem fileSystem;
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final FuzzyFinder fuzzyFinder;
  private final Repository repository;
  private final HighlightState highlightState;
  private final Register register = new Register();
  private final ListModel<Editor> stack;
  private final EditorEnvironment editorEnvironment = new EditorEnvironment() {
    @Override
    public void openFile(String filename) {
      Controller.this.openFile(filename);
    }

    @Override
    public void addSnippet(FileView fileView) {
      Controller.this.addSnippet(fileView);
    }
  };

  public Controller(ListModel<Editor> editors, FileSystem fileSystem,
      FuzzyFinder fuzzyFinder, Repository repository,
      HighlightState highlightState, ListModel<Editor> stack) {
    this.editors = editors;
    this.fileSystem = fileSystem;
    this.fuzzyFinder = fuzzyFinder;
    this.repository = repository;
    this.highlightState = highlightState;
    this.stack = stack;
    stack.setFocusLatest(false);
    fuzzyFinder.setSelectionListener(this);
    editors.focus();
    shortcuts.setShortcut(KeyStroke.fromString("J"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveFocusDown();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("K"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveFocusUp();
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
    shortcuts.setShortcut(KeyStroke.fromString("1"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        importDiffs();
      }
    });
    shortcuts.setShortcut(Arrays.asList(KeyStroke.fromControlChar('s')),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        saveFile();
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
    editors.getFocusedItem().save(fileSystem);
  }

  public void showFuzzyFinder() {
    fuzzyFinder.clearQuery();
    fuzzyFinder.setVisible(true);
  }

  private void moveFocusUp() {
    getFocusedList().moveUp();
  }

  private void moveFocusDown() {
    getFocusedList().moveDown();
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
    Editor existingEditor = attemptToFocusExistingEditor(filename);
    if (existingEditor != null) {
      return existingEditor;
    }
    FileView fileView = loadFileView(filename, 0, -1);
    if (fileView == null) {
      return null;
    }
    Editor editor = makeEditor(fileView);
    editors.add(editor);
    return editor;
  }

  private Editor openSnippet(String filename, int start, int end) {
    FileView fileView = loadFileView(filename, start, end);
    if (fileView == null) {
      return null;
    }
    Editor editor = makeEditor(fileView);
    stack.add(editor);
    return editor;
  }

  private Editor makeEditor(FileView fileView) {
    return new Editor(fileView, highlightState, register, editorEnvironment);
  }

  private void addSnippet(FileView fileView) {
    stack.add(makeEditor(fileView));
  }

  private Editor attemptToFocusExistingEditor(String filename) {
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
    }
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (fuzzyFinder.isVisible() && fuzzyFinder.handleKeyStroke(keyStroke)) {
      return true;
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

  @Override
  public void onItemSelected(String fuzzyFinderFile) {
    openFile(fuzzyFinderFile);
    fuzzyFinder.setVisible(false);
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
}
