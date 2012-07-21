package com.id.ui;

public interface ViewFactory<M, V> {
  V createView(M model);
}
