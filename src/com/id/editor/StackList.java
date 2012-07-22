package com.id.editor;

import java.util.ArrayList;
import java.util.List;

import com.id.app.ListModel;
import com.id.app.NavigationKeyHandler;
import com.id.app.Producer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;

public class StackList extends ListModel<Stack> implements KeyStrokeHandler,
    Producer<ListModel<Editor>>, Hideable, ListModel.Listener<Stack> {
  private final NavigationKeyHandler<Editor> navigationKeyHandler;
  private boolean hidden = false;
  private final List<Hideable.Listener> hideableListeners = new ArrayList<Hideable.Listener>();

  public StackList() {
    navigationKeyHandler = new NavigationKeyHandler<Editor>(this);
    addListener(this);
    setHidden(true);
  }

  public Editor getFocusedEditor() {
    Stack focusedStack = getFocusedItem();
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

  public void focusNextStack() {
    if (isEmpty()) {
      add(new Stack());
      return;
    }
    if (isFocusOnLast()) {
      if (!getFocusedItem().isEmpty()) {
        add(new Stack());
      }
    } else {
      moveDown();
    }
  }

  public void focusPreviousStack() {
    if (isEmpty()) {
      add(new Stack());
      return;
    }
    if (isFocusOnFirst()) {
      if (!getFocusedItem().isEmpty()) {
        insert(0, new Stack());
      }
    } else {
      moveUp();
    }
  }

  // Hideable.
  @Override
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
    for (Hideable.Listener listener : hideableListeners) {
      listener.onHiddenChanged(hidden);
    }
  }

  @Override
  public boolean isHidden() {
    return hidden;
  }

  @Override
  public void addHideableListener(Hideable.Listener listener) {
    listener.onHiddenChanged(hidden);
    hideableListeners.add(listener);
  }

  @Override
  public void onAdded(int i, Stack t) {
    updateHidden();
  }

  @Override
  public void onSelectionChanged(int i, Stack t) {
    updateHidden();
  }

  @Override
  public void onRemoved(int i, Stack t) {
    updateHidden();
  }

  @Override
  public void onSelectionLost() {
    // Do nothing.
  }

  @Override
  public void onFocusChanged(boolean isFocused) {
    // Do nothing.
  }

  private void updateHidden() {
    setHidden(isEmpty());
  }
}
