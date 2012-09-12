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
    // TODO(koz): Track down why we need this.
    height = Math.max(0, height);  // Don't allow negative height.
    int width = charsToDraw;
    Matrix matrix = new Matrix(height, width, fontDescentPx, fontHeightPx, fontWidthPx, startY, startX);
    for (int i = 0; i < height; i++) {
      int lineY = startY + i;
      setLine(i, startX, charsToDraw, matrix, lineY);
    }
    return matrix;
  }

  private int cap(int x, int low, int high) {
    return Math.min(high, Math.max(low, x));
  }

  private String safeSubstring(String line, int x, int length) {
    int endIndex = cap(x + length, 0, line.length());
    int startIndex = cap(x, 0, line.length());
    return line.substring(startIndex, endIndex);
  }

  private String paddedString(String line, int x, int length, char padding) {
    StringBuffer buffer = new StringBuffer();
    String substring = safeSubstring(line, x, length);
    buffer.append(substring);
    for (int i = 0; i < length - substring.length(); i++) {
      buffer.append(padding);
    }
    return buffer.toString();
  }

  private void setLine(int matrixY, int startX, int length, Matrix matrix, int lineY) {
    String line = editor.getLine(lineY);
    String substring = paddedString(line, startX, length, ' ');
    if (hasTrailingWhitespace(line)) {
      matrix.get(matrixY, line.length() - 1).isWhitespaceIndicator = true;
    }
    if (line.length() > MAX_LINE_LENGTH && MAX_LINE_LENGTH <= matrix.getWidth()) {
      matrix.get(matrixY, MAX_LINE_LENGTH - 1).is80CharIndicator = true;
    }
    for (int i = 0; i < Math.min(matrix.getWidth(), line.length() + 1); i++) {
      matrix.get(matrixY, i).letter = substring.charAt(i);
      if (editor.isInVisual(lineY, i)) {
        matrix.get(matrixY, i).isVisual = true;
      }
      if (editor.isHighlight(lineY, i)) {
        matrix.get(matrixY, i).isHighlight = true;
      }
      if (editor.isSearchHighlight(lineY, i)) {
        matrix.get(matrixY, i).isSearchHighlight = true;
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
