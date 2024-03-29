package com.id.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.ui.Constants;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class EditorTitleView extends LinewisePanel {
  private static final int BOTTOM_PADDING_PX = 13;
  private static final int LINE_PADDING_RIGHT_PX = 10;
  private final Editor editor;

  public EditorTitleView(Editor editor) {
    this.editor = editor;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);
    g.setColor(isSelected() ? Color.BLACK: Color.GRAY);
    g.drawString(getTitle(), 0, getFontHeightPx() - 3);
    g.setColor(Constants.TITLE_LINE_COLOR);
    int lineY = getFontHeightPx() + 5;
    g.drawLine(0, lineY, getWidth() - LINE_PADDING_RIGHT_PX, lineY);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(getTitle().length() * getFontWidthPx(), getFontHeightPx() + BOTTOM_PADDING_PX);
  }

  private boolean isSelected() {
    return editor.isFocused();
  }

  private String getTitle() {
    String prefix = editor.isModified() ? "*" : "";
    return prefix + " " + editor.getFilename();
  }
}
