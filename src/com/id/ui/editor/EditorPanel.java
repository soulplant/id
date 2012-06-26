package com.id.ui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.file.ModifiedListener;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements Editor.EditorView, ModifiedListener {
  private final TextPanel textPanel;
  private final Editor editor;
  private final JScrollPane scrollPane;
  private final JLabel filenameLabel;
  private final ListModel<Editor> containerModel;

  public EditorPanel(Editor editor, final ListModel<Editor> containerModel,
      boolean showScrollbars) {
    this.containerModel = containerModel;
    setLayout(new BorderLayout());
    setBorder(new Border() {
      @Override
      public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
      }

      @Override
      public boolean isBorderOpaque() {
        return true;
      }

      @Override
      public void paintBorder(Component c, Graphics g, int x, int y, int width,
          int height) {
        g.setColor(isFocused() ? Color.GREEN : Color.RED);
        g.fillRect(x, y, width - 1, height - 1);
      }
    });
    this.editor = editor;
    textPanel = new TextPanel(editor);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new MarkerPanel(editor), BorderLayout.LINE_START);
    panel.add(textPanel, BorderLayout.CENTER);
    filenameLabel = new JLabel(getTitle());
    this.add(filenameLabel, BorderLayout.PAGE_START);
    scrollPane = new JScrollPane(panel);
    if (!showScrollbars) {
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    this.add(scrollPane, BorderLayout.CENTER);
    editor.setView(this);
    editor.addFileModifiedListener(this);
  }

  private boolean isFocused() {
    return containerModel.isFocused() && containerModel.isFocused(editor);
  }

  private String getTitle() {
    String prefix = "";
    if (editor.isDogEared()) {
      prefix = editor.isModified() ? "o" : ".";
    } else {
      prefix = editor.isModified() ? "*" : "";
    }
    return prefix + " " + editor.getFilename();
  }

  @Override
  public void onModifiedStateChanged() {
    filenameLabel.setText(getTitle());
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
    int fontWidthPx = textPanel.getFontWidthPx();
    int fontHeightPx = textPanel.getFontHeightPx();
    return new Rectangle(point.getX() * fontWidthPx, point.getY() * fontHeightPx, fontWidthPx,
        fontHeightPx);
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
    return getHeight() / 14;
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
}
