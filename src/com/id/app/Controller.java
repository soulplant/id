package com.id.app;

import java.util.Arrays;

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

  public Controller(ListModel<Editor> editors, FileSystem fileSystem,
      FuzzyFinder fuzzyFinder, Repository repository,
      HighlightState highlightState, ListModel<Editor> stack) {
    this.editors = editors;
    this.fileSystem = fileSystem;
    this.fuzzyFinder = fuzzyFinder;
    this.repository = repository;
    this.highlightState = highlightState;
    this.stack = stack;
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
  }

  protected void focusEditors() {
    if (editors.isFocused()) {
      return;
    }
    stack.blur();
    editors.focus();
  }

  protected void focusStack() {
    if (stack.isFocused()) {
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
    editors.moveUp();
  }

  private void moveFocusDown() {
    editors.moveDown();
  }

  public Editor openFile(String filename) {
    Editor existingEditor = attemptToFocusExistingEditor(filename);
    if (existingEditor != null) {
      return existingEditor;
    }
    File file = fileSystem.getFile(filename);
    if (file == null) {
      return null;
    }
    Editor editor = new Editor(new FileView(file), highlightState, register);
    editor.setEnvironment(new EditorEnvironment() {
      @Override
      public void openFile(String filename) {
        Controller.this.openFile(filename);
      }

      @Override
      public void addSnippet(FileView fileView) {
        Controller.this.addSnippet(fileView);
      }
    });
    editors.add(editor);
    return editor;
  }

  protected void addSnippet(FileView fileView) {
    stack.add(new Editor(fileView, highlightState, register));
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
    editors.removeFocused();
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (fuzzyFinder.isVisible() && fuzzyFinder.handleKeyStroke(keyStroke)) {
      return true;
    }
    ListModel<Editor> focusedList = editors.isFocused() ? editors : stack;
    if (!focusedList.isEmpty() && focusedList.getFocusedItem().handleKeyStroke(keyStroke)) {
      return true;
    }
    return shortcuts.stepAndExecute(keyStroke);
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
}
