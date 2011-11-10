package com.id.app;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EditorSwapperPanel extends JPanel {
  private final List<EditorPanel> editorPanels = new ArrayList<EditorPanel>();
  private int focused;

  public EditorSwapperPanel() {
  }

  public void addEditor(EditorPanel editorPanel) {
    editorPanels.add(editorPanel);
    if (editorPanels.size() == 1) {
      setFocused(0);
    }
  }

  public void next() {
    setFocused(focused + 1);
  }

  public void previous() {
    setFocused(focused - 1);
  }

  private void setFocused(int focused) {
    this.focused = Math.max(0, Math.min(editorPanels.size() - 1, focused));
    this.removeAll();
    add(editorPanels.get(this.focused), BorderLayout.CENTER);
  }
}
