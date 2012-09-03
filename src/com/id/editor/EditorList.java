package com.id.editor;

import com.id.app.ListModel;
import com.id.app.NavigationKeyHandler;
import com.id.app.Producer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;

public class EditorList extends ListModel<Editor> implements KeyStrokeHandler, Producer<ListModel<Editor>> {
  private final NavigationKeyHandler<Editor> navigationKeyHandler;

  public EditorList() {
    navigationKeyHandler = new NavigationKeyHandler<Editor>(this);
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    Editor editor = getFocusedItem();
    if (editor != null && editor.handleKeyStroke(keyStroke)) {
      return true;
    }
    if (navigationKeyHandler.handleKeyStroke(keyStroke)) {
      return true;
    }
    return false;
  }

  @Override
  public ListModel<Editor> produce() {
    return this;
  }

  public Editor getEditorByName(String filename) {
    for (Editor editor : this) {
      if (filename.equals(editor.getFilename())) {
        return editor;
      }
    }
    return null;
  }

  private int getIndex(Editor target) {
    for (int i = 0; i < this.size(); i++) {
      if (get(i) == target) {
        return i;
      }
    }
    return -1;
  }

  public void setFocusedEditor(Editor editor) {
    int index = getIndex(editor);
    if (index == -1) {
      return;
    }
    setFocusedIndex(index);
  }

}
