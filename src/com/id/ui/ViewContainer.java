package com.id.ui;

import java.util.Map;

import com.id.app.ListModel;

public interface ViewContainer<M, V> {
  void refreshFrom(ListModel<M> models, Map<M, V> viewMap);
}
