package com.id.editor;

import com.id.app.ListModel;

public class StackList extends ListModel<Stack> {

  public Editor getFocusedEditor() {
    Stack focusedStack = getFocusedItemOrNull();
    if (focusedStack == null) {
      return null;
    }
    return focusedStack.getFocusedItem();
  }

  public void addSnippet(Editor editor) {
    if (isEmpty()) {
      add(new Stack());
    }
    getFocusedItem().add(editor);
  }
}
