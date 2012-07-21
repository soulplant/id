package com.id.ui;

import java.util.HashMap;
import java.util.Map;

import com.id.app.ListModel;

public class ListModelBinder<M, V> implements ListModel.Listener<M> {
  private final Map<M, V> map = new HashMap<M, V>();
  private final ListModel<M> models;
  private final ViewFactory<M, V> viewFactory;
  private final ViewContainer<M, V> viewContainer;

  public ListModelBinder(ListModel<M> models, ViewFactory<M, V> viewFactory,
      ViewContainer<M, V> viewContainer) {
    this.models = models;
    this.viewFactory = viewFactory;
    this.viewContainer = viewContainer;

    for (int i = 0; i < models.size(); i++) {
      M model = models.get(i);
      V view = viewFactory.createView(model);
      map.put(model, view);
    }
    refresh();
  }

  private void refresh() {
    // TODO(koz): So how this currently works is we just pass over all our data
    // whenever the view changes. As per the TODO below, this could be made a
    // lot better if we actually told the viewContainer what things changed and
    // when.
    viewContainer.refreshFrom(models,  map);
  }

  @Override
  public void onAdded(int i, M model) {
    // TODO(koz): This is some hax here. We don't remove old editors, in case they are
    // being added and removed straight away. This currently happens when ctrl-j/k is
    // typed. We don't want to throw away the EditorPanel because it holds state such
    // as the scroll (which is also a design flaw). What should happen is we should
    // send move events as well as add/remove, so that we don't leak EditorPanels.
    V view = map.get(model);
    if (view == null) {
      view = viewFactory.createView(model);
      map.put(model, view);
    }
    refresh();
  }

  @Override
  public void onSelectionChanged(int i, M editor) {
    refresh();
  }

  @Override
  public void onRemoved(int i, M editor) {
    // EditorPanel editorPanel = map.remove(editor);
    refresh();
  }

  @Override
  public void onSelectionLost() {
    // Do nothing.
  }

  @Override
  public void onFocusChanged(boolean isFocused) {
    // Do nothing.
  }
}
