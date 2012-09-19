package com.id.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.ui.Constants;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class TextPanel extends LinewisePanel {
  private final Editor editor;

  public TextPanel(Editor editor) {
    this.editor = editor;
    editor.addListener(new Editor.Listener() {
      @Override
      public void onSizeChanged() {
        updateSize();
        invalidate();
      }
    });
    updateSize();
  }

  private void updateSize() {
    setPreferredSize(new Dimension(
        getFontWidthPx(), getFontHeightPx() * editor.getLineCountForMode()));
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);

    int clipHeight = g.getClipBounds().height;
    int startY = g.getClipBounds().y / getFontHeightPx();
    int linesToRender =
        (clipHeight + getFontHeightPx() - 1) / getFontHeightPx();
    int maxLine =
        Math.min(linesToRender + startY, editor.getLineCountForMode());
    Editor.Iterator it = editor.getIterator(startY);

    for (int offset = 0; offset < maxLine && !it.done(); offset++, it.next()) {
      int y = (startY + offset) * getFontHeightPx();
      String line = it.getLine();

      if (!it.isInGrave()) {
        // Draw expando-diff background.
        if (editor.isInExpandoDiffMode()) {
          Color color = null;
          switch (editor.getStatus(it.getY())) {
          case NEW:
            color = Constants.MARKER_ADDED_COLOR;
            break;
          case MODIFIED:
            color = Constants.MARKER_MODIFIED_COLOR;
            break;
          case NORMAL:
            color = null;
            break;
          }
          if (color != null) {
            g.setColor(color);
            g.fillRect(0, y, 80 * getFontWidthPx(), getFontHeightPx());
          }
        }
        // Draw background.
        RectFiller rectFiller = new RectFiller(g, 0, y,
            getFontWidthPx(), getFontHeightPx());
        for (int j = 0; j < line.length(); j++) {
          rectFiller.nextColor(getBgColor(it.getY(), j));
        }
        rectFiller.done();

        // Draw text.
        g.setColor(Constants.TEXT_COLOR);
        g.drawString(line, 0, y + getFontHeightPx() - getFontDescentPx());

        // Draw decorations.
        if (hasTrailingWhitespace(line)) {
          g.setColor(Constants.WHITESPACE_INDICATOR_COLOR);
          g.fillRect(line.length() * getFontWidthPx(), y, 2, getFontHeightPx());
        }
        if (line.length() > 80) {
          g.setColor(Constants.EIGHTY_CHAR_INDICATOR_COLOR);
          g.fillRect(80 * getFontWidthPx(), y, 2, getFontHeightPx());
        }
      } else {
        g.setColor(Constants.MARKER_REMOVED_COLOR);
        g.fillRect(0, y, 80 * getFontWidthPx(), getFontHeightPx());
        g.setColor(Constants.TEXT_COLOR);
        g.drawString(it.getOriginal(), 0, y + getFontHeightPx() - getFontDescentPx());
      }
    }

    // Draw cursor.
    Point point = it.getCursorPosition();
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
    return null;
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

    private Color currentColor = null;
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
