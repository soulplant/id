package com.id.rendering;

import com.id.ui.Constants;

import java.awt.Color;
import java.awt.Graphics;

/**
 * The matrix of items, known as entries, to be rendered on the screen. It is
 * typically not the full file, just the lines currently displayed, i.e. view of
 * the current portion of the file. An entry is a letter with some additional
 * decorations, e.g. highlighted, etc.
 *
 */
public class Matrix {
  public class Decoration {
    public boolean isVisual = false;
    public boolean isHighlight = false;
    public boolean isSearchHighlight = false;
    public boolean isWhitespaceIndicator = false;
    public boolean is80CharIndicator = false;
//    public boolean isCursor = false;
//    public boolean isWideCursor = false;
  }

  /** Decorations keyed by [row][column]. */
  private final Decoration[][] decorations;
  /** Lines keyed by row, each containing as any characters as the column count. */
  private final String[] lines;
  private final int height;
  private final int width;
  private final int charHeightPx;
  private final int fontDescentPx;
  private final int lineOffset;
  private final int charOffset;
  private final int charWidthPx;

  public Matrix(int height, int width, int fontDescentPx, int charHeightPx,
      int charWidthPx, int lineOffset, int charOffset) {
    this.height = height;
    this.width = width;
    this.fontDescentPx = fontDescentPx;
    this.charHeightPx = charHeightPx;
    this.charWidthPx = charWidthPx;
    this.lineOffset = lineOffset;
    this.charOffset = charOffset;
    this.decorations = new Decoration[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        decorations[y][x] = new Decoration();
      }
    }
    this.lines = new String[height];
    StringBuilder builder = new StringBuilder();
    for (int x = 0; x < width; x++) {
      builder.append(" ");
    }
    String emptyLine = builder.toString();
    for (int y = 0; y < height; y++) {
      lines[y] = emptyLine;
    }
  }

  public Decoration getDecoration(int y, int x) {
    if (!isDecorationInBounds(y, x)) {
      throw new IllegalArgumentException("Decoration not in bounds: [" + y + "," + x + "]");
    }
    if (decorations[y][x] == null) {
      throw new IllegalStateException("(" + y + ", " + x + ") accessed from "
          + decorations.length + ", " + decorations[0].length);
    }
    return decorations[y][x];
  }

  public String getLine(int y) {
    if (!(y >= 0 && y < lines.length)) {
      throw new IllegalArgumentException("Line not in bounds: " + y);
    }
    if (lines[y] == null) {
      throw new IllegalStateException("Row " + y + " not initialised.");
    }
    return lines[y];
  }

  public boolean isVisual(int y, int x) {
    return getDecoration(y, x).isVisual;
  }

  public void setLine(int y, String line) {
    lines[y] = line;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public void render(Graphics g) {
    drawBackground(g);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Decoration decoration = getDecoration(y, x);
        int boxY = (lineOffset + y) * charHeightPx;
        int boxX = x * charWidthPx;
        if (decoration.isWhitespaceIndicator) {
          g.setColor(Constants.WHITESPACE_INDICATOR_COLOR);
          g.fillRect(boxX + charWidthPx, boxY, 2, charHeightPx);
        }
        if (decoration.is80CharIndicator) {
          g.setColor(Constants.EIGHTY_CHAR_INDICATOR_COLOR);
          g.fillRect(boxX + charWidthPx, boxY, 2, charHeightPx);
        }
      }
      // NOTE drawString() takes the bottom y coordinate of the rect to draw the text in.
      int textY = (lineOffset + y + 1) * charHeightPx - fontDescentPx;
      g.setColor(Constants.TEXT_COLOR);
      g.drawString(getLine(y), charOffset * charWidthPx, textY);
    }
  }

  private void drawBackground(Graphics g) {
    for (int y = 0; y < height; y++) {
      int boxY = (lineOffset + y) * charHeightPx;
      int boxX = 0;
      RectFiller rectFiller = new RectFiller(g, boxX, boxY);
      for (int x = 0; x < width; x++) {
        Decoration decoration = getDecoration(y, x);
        if (decoration.isVisual) {
          rectFiller.nextColor(Constants.VISUAL_COLOR);
        } else if (decoration.isHighlight || decoration.isSearchHighlight) {
          rectFiller.nextColor(Constants.HIGHLIGHT_COLOR);
        } else {
          rectFiller.nextColor(null);
        }
      }
      rectFiller.done();
    }
  }

  /**
   * Draws a line of character rects, consolidating adjacent rects into one when
   * they are the same color.
   */
  private class RectFiller {
    private final int startX;
    private final int startY;
    private final Graphics g;

    private Color currentColor = Constants.BG_COLOR;
    private int rectLengthDrawn = 0;
    private int currentRectLength = 0;

    public RectFiller(Graphics g, int startX, int startY) {
      this.g = g;
      this.startX = startX;
      this.startY = startY;
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

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for (int y = 0; y < height; y++) {
      buffer.append("[");
      buffer.append(getLine(y));
      buffer.append("]");
    }
    return buffer.toString();
  }

  public int getLineOffset() {
    return lineOffset;
  }

  public int getCharOffset() {
    return charOffset;
  }

  private boolean isDecorationInBounds(int y, int x) {
    if (decorations.length == 0) {
      return false;
    }
    return y >= 0 && y < decorations.length && x >= 0 && x < decorations[0].length;
  }
}
