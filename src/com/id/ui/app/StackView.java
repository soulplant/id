package com.id.ui.app;

import java.awt.GridBagConstraints;

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
      public void onFocusChanged(int i, Editor t) {
        updateEditors();
      }

      @Override
      public void onRemoved(int i, Editor t) {
        updateEditors();
      }

      @Override
      public void onFocusLost() {
        updateEditors();
      }
    });
  }

  private void updateEditors() {
    removeAll();
    GridBagConstraints c = new GridBagConstraints();
    for (int i = 0; i < editors.size(); i++) {
      c.gridy = i;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      EditorPanel editorPanel = new EditorPanel(editors.get(i));
      add(editorPanel, c);
    }
  }
}
