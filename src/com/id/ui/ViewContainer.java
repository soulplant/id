package com.id.ui;

public interface ViewContainer<V> {
  void removeAll();
  void add(V view);
}
