package com.id.app;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class EditorSwapperPanel extends JPanel {
  private final List<EditorPanel> editorPanels = new ArrayList<EditorPanel>();
  private final JLabel placeholderLabel = new JLabel("No more things to edit");
  private int selected;

  public EditorSwapperPanel() {
    setLayout(new BorderLayout());
    super.add(placeholderLabel);
  }

  public void addEditor(EditorPanel editorPanel) {
    editorPanels.add(editorPanel);
    if (editorPanels.size() == 1) {
      setSelected(0);
    }
  }

  public void removeEditor(EditorPanel editorPanel) {
    int i = editorPanels.indexOf(editorPanel);
    if (i == selected) {
      previous();
    }
    editorPanels.remove(editorPanel);
    if (editorPanels.isEmpty()) {
      super.removeAll();
      super.add(placeholderLabel);
    }
  }

  public void next() {
    setSelected(selected + 1);
  }

  public void previous() {
    setSelected(selected - 1);
  }

  protected void setSelected(int selected) {
    this.selected = Math.max(0, Math.min(editorPanels.size() - 1, selected));
    super.removeAll();
    add(editorPanels.get(this.selected), BorderLayout.CENTER);
  }

  public boolean focusByFilename(String filename) {
    if (filename == null) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < editorPanels.size(); i++) {
      if (filename.equals(editorPanels.get(i).getFilename())) {
        setSelected(i);
        return true;
      }
    }
    return false;
  }
}