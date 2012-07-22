package com.id.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.id.editor.Editor;
import com.id.editor.Focusable;


// TODO(koz): This class confuses 'focus' and 'selection'. Focus should refer
// to the whether or not the entire list has focus, and the selection should
// refer to which item in the list is selected.
public class ListModel<T> implements Iterable<T>, Focusable {
  public interface Listener<T> {
    void onAdded(int i, T t);
    void onSelectionChanged(int i, T t);
    void onRemoved(int i, T t);
    void onSelectionLost();
    void onFocusChanged(boolean isFocused);
  }

  private final List<Listener<T>> listeners = new ArrayList<Listener<T>>();
  private final List<T> items = new ArrayList<T>();
  private int focusedIndex = -1;
  private boolean isFocused = false;
  private boolean isFocusLatestEnabled = true;

  public void addListener(Listener<T> listener) {
    listeners.add(listener);
  }

  public void add(T t) {
    insert(items.size(), t);
  }

  public void insertAfterFocused(T t) {
    if (isEmpty()) {
      add(t);
      return;
    }
    insert(focusedIndex + 1, t);
  }

  public void insert(int i, T t) {
    setItemFocused(t, false);
    items.add(i, t);
    if (isFocusLatestEnabled || items.size() == 1) {
      setFocusedIndexImpl(i, false);
      fireOnAdded(i, t);
      setFocusedIndex(i);
    } else {
      fireOnAdded(i, t);
    }
  }

  public void setFocusLatest(boolean focusLatest) {
    isFocusLatestEnabled = focusLatest;
  }

  public void moveUp() {
    if (items.isEmpty()) {
      return;
    }
    setFocusedIndex(focusedIndex - 1);
  }

  public void moveDown() {
    if (items.isEmpty()) {
      return;
    }
    setFocusedIndex(focusedIndex + 1);
  }

  public void moveFocusedItemUp() {
    if (items.size() <= 1) {
      return;
    }
    if (isFocusAtStart()) {
      return;
    }
    T focusedItem = getFocusedItem();
    int i = getFocusedIndex();
    remove(i);
    insert(i - 1, focusedItem);
  }

  private boolean isFocusAtStart() {
    return getFocusedIndex() == 0;
  }

  public void moveFocusedItemDown() {
    if (items.size() <= 1) {
      return;
    }
    if (isFocusAtEnd()) {
      return;
    }
    T focusedItem = getFocusedItem();
    int i = getFocusedIndex();
    remove(i);
    insert(i + 1, focusedItem);
    setFocusedIndex(i + 1);
  }

  private boolean isFocusAtEnd() {
    return getFocusedIndex() == items.size() - 1;
  }

  public void setFocusedIndex(int index) {
    setFocusedIndexImpl(index, true);
  }

  private void setFocusedIndexImpl(int index, boolean notify) {
    blurItem();
    int clampedIndex = Math.min(items.size() - 1, Math.max(index, 0));
    focusedIndex = clampedIndex;
    focusItem();
    if (notify) {
      fireSelectionChanged();
    }
  }

  private void focusItem() {
    T focusedItem = getFocusedItemOrNull();
    if (focusedItem != null) {
      setItemFocused(focusedItem, true);
    }

  }

  private void setItemFocused(T item, boolean focused) {
    if (item != null && item instanceof Focusable) {
      ((Focusable) item).setFocused(isFocused() && focused);
    }
  }

  private void blurItem() {
    T focusedItem = getFocusedItemOrNull();
    if (focusedItem != null) {
      setItemFocused(focusedItem, false);
    }
  }

  public void removeFocused() {
    if (items.isEmpty()) {
      return;
    }
    remove(focusedIndex);
  }

  public void remove(int i) {
    T removed = items.remove(i);
    if (focusedIndex >= i) {
      focusedIndex--;
      if (focusedIndex < 0 && !items.isEmpty()) {
        focusedIndex = 0;
      }
    }
    setItemFocused(removed, false);
    focusItem();
    fireOnRemoved(i, removed);
    if (focusedIndex == -1) {
      fireOnSelectionLost();
    } else {
      fireSelectionChanged();
    }
  }

  private void fireOnRemoved(int i, T removed) {
    for (Listener<T> listener : listeners) {
      listener.onRemoved(i, removed);
    }
  }

  private void fireOnAdded(int i, T added) {
    for (Listener<T> listener : listeners) {
      listener.onAdded(i, added);
    }
  }

  private void fireOnSelectionLost() {
    for (Listener<T> listener : listeners) {
      listener.onSelectionLost();
    }
  }

  private void fireSelectionChanged() {
    for (Listener<T> listener : listeners) {
      listener.onSelectionChanged(focusedIndex, getFocusedItem());
    }
  }

  public T getFocusedItemOrNull() {
    if (items.isEmpty() || focusedIndex == -1) {
      return null;
    }
    return getFocusedItem();
  }

  public T getFocusedItem() {
    return items.get(focusedIndex);
  }

  public int size() {
    return items.size();
  }

  public T get(int i) {
    return items.get(i);
  }

  public int getFocusedIndex() {
    return focusedIndex;
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private int i = 0;

      @Override
      public boolean hasNext() {
        return i < size();
      }

      @Override
      public T next() {
        return get(i++);
      }

      @Override
      public void remove() {
        i--;
        ListModel.this.remove(i);
      }
    };
  }

  // Focusable.
  @Override
  public void setFocused(boolean isFocused) {
    this.isFocused = isFocused;
    if (this.isFocused) {
      focusItem();
    } else {
      blurItem();
    }
    fireOnFocusChanged();
  }

  @Override
  public boolean isFocused() {
    return isFocused;
  }

  public void blur() {
    setFocused(false);
  }

  public void focus() {
    setFocused(true);
  }

  private void fireOnFocusChanged() {
    for (Listener<T> listener : listeners) {
      listener.onFocusChanged(isFocused);
    }
  }

  public boolean isFocused(Editor editor) {
    return getFocusedItem() == editor;
  }
}
