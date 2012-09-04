package com.id.app;

import com.id.editor.Editor;
import com.id.editor.Editor.EditorEnvironment;
import com.id.editor.EditorList;
import com.id.editor.StackList;
import com.id.file.File;
import com.id.file.FileView;
import com.id.fuzzy.Finder;
import com.id.platform.FileSystem;

public class EditorOpener implements EditorEnvironment {
  private final EditorFactory editorFactory;
  private final FocusManager focusManager;
  private final EditorList editorList;
  private final StackList stackList;
  private final FileSystem fileSystem;
  private final Finder finder;

  public EditorOpener(EditorFactory editorFactory, FocusManager focusManager,
      EditorList editorList, StackList stackList, FileSystem fileSystem,
      Finder finder) {
    this.editorFactory = editorFactory;
    this.focusManager = focusManager;
    this.editorList = editorList;
    this.stackList = stackList;
    this.fileSystem = fileSystem;
    this.finder = finder;

    editorFactory.setEditorEnvironment(this);
  }

  // TODO(koz): Make this take an enum, rather than a boolean.
  public Editor openFile(String filename, boolean createNewFile) {
    if (filename == null) {
      throw new IllegalStateException("Don't pass null filenames.");
    }
    Editor existingEditor = focusManager.focusEditor(filename);
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
    Editor editor = editorFactory.makeEditor(fileView);
    editorList.insertAfterFocused(editor);
    return editor;
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

  @Override
  public Editor openFile(String filename) {
    return openFile(filename, true);
  }

  @Override
  public void openFileMatchingPattern(String pattern) {
    String filename = finder.findFirstFileMatching(pattern);
    if (filename == null) {
      return;
    }
    openFile(filename, true);
  }

  @Override
  public void addSnippet(FileView fileView) {
    Editor editor = editorFactory.makeEditor(fileView);
    stackList.addSnippet(editor);
  }
}
