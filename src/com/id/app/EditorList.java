package com.id.app;

import com.id.editor.Editor;

public interface EditorList {
  interface Listener {
    void onSelectedChanged(int i);
  }
  void addListener(Listener listener);
  void removeListener(Listener listener);
  Editor getEditor(int i);
  int getEditorCount();
  int getSelectedIndex();
}
