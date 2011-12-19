package com.id.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;
import com.id.file.FileView;

public class Minibuffer implements KeyStrokeHandler, File.Listener {
  public interface Listener {
    void onDone();
    void onTextChanged();
  }

  private final List<Listener> listeners = new ArrayList<Listener>();
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final File file;
  private final FileView fileView;
  private final Editor editor;

  public Minibuffer() {
    file = new File("");
    fileView = new FileView(file);
    editor = new Editor(fileView);
    editor.insert();
    editor.addFileListener(this);
    shortcuts.setShortcut(Arrays.asList(KeyStroke.escape()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        escape();
      }
    });
  }

  private void escape() {
    fireDone();
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

  private void fireDone() {
    for (Listener listener : listeners) {
      listener.onDone();
    }
  }

  private void fireTextChanged() {
    for (Listener listener : listeners) {
      listener.onTextChanged();
    }
  }

  public void clear() {
    file.changeLine(0, "");
  }

  public void setText(String text) {
    file.changeLine(0, text);
  }
}
