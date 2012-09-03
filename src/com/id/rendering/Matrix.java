package com.id.rendering;

import java.awt.Color;
import java.awt.Graphics;

import com.id.ui.Constants;

public class Matrix {
  public class Entry {
    public char letter;
    public boolean isVisual = false;
    public boolean isHighlight = false;
    public boolean isSearchHighlight = false;
    public boolean isWhitespaceIndicator = false;
    public boolean is80CharIndicator = false;
//    public boolean isCursor = false;
//    public boolean isWideCursor = false;

    public Entry(char letter) {
      this.letter = letter;
    }
  }

  private final Entry[][] entries;
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
//    this.lineOffset = lineOffset;
    this.lineOffset = 0;
    this.charOffset = charOffset;
    this.entries = new Entry[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        entries[y][x] = new Entry(' ');
      }
    }
  }

  private Entry getEntry(int y, int x) {
    if (entries[y][x] == null) {
      throw new IllegalStateException("(" + y + ", " + x + ") accessed from "
          + entries.length + ", " + entries[0].length);
    }
    return entries[y][x];
  }

  public boolean isVisual(int y, int x) {
    return getEntry(y, x).isVisual;
  }

  public char getLetter(int y, int x) {
    return getEntry(y, x).letter;
  }

  public void setLetter(int y, int x, char letter) {
    getEntry(y, x).letter = letter;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public String getLine(int y) {
    StringBuffer buffer = new StringBuffer();
    for (Entry entry : entries[y]) {
      buffer.append(entry.letter);
    }
    return buffer.toString();
  }

  public void render(Graphics g) {
    drawBackground(g);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Entry entry = getEntry(y, x);
        int boxY = (lineOffset + y) * charHeightPx;
        int boxX = x * charWidthPx;
        if (entry.isWhitespaceIndicator) {
          g.setColor(Constants.WHITESPACE_INDICATOR_COLOR);
          g.fillRect(boxX + charWidthPx, boxY, 2, charHeightPx);
        }
        if (entry.is80CharIndicator) {
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
        Entry entry = getEntry(y, x);
        if (entry.isVisual) {
          rectFiller.nextColor(Constants.VISUAL_COLOR);
        } else if (entry.isHighlight || entry.isSearchHighlight) {
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

  public Entry get(int y, int x) {
    if (isInBounds(y, x)) {
      return getEntry(y, x);
    }
    // NOTE(koz): Should this throw an exception instead?
    return new Entry(' ');
  }

  private boolean isInBounds(int y, int x) {
    if (entries.length == 0) {
      return false;
    }
    return y >= 0 && y < entries.length && x >= 0 && x < entries[0].length;
  }
}
