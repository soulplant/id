package com.id.editor;

import com.id.app.ListModel;
import com.id.app.NavigationKeyHandler;
import com.id.app.Producer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;

public class StackList extends ListModel<Stack> implements KeyStrokeHandler, Producer<ListModel<Editor>> {
  private final NavigationKeyHandler<Editor> navigationKeyHandler;

  public StackList() {
    navigationKeyHandler = new NavigationKeyHandler<Editor>(this);
  }

  public Editor getFocusedEditor() {
    Stack focusedStack = getFocusedItemOrNull();
    if (focusedStack == null) {
      return null;
    }
    return focusedStack.getFocusedItemOrNull();
  }

  public void addSnippet(Editor editor) {
    if (isEmpty()) {
      add(new Stack());
    }
    getFocusedItem().add(editor);
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    Editor focusedEditor = getFocusedEditor();
    if (focusedEditor != null && focusedEditor.handleKeyStroke(keyStroke)) {
      return true;
    }
    if (navigationKeyHandler.handleKeyStroke(keyStroke)) {
      return true;
    }
    return false;
  }

  // Producer.
  @Override
  public Stack produce() {
    return getFocusedItem();
  }
}
