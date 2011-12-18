package com.id.ui.editor;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.file.ModifiedListener;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements Editor.Context, ModifiedListener {
  private final TextPanel textPanel;
  private final Editor editor;
  private final JScrollPane scrollPane;
  private final JLabel filenameLabel;

  public EditorPanel(Editor editor) {
    setLayout(new BorderLayout());
    this.editor = editor;
    textPanel = new TextPanel(editor);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new MarkerPanel(editor), BorderLayout.LINE_START);
    panel.add(textPanel, BorderLayout.CENTER);
    filenameLabel = new JLabel(editor.getFilename());
    this.add(filenameLabel, BorderLayout.PAGE_START);
    scrollPane = new JScrollPane(panel);
    this.add(scrollPane, BorderLayout.CENTER);
    editor.setContext(this);
    editor.addFileModifiedListener(this);
  }

  @Override
  public void onModifiedStateChanged() {
    String prefix = editor.isModified() ? "*" : "";
    filenameLabel.setText(prefix + " " + editor.getFilename());
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

  @Override
  public void moveViewportToIncludePoint(Point point) {
    textPanel.scrollRectToVisible(cursorPointToRect(point));
  }

  private Rectangle cursorPointToRect(Point point) {
    return new Rectangle(point.getX() * 8, point.getY() * 14, 8, 14);
  }

  @Override
  public void recenterScreenOnPoint(Point point) {
    int viewportHeight = textPanel.getVisibleRect().height;
    int padding = (viewportHeight - 14) / 2;

    Rectangle rect = new Rectangle(point.getX() * 8, point.getY() * 14 - padding, 8, viewportHeight);
    textPanel.scrollRectToVisible(rect);
  }

  @Override
  public int getViewportHeight() {
    return getHeight() / 14;
  }
}
