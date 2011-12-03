package com.id.app;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.id.editor.Editor;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class EditorSwapperPanel extends JPanel implements EditorList {
  private final List<EditorPanel> editorPanels = new ArrayList<EditorPanel>();
  private int selected;
  private final List<EditorList.Listener> listeners = new ArrayList<EditorList.Listener>();

  public EditorSwapperPanel() {
    setLayout(new BorderLayout());
  }

  public void addEditor(EditorPanel editorPanel) {
    editorPanels.add(editorPanel);
    if (editorPanels.size() == 1) {
      setSelected(0);
    }
  }

  public void next() {
    setSelected(selected + 1);
  }

  public void previous() {
    setSelected(selected - 1);
  }

  private void setSelected(int selected) {
    this.selected = Math.max(0, Math.min(editorPanels.size() - 1, selected));
    this.removeAll();
    add(editorPanels.get(this.selected), BorderLayout.CENTER);
    fireSelectedChanged();
  }

  private void fireSelectedChanged() {
    for (EditorList.Listener listener : listeners) {
      listener.onSelectedChanged(selected);
    }
  }

  public boolean handleKeyPress(KeyEvent e) {
    boolean handled = editorPanels.get(selected).handleKeyPress(e);
    if (!handled) {
      if (e.isShiftDown()) {
        handled = true;
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

  @Override
  public Editor getEditor(int i) {
    return editorPanels.get(i).getEditor();
  }

  @Override
  public int getSelectedIndex() {
    return selected;
  }

  @Override
  public int getEditorCount() {
    return editorPanels.size();
  }

  @Override
  public void addListener(Listener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void removeListener(Listener listener) {
    this.listeners.remove(listener);
  }
}
