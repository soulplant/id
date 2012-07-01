package com.id.ui.editor;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.editor.Point;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements Editor.EditorView {
  private final TextPanel textPanel;
  private final Editor editor;
  private final JScrollPane scrollPane;
  private final EditorTitleView titleView;

  public EditorPanel(Editor editor, final ListModel<Editor> editors,
      boolean showScrollbars) {
    setLayout(new BorderLayout());
    this.editor = editor;
    textPanel = new TextPanel(editor);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new MarkerPanel(editor), BorderLayout.LINE_START);
    panel.add(textPanel, BorderLayout.CENTER);
    if (showScrollbars) {
      // TODO(koz): Implement an actual scrollbar in terms of the scrollPane.
      JPanel scrollBar = new JPanel();
      panel.add(scrollBar, BorderLayout.LINE_END);
    }
    titleView = new EditorTitleView(editor, editors);
    this.add(titleView, BorderLayout.PAGE_START);
    scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    this.add(scrollPane, BorderLayout.CENTER);
    editor.setView(this);
  }

  public boolean handleKeyPress(KeyEvent e) {
    return textPanel.handleKeyPress(e);
  }

  public Editor getEditor() {
    return editor;
  }

  // EditorView.
  @Override
  public void moveViewportToIncludePoint(Point point) {
    textPanel.scrollRectToVisible(cursorPointToRect(point));
  }

  @Override
  public void recenterScreenOnPoint(Point point) {
    int fontWidthPx = textPanel.getFontWidthPx();
    int fontHeightPx = textPanel.getFontHeightPx();
    int viewportHeight = textPanel.getVisibleRect().height;
    int padding = (viewportHeight - fontHeightPx) / 2;

    Rectangle rect = new Rectangle(point.getX() * fontWidthPx,
        point.getY() * fontHeightPx - padding, fontWidthPx, viewportHeight);
    textPanel.scrollRectToVisible(rect);
  }

  @Override
  public int getViewportHeight() {
    return getHeight() / textPanel.getFontHeightPx();
  }

  @Override
  public boolean isVisible(Point point) {
    return textPanel.getVisibleRect().contains(
        point.getX() * textPanel.getFontWidthPx(),
        point.getY() * textPanel.getFontHeightPx());
  }

  @Override
  public int getTopLineVisible() {
    return textPanel.getTopLineVisible();
  }

  @Override
  public void setTopLineVisible(int topLine) {
    // TODO(koz): Implement this properly.
    recenterScreenOnPoint(new Point(topLine, 0));
  }

  private Rectangle cursorPointToRect(Point point) {
    int fontWidthPx = textPanel.getFontWidthPx();
    int fontHeightPx = textPanel.getFontHeightPx();
    return new Rectangle(point.getX() * fontWidthPx, point.getY() * fontHeightPx, fontWidthPx,
        fontHeightPx);
  }
}
