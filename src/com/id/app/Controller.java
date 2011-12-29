package com.id.app;

import java.util.Arrays;

import com.id.editor.Editor;
import com.id.editor.Editor.EditorEnvironment;
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

  public Controller(ListModel<Editor> editors, FileSystem fileSystem,
      FuzzyFinder fuzzyFinder, Repository repository,
      HighlightState highlightState) {
    this.editors = editors;
    this.fileSystem = fileSystem;
    this.fuzzyFinder = fuzzyFinder;
    this.repository = repository;
    this.highlightState = highlightState;
    fuzzyFinder.setSelectionListener(this);
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
    Editor editor = new Editor(new FileView(file), highlightState);
    editor.setEnvironment(new EditorEnvironment() {
      @Override
      public void openFile(String filename) {
        Controller.this.openFile(filename);
      }
    });
    editors.add(editor);
    return editor;
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
    if (!editors.isEmpty() && editors.getFocusedItem().handleKeyStroke(keyStroke)) {
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
