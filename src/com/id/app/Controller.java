package com.id.app;

import java.util.ArrayList;
import java.util.List;

import com.id.editor.Editor;
import com.id.editor.EditorList;
import com.id.editor.Patterns;
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

public class Controller implements KeyStrokeHandler {
  private final EditorList editorList;
  private final FileSystem fileSystem;
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final Finder finder;
  private final Repository repository;
  private final HighlightState highlightState;
  private final StackList stackList;
  private final MinibufferSubsystem minibufferSubsystem;
  private final FinderDriver autocompleteDriver;

  private final FinderDriver fileFinderDriver;
  private final FocusManager focusManager;
  private final EditorOpener editorOpener;

  public Controller(EditorList editorList, FileSystem fileSystem,
      Finder fuzzyFinder, Repository repository, HighlightState highlightState,
      final StackList stackList, final MinibufferSubsystem minibufferSubsystem,
      CommandExecutor commandExecutor, FinderDriver autocompleteDriver,
      FinderDriver fileFinderDriver, FocusManager focusManager,
      EditorOpener editorOpener) {
    this.editorList = editorList;
    this.fileSystem = fileSystem;
    this.finder = fuzzyFinder;
    this.repository = repository;
    this.highlightState = highlightState;
    this.stackList = stackList;
    this.minibufferSubsystem = minibufferSubsystem;
    this.autocompleteDriver = autocompleteDriver;
    this.fileFinderDriver = fileFinderDriver;
    this.focusManager = focusManager;
    this.editorOpener = editorOpener;

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
    shortcuts.setShortcut(KeyStroke.fromString("["), new ShortcutTree.Action() {
      @Override
      public void execute() {
        stackList.focusPreviousStack();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("]"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        stackList.focusNextStack();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString(":"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        minibufferSubsystem.activateMinibuffer();
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

  private void goToTopFileInFileList() {
    focusManager.focusTopFileInFileList();
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
    focusManager.focusEditorList();
  }

  private void focusStack() {
    focusManager.focusStackList();
  }

  public void saveFile() {
    focusManager.getFocusedEditor().save(fileSystem);
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

  private Editor openFile(String filename) {
    return editorOpener.openFile(filename);
  }

  private void openOtherFiles() {
    String filename = focusManager.getFocusedEditor().getFilename();
    String parentPath = new java.io.File(filename).getParent();
    for (String subdir : fileSystem.getSubdirectories(parentPath)) {
      subdir = new java.io.File(parentPath, subdir).getPath();
      if (subdir.equals(filename)) {
        continue;
      }
      if (fileSystem.isFile(subdir)) {
        if (isSameButForExtension(subdir, filename)) {
          editorOpener.openFile(subdir, false);
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

  public void closeCurrentFile() {
    focusManager.closeCurrentFile();
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (finder.isVisible() && finder.handleKeyStroke(keyStroke)) {
      return true;
    }
    if (minibufferSubsystem.handleKeyStroke(keyStroke)) {
      return true;
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
