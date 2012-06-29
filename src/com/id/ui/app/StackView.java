package com.id.ui.app;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.Constants;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class StackView extends JPanel implements ListModel.Listener<Editor> {
  private final ListModel<Editor> editors;

  public StackView(ListModel<Editor> editors) {
    this.editors = editors;
    setLayout(new StackLayout());
    updateEditors();
    editors.addListener(this);
    setBackground(Constants.BG_COLOR);
  }

  private void updateEditors() {
    removeAll();
    for (int i = 0; i < editors.size(); i++) {
      EditorPanel editorPanel = new EditorPanel(editors.get(i), editors, false);
      add(editorPanel);
    }
  }

  @Override
  public void onAdded(int i, Editor t) {
    updateEditors();
  }

  @Override
  public void onSelectionChanged(int i, Editor t) {
    updateEditors();
  }

  @Override
  public void onRemoved(int i, Editor t) {
    updateEditors();
  }

  @Override
  public void onSelectionLost() {
    updateEditors();
  }

  @Override
  public void onFocusChanged(boolean isFocused) {
    // Do nothing.
  }
}
