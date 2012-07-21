package com.id.ui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class ListModelView<M, V extends JPanel> extends JPanel implements ListModel.Listener<M> {
  private final Map<M, V> map = new HashMap<M, V>();
  private final ListModel<M> models;
  private final LinewisePanel placeholder = new LinewisePanel();
  private final ViewFactory<M, V> viewFactory;

  public ListModelView(ListModel<M> models, ViewFactory<M, V> viewFactory) {
    this.models = models;
    this.viewFactory = viewFactory;
    setLayout(new BorderLayout());
    for (int i = 0; i < models.size(); i++) {
      M model = models.get(i);
      V view = viewFactory.createView(model);
      map.put(model, view);
    }
    refresh();
  }

  private void refresh() {
    super.removeAll();
    if (models.isEmpty()) {
      super.add(placeholder);
      return;
    }
    super.add(map.get(models.getFocusedItem()));
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
