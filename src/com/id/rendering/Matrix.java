package com.id.rendering;

import java.awt.Graphics;
import java.util.ArrayList;

public class Matrix {
  private final ArrayList<Slug> slugs = new ArrayList<Slug>();
  private final int height;
  private final int width;
  private final int offsetYPx;
  private final int offsetXPx;
  private final int lineHeightPx;
  private final int fontDescentPx;
  private final int lineOffset;

  public Matrix(int height, int width, int fontDescentPx, int lineHeightPx, int offsetYPx, int offsetXPx, int lineOffset) {
    this.height = height;
    this.width = width;
    this.fontDescentPx = fontDescentPx;
    this.lineHeightPx = lineHeightPx;
    this.offsetYPx = offsetYPx;
    this.offsetXPx = offsetXPx;
    this.lineOffset = lineOffset;
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

  public int getOffsetY() {
    return offsetYPx;
  }

  public int getOffsetX() {
    return offsetXPx;
  }

  public String getLine(int y) {
    return slugs.get(y).getString();
  }

  public void render(Graphics g) {
    for (int i = 0; i < height; i++) {
      Slug slug = slugs.get(i);
      // TODO Remove offsetYPx.
      int y = (lineOffset + i + 1) * lineHeightPx - fontDescentPx -  0 * offsetYPx;
      // NOTE drawString() takes the bottom y coordinate of the rect to draw the text in.
      g.drawString(slug.getString(), -offsetXPx, y);
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
}
