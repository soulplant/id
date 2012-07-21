package com.id.ui.app;

public interface ViewFactory<M, V> {
  V createView(M model);
}
