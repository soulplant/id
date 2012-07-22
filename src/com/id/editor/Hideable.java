package com.id.editor;

public interface Hideable {
  public interface Listener {
    void onHiddenChanged(boolean hidden);
  }
  void setHidden(boolean hidden);
  boolean isHidden();
  void addHideableListener(Listener listener);
}
