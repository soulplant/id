package com.id.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.app.HighlightState;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;
import com.id.file.FileView;

public class Minibuffer implements KeyStrokeHandler, File.Listener {
  public interface Listener {
    void onTextEntered(boolean controlPressed);
    void onTextChanged();
    void onQuit();
  }

  private final List<Listener> listeners = new ArrayList<Listener>();
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final File file;
  private final FileView fileView;
  private final Editor editor;

  public Minibuffer() {
    file = new File("");
    fileView = new FileView(file);
    editor = new Editor(fileView, new HighlightState(), new Register(),
        new Editor.EmptyEditorEnvironment(), new SharedEditorSettings());
    editor.insert();
    editor.addFileListener(this);
    shortcuts.setShortcut(KeyStroke.fromString("<ESC>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        escape();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<CR>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        enter(false);
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<C-CR>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        enter(true);
      }
    });
  }

  private void enter(boolean controlPressed) {
    fireTextEntered(controlPressed);
  }

  private void escape() {
    fireQuit();
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (shortcuts.stepAndExecute(keyStroke)) {
      return true;
    }
    return editor.handleKeyStroke(keyStroke);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public String getText() {
    return editor.getLine(0);
  }

  @Override
  public void onLineInserted(int y, String line) {
    throw new IllegalStateException();
  }

  @Override
  public void onLineRemoved(int y, String line) {
    throw new IllegalStateException();
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    fireTextChanged();
  }

  private void fireQuit() {
    for (Listener listener : listeners) {
      listener.onQuit();
    }
  }

  private void fireTextEntered(boolean controlPressed) {
    for (Listener listener : listeners) {
      listener.onTextEntered(controlPressed);
    }
  }

  private void fireTextChanged() {
    for (Listener listener : listeners) {
      listener.onTextChanged();
    }
  }

  public void clear() {
    if (file.getLineCount() != 1) {
      throw new IllegalStateException("Minibuffer's file has "
          + file.getLineCount() + " lines instead of 1.");
    }
    editor.escape();
    editor.substituteLine();
  }

  public void setText(String text) {
    file.changeLine(0, text);
  }

  public Editor getEditor() {
    return editor;
  }
}
