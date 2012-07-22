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
    Editor editor = getFocusedItemOrNull();
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

}
