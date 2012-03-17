package com.id.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.id.editor.Editor;


public class ListModel<T> implements Iterable<T> {
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
    items.add(t);
    fireOnAdded(items.size() - 1, t);
    if (isFocusLatestEnabled || items.size() == 1) {
      setFocusedIndex(items.size() - 1);
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

  public void setFocusedIndex(int index) {
    int clampedIndex = Math.min(items.size() - 1, Math.max(index, 0));
    if (focusedIndex == clampedIndex) {
      return;
    }
    focusedIndex = clampedIndex;
    fireSelectionChanged();
  }

  public void removeFocused() {
    if (items.isEmpty()) {
      return;
    }
    int index = focusedIndex;
    T removed = items.remove(focusedIndex);
    if (focusedIndex >= items.size()) {
      focusedIndex = items.size() - 1;
    }
    fireOnRemoved(index, removed);
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
        throw new UnsupportedOperationException();
      }
    };
  }

  public boolean isFocused() {
    return isFocused;
  }

  public void blur() {
    setFocused(false);
  }

  public void focus() {
    setFocused(true);
  }

  public void setFocused(boolean isFocused) {
    this.isFocused = isFocused;
    fireOnFocusChanged();
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
