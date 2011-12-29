package com.id.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ListModel<T> implements Iterable<T> {
  public interface Listener<T> {
    void onAdded(int i, T t);
    void onFocusChanged(int i, T t);
    void onRemoved(int i, T t);
    void onFocusLost();
  }

  private final List<Listener<T>> listeners = new ArrayList<Listener<T>>();
  private final List<T> items = new ArrayList<T>();
  private int focusedIndex = -1;

  public void addListener(Listener<T> listener) {
    listeners.add(listener);
  }

  public void add(T t) {
    items.add(t);
    fireOnAdded(items.size() - 1, t);
    setFocusedIndex(items.size() - 1);
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
    fireFocusChanged();
  }

  public void removeFocused() {
    if (items.isEmpty()) {
      return;
    }
    int index = focusedIndex;
    T removed = items.remove(focusedIndex);
    focusedIndex = items.size() - 1;
    fireOnRemoved(index, removed);
    if (focusedIndex == -1) {
      fireOnFocusLost();
    } else {
      fireFocusChanged();
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

  private void fireOnFocusLost() {
    for (Listener<T> listener : listeners) {
      listener.onFocusLost();
    }
  }

  private void fireFocusChanged() {
    for (Listener<T> listener : listeners) {
      listener.onFocusChanged(focusedIndex, getFocusedItem());
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
}
