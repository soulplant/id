package com.id.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.file.File;
import com.id.ui.Constants;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class TextPanel extends LinewisePanel {
  private final Editor editor;

  public TextPanel(Editor editor) {
    this.editor = editor;
    editor.addFileListener(new File.Listener() {
      @Override
      public void onLineInserted(int y, String line) {
        updateSize();
      }

      @Override
      public void onLineRemoved(int y, String line) {
        updateSize();
      }

      @Override
      public void onLineChanged(int y, String oldLine, String newLine) {
        repaint();
      }
    });
    updateSize();
  }

  private void updateSize() {
    setPreferredSize(new Dimension(
        getFontWidthPx(), getFontHeightPx() * editor.getLineCount()));
    invalidate();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);

    int heightLeft = g.getClipBounds().height;
    int startY = g.getClipBounds().y / getFontHeightPx();
    int linesToRender = (heightLeft + getFontHeightPx() - 1) / getFontHeightPx();
    int maxLine = Math.min(linesToRender + startY, editor.getLineCount());

    for (int i = startY; i < maxLine; i++) {
      // Draw background.
      int y = i * getFontHeightPx();
      RectFiller rectFiller = new RectFiller(g, 0, y,
          getFontWidthPx(), getFontHeightPx());
      String line = editor.getLine(i);
      for (int j = 0; j < line.length(); j++) {
        rectFiller.nextColor(getBgColor(i, j));
      }
      rectFiller.done();

      // Draw text.
      g.setColor(Color.BLACK);
      g.drawString(editor.getLine(i), 0,
          y + getFontHeightPx() - getFontDescentPx());

      // Draw decorations.
      if (hasTrailingWhitespace(line)) {
        g.setColor(Constants.WHITESPACE_INDICATOR_COLOR);
        g.fillRect(line.length() * getFontWidthPx(), y, 2, getFontHeightPx());
      }
      if (line.length() > 80) {
        g.setColor(Constants.EIGHTY_CHAR_INDICATOR_COLOR);
        g.fillRect(80 * getFontWidthPx(), y, 2, getFontHeightPx());
      }
    }

    // Draw cursor.
    Point point = editor.getCursorPosition();
    int cursorYPx = point.getY() * getFontHeightPx();
    int cursorXPx = point.getX() * getFontWidthPx();
    int cursorWidthPx = editor.isInInsertMode() ? 2 : getFontWidthPx();
    int cursorHeightPx = getFontHeightPx() - 1;
    if (editor.isFocused()) {
      if (editor.isInInsertMode()) {
        g.fillRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
      } else {
        g.drawRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
      }
    }
  }

  private boolean hasTrailingWhitespace(String line) {
    if (line.isEmpty()) {
      return false;
    }
    return Character.isWhitespace(line.charAt(line.length() - 1));
  }

  private Color getBgColor(int y, int x) {
    if (editor.isSearchHighlight(y, x)) {
      return Constants.HIGHLIGHT_COLOR;
    }
    if (editor.isHighlight(y, x)) {
      return Constants.HIGHLIGHT_COLOR;
    }
    if (editor.isInVisual(y, x)) {
      return Constants.VISUAL_COLOR;
    }
    return Constants.BG_COLOR;
  }

  public int getTopLineVisible() {
    return getVisibleRect().y / getFontHeightPx();
  }

  /**
   * Draws a line of character rects, consolidating adjacent rects into one when
   * they are the same color.
   */
  private static class RectFiller {
    private final int startX;
    private final int startY;
    private final Graphics g;

    private Color currentColor = Constants.BG_COLOR;
    private int rectLengthDrawn = 0;
    private int currentRectLength = 0;
    private final int charWidthPx;
    private final int charHeightPx;

    public RectFiller(Graphics g, int startX, int startY, int charWidthPx,
        int charHeightPx) {
      this.g = g;
      this.startX = startX;
      this.startY = startY;
      this.charWidthPx = charWidthPx;
      this.charHeightPx = charHeightPx;
    }

    public void nextColor(Color color) {
      if (color != currentColor) {
        drawRect();
        currentColor = color;
        rectLengthDrawn += currentRectLength;
        currentRectLength = 0;
      }
      currentRectLength += charWidthPx;
    }

    public void done() {
      drawRect();
    }

    private void drawRect() {
      if (currentColor == null) {
        return;
      }
      int x = startX + rectLengthDrawn;
      g.setColor(currentColor);
      g.fillRect(x, startY, currentRectLength, charHeightPx);
    }
  }
}
