package com.id.app;

import com.id.editor.Editor;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;
import com.id.file.FileView;
import com.id.fuzzy.FuzzyFinder;
import com.id.platform.FileSystem;

public class Controller implements KeyStrokeHandler, FuzzyFinder.SelectionListener {
  private final ListModel<Editor> editors;
  private final FileSystem fileSystem;
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final FuzzyFinder fuzzyFinder;

  public Controller(ListModel<Editor> editors, FileSystem fileSystem, FuzzyFinder fuzzyFinder) {
    this.editors = editors;
    this.fileSystem = fileSystem;
    this.fuzzyFinder = fuzzyFinder;
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
  }

  private void showFuzzyFinder() {
    fuzzyFinder.setVisible(true);
  }

  private void moveFocusUp() {
    editors.moveUp();
  }

  private void moveFocusDown() {
    editors.moveDown();
  }

  public void openFile(String filename) {
    File file = fileSystem.getFile(filename);
    editors.add(new Editor(new FileView(file)));
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

  public void openFuzzyFinder() {
    fuzzyFinder.setVisible(true);
  }

  @Override
  public void onItemSelected(String fuzzyFinderFile) {
    openFile(fuzzyFinderFile);
    fuzzyFinder.setVisible(false);
  }
}
