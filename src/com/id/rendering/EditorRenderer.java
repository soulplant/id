package com.id.rendering;

import java.awt.Rectangle;

import com.id.editor.Editor;

public class EditorRenderer {
  public static final int MAX_LINE_LENGTH = 80;
  private final int fontWidthPx;
  private final int fontHeightPx;
  private final Rectangle screen;
  private final Editor editor;
  private final int fontDescentPx;

  public EditorRenderer(Editor editor, Rectangle screen, int fontWidthPx, int fontHeightPx, int fontDescentPx) {
    this.editor = editor;
    this.screen = screen;
    this.fontWidthPx = fontWidthPx;
    this.fontHeightPx = fontHeightPx;
    this.fontDescentPx = fontDescentPx;
  }

  public Matrix render() {
    int startX = screen.x / fontWidthPx;
    int startY = screen.y / fontHeightPx;
    int endX = (screen.x + screen.width - 1) / fontWidthPx;
    int endY = (screen.y + screen.height - 1) / fontHeightPx;

    int linesToDraw = endY - startY + 1;
    int charsToDraw = endX - startX + 1;

    return matrix(startY, startX, linesToDraw, charsToDraw);
  }

  private Matrix matrix(int startY, int startX, int linesToDraw, int charsToDraw) {
    int height = Math.min(linesToDraw, editor.getLineCount() - startY);
    int width = charsToDraw;
    Matrix matrix = new Matrix(height, width, fontDescentPx, fontHeightPx, fontWidthPx, startY, startX);
    for (int i = 0; i < height; i++) {
      int lineY = startY + i;
      setLine(i, startX, charsToDraw, matrix, lineY);
    }
    return matrix;
  }

  private String safeSubstring(String line, int x, int length) {
    if (x < 0) {
      throw new IllegalStateException("x should be >= 0");
    }
    int endIndex = Math.min(line.length(), x + length);
    int startIndex = Math.max(0, x);
    return line.substring(startIndex, endIndex);
  }

  private void setLine(int matrixY, int startX, int length, Matrix matrix, int lineY) {
    String line = editor.getLine(lineY);
    line = safeSubstring(line, startX, length);
    if (hasTrailingWhitespace(line)) {
      matrix.setWhitespaceIndicator(matrixY, line.length() - 1, true);
    }
    if (line.length() > MAX_LINE_LENGTH) {
      matrix.set80CharIndicator(matrixY, MAX_LINE_LENGTH - 1, true);
    }
    for (int i = 0; i < Math.min(matrix.getWidth(), line.length()); i++) {
      matrix.setLetter(matrixY, i, line.charAt(i));
      if (editor.isInVisual(lineY, i)) {
        matrix.setVisual(matrixY, i, true);
      }
      if (editor.isHighlight(lineY, i)) {
        matrix.setHighlight(matrixY, i, true);
      }
      if (editor.isSearchHighlight(lineY, i)) {
        matrix.setSearchHighlight(matrixY, i, true);
      }
    }
  }

  private boolean hasTrailingWhitespace(String line) {
    if (line.isEmpty()) {
      return false;
    }
    return Character.isWhitespace(line.charAt(line.length() - 1));
  }
}
