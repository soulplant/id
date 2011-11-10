package com.id.app;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
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
    repaint();
  }

  public boolean handleKeyPress(KeyEvent e) {
    boolean handled = editorPanels.get(focused).handleKeyPress(e);
    if (!handled) {
      handled = true;
      if (e.isShiftDown()) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_J:
          next();
          break;
        case KeyEvent.VK_K:
          previous();
          break;
        default:
          handled = false;
        }
      }
    }
    if (handled) {
      repaint();
    }
    return handled;
  }
}
