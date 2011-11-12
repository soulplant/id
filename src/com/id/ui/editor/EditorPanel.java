package com.id.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.id.editor.Editor;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel {
  private final TextPanel textPanel;
  private final Editor editor;

  public EditorPanel(Editor editor) {
    setLayout(new BorderLayout());
    this.editor = editor;
    textPanel = new TextPanel(editor);
    this.add(new JLabel(editor.getFilename()), BorderLayout.PAGE_START);
    this.add(textPanel, BorderLayout.CENTER);
  }

  public boolean handleKeyPress(KeyEvent e) {
    return textPanel.handleKeyPress(e);
  }

  public String getFilename() {
    return editor.getFilename();
  }

  public Editor getEditor() {
    return editor;
  }
}
