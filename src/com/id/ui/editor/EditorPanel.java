package com.id.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.ui.Constants;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements Editor.EditorView {
  private final TextPanel textPanel;
  private final Editor editor;
  private final EditorTitleView titleView;

  public EditorPanel(Editor editor, boolean selfScrolling) {
    setLayout(new BorderLayout());
    this.editor = editor;
    textPanel = new TextPanel(editor);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new MarkerPanel(editor), BorderLayout.LINE_START);
    panel.add(textPanel, BorderLayout.CENTER);
    if (selfScrolling) {
      // Add padding at the bottom so we can scroll beyond the end of the
      // text.
      JPanel bottomPadding = new JPanel();
      bottomPadding.setBackground(Constants.BG_COLOR);
      bottomPadding.setPreferredSize(new Dimension(0, 2000));
      panel.add(bottomPadding, BorderLayout.PAGE_END);
      JScrollPane scrollPane = new JScrollPane(panel);
      scrollPane.setBorder(null);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(scrollPane, BorderLayout.CENTER);
    } else {
      add(panel, BorderLayout.CENTER);
    }
    titleView = new EditorTitleView(editor);
    add(titleView, BorderLayout.PAGE_START);
    editor.setView(this);
  }

  public Editor getEditor() {
    return editor;
  }

  // EditorView.
  @Override
  public void moveViewportToIncludePoint(Point point) {
    textPanel.scrollRectToVisible(cursorPointToRect(point));
  }

  /**
   * @param component
   * @return height in pixels of the visible rect of the viewport that contains
   *         {@code component}.
   */
  private int getViewportHeightPx(Component component) {
    return getViewport(component).getVisibleRect().height;
  }

  @Override
  public void recenterScreenOnPoint(Point point) {
    int fontHeightPx = textPanel.getFontHeightPx();
    int cursorY = point.getY() * fontHeightPx;

    scrollToCenterPointInside(textPanel, cursorY);
    invalidate();
  }

  /**
   * @return the closest ancestor JViewport to {@code component}, or
   *         {@code null}.
   */
  private JViewport getViewport(Component component) {
    Container parent = component.getParent();
    while (true) {
      if (parent instanceof JViewport) {
        return (JViewport) parent;
      }
      parent = parent.getParent();
    }
  }

  /**
   * Scrolls so that {@code y} (which is an offset in {@code component}
   * coordinates) is centered in the screen.
   */
  private void scrollToCenterPointInside(Component component, int y) {
    JViewport viewport = getViewport(component);
    // Translate the point (0, y) into the scrolled panel's coordinates (instead
    // of being relative to its direct container's coordinates).
    int offsetY = SwingUtilities.convertPoint(component,
        new java.awt.Point(0, y), viewport.getView()).y;
    int viewportHeight = viewport.getExtentSize().height;
    int viewHeight = viewport.getView().getHeight();
    int scrollY = offsetY - viewportHeight / 2;
    scrollY = Math.min(scrollY, viewHeight - viewportHeight);
    scrollY = Math.max(0, scrollY);
    viewport.setViewPosition(new java.awt.Point(0, scrollY));
  }

  @Override
  public int getViewportHeight() {
    return getViewportHeightPx(textPanel) / textPanel.getFontHeightPx();
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
