package com.id.ui.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.id.editor.Editor;
import com.id.editor.Point;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements Editor.EditorView {
  private final TextPanel textPanel;
  private final Editor editor;
  private final EditorTitleView titleView;
  private final MarkerPanel markerPanel;

  public EditorPanel(Editor editor) {
    setLayout(new BorderLayout());
    this.editor = editor;
    textPanel = new TextPanel(editor);
    markerPanel = new MarkerPanel(editor);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(markerPanel, BorderLayout.LINE_START);
    panel.add(textPanel, BorderLayout.CENTER);
    titleView = new EditorTitleView(editor);
    this.add(titleView, BorderLayout.PAGE_START);
    this.add(panel, BorderLayout.CENTER);
    editor.setView(this);
  }

  public Editor getEditor() {
    return editor;
  }

  // EditorView.
  @Override
  public void moveViewportToIncludePoint(Point point) {
    if (!isVisible(point)) {
      if (point.getY() <= textPanel.getTopLine()) {
        setTopLine(point.getY());
      } else {
        setBottomLine(point.getY());
      }
    }
  }

  private void setBottomLine(int y) {
    setTopLine(y - textPanel.getLinesHigh() + 1);
  }

  @Override
  public void recenterScreenOnPoint(Point point) {
    int topLine = point.getY() - textPanel.getLinesHigh() / 2;
    topLine = Math.max(0, topLine);
    topLine = Math.min(topLine, editor.getLineCount() - 1);
    setTopLine(topLine);
  }

  private void setTopLine(int topLine) {
    textPanel.setTopLine(topLine);
    markerPanel.setTopLine(topLine);
  }

  @Override
  public int getViewportHeight() {
    return textPanel.getLinesHigh();
  }

  @Override
  public boolean isVisible(Point point) {
    return textPanel.isPointVisible(point);
  }

  @Override
  public int getTopLineVisible() {
    return textPanel.getTopLine();
  }

  @Override
  public void setTopLineVisible(int topLine) {
    setTopLine(topLine);
  }
}
