package com.id.ui.app;

import java.util.HashMap;
import java.util.Map;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.editor.EditorPanel;

// TODO Merge this into EditorSwapperPanel.
@SuppressWarnings("serial")
public class SpotlightView extends EditorSwapperPanel implements ListModel.Listener<Editor> {
  private final Map<Editor, EditorPanel> map = new HashMap<Editor, EditorPanel>();
  private final ListModel<Editor> editors;

  public SpotlightView(ListModel<Editor> editors) {
    this.editors = editors;
  }

  private EditorPanel wrap(Editor editor) {
    EditorPanel panel = new EditorPanel(editor, editors);
    map.put(editor, panel);
    return panel;
  }

  @Override
  public void onAdded(int i, Editor t) {
    addEditor(wrap(t));
  }

  @Override
  public void onSelectionChanged(int i, Editor t) {
    setSelected(i);
  }

  @Override
  public void onRemoved(int i, Editor t) {
    removeEditor(map.get(t));
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
