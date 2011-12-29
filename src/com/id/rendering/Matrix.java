package com.id.rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Matrix {
  private final ArrayList<Slug> slugs = new ArrayList<Slug>();
  private final int height;
  private final int width;
  private final int charHeightPx;
  private final int fontDescentPx;
  private final int lineOffset;
  private final int charOffset;
  private final int charWidthPx;

  public Matrix(int height, int width, int fontDescentPx, int charHeightPx, int charWidthPx, int lineOffset, int charOffset) {
    this.height = height;
    this.width = width;
    this.fontDescentPx = fontDescentPx;
    this.charHeightPx = charHeightPx;
    this.charWidthPx = charWidthPx;
    this.lineOffset = lineOffset;
    this.charOffset = charOffset;
    for (int y = 0; y < height; y++) {
      slugs.add(new Slug(width));
    }
  }

  public boolean isVisual(int y, int x) {
    return slugs.get(y).isVisual(x);
  }

  public char getLetter(int y, int x) {
    return slugs.get(y).getLetter(x);
  }

  public void setLetter(int y, int x, char letter) {
    slugs.get(y).setLetter(x, letter);
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public String getLine(int y) {
    return slugs.get(y).getString();
  }

  public void render(Graphics g) {
    for (int y = 0; y < height; y++) {
      Slug slug = slugs.get(y);
      for (int x = 0; x < slug.getLength(); x++) {
        int boxY = (lineOffset + y) * charHeightPx;
        int boxX = x * charWidthPx;
        if (slug.isVisual(x)) {
          g.setColor(Color.GRAY);
          g.fillRect(boxX, boxY, charWidthPx, charHeightPx);
        } else if (slug.isHighlight(x) || slug.isSearchHighlight(x)) {
          g.setColor(Color.CYAN);
          g.fillRect(boxX, boxY, charWidthPx, charHeightPx);
        }
        if (slug.isWhitespaceIndicator(x)) {
          g.setColor(Color.GRAY);
          g.fillRect(boxX + charWidthPx, boxY, 2, charHeightPx);
        }
      }
      // NOTE drawString() takes the bottom y coordinate of the rect to draw the text in.
      int textY = (lineOffset + y + 1) * charHeightPx - fontDescentPx;
      g.setColor(Color.black);
      g.drawString(slug.getString(), charOffset * charWidthPx, textY);
    }
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for (int y = 0; y < height; y++) {
      buffer.append("[");
      for (int x = 0; x < width; x++) {
        buffer.append(slugs.get(y).getLetter(x));
      }
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

  public void setVisual(int y, int x, boolean visual) {
    slugs.get(y).setVisual(x, visual);
  }

  public boolean isHighlight(int y, int x) {
    return slugs.get(y).isHighlight(x);
  }

  public void setHighlight(int y, int x, boolean highlight) {
    slugs.get(y).setHighlight(x, highlight);
  }

  public void setSearchHighlight(int y, int x, boolean searchHighlight) {
    slugs.get(y).setSearchHighlight(x, searchHighlight);
  }

  public boolean isSearchHighlight(int y, int x) {
    return slugs.get(y).isSearchHighlight(x);
  }

  public void setWhitespaceIndicator(int y, int x, boolean b) {
    slugs.get(y).setWhitespaceIndicator(x, b);
  }

  public boolean isWhitespaceIndicator(int y, int x) {
    return slugs.get(y).isWhitespaceIndicator(x);
  }
}
