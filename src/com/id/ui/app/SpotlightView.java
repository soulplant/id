package com.id.ui.app;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;

@SuppressWarnings("serial")
public class SpotlightView<M, V extends JPanel> extends JPanel implements ListModel.Listener<M> {
  private final Map<M, V> map = new HashMap<M, V>();
  private final ListModel<M> editors;
  private final LinewisePanel placeholder = new LinewisePanel();
  private final ViewFactory<M, V> viewFactory;

  public SpotlightView(ListModel<M> editors, ViewFactory<M, V> viewFactory) {
    this.editors = editors;
    this.viewFactory = viewFactory;
    setLayout(new BorderLayout());
    for (int i = 0; i < editors.size(); i++) {
      M editor = editors.get(i);
      V editorPanel = viewFactory.createView(editor);
      map.put(editor, editorPanel);
    }
    refresh();
  }

  private void refresh() {
    super.removeAll();
    if (editors.isEmpty()) {
      super.add(placeholder);
      return;
    }
    super.add(map.get(editors.getFocusedItem()));
  }

  @Override
  public void onAdded(int i, M editor) {
    // TODO(koz): This is some hax here. We don't remove old editors, in case they are
    // being added and removed straight away. This currently happens when ctrl-j/k is
    // typed. We don't want to throw away the EditorPanel because it holds state such
    // as the scroll (which is also a design flaw). What should happen is we should
    // send move events as well as add/remove, so that we don't leak EditorPanels.
    V editorPanel = map.get(editor);
    if (editorPanel == null) {
      editorPanel = viewFactory.createView(editor);
      map.put(editor, editorPanel);
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
