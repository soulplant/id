package com.id.ui.app;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class StackView extends JPanel {
  private final ListModel<Editor> editors;

  public StackView(ListModel<Editor> editors) {
    this.editors = editors;
    setLayout(new StackLayout());
    updateEditors();
    editors.addListener(new ListModel.Listener<Editor>() {
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
        // TODO(koz): Make this appear focused.
      }
    });
  }

  private void updateEditors() {
    removeAll();
    for (int i = 0; i < editors.size(); i++) {
      EditorPanel editorPanel = new EditorPanel(editors.get(i));
      add(editorPanel);
    }
  }
}
