package com.id.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.id.editor.Editor;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel {
  private final TextPanel textPanel;
  private final Editor editor;
  private final JScrollPane scrollPane;

  public EditorPanel(Editor editor) {
    setLayout(new BorderLayout());
    this.editor = editor;
    textPanel = new TextPanel(editor);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new MarkerPanel(editor), BorderLayout.LINE_START);
    panel.add(textPanel, BorderLayout.CENTER);
    this.add(new JLabel(editor.getFilename()), BorderLayout.PAGE_START);
    scrollPane = new JScrollPane(panel);
    this.add(scrollPane, BorderLayout.CENTER);
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
