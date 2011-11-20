package com.id.rendering;

import java.awt.Rectangle;

import com.id.editor.Editor;

public class EditorRenderer {
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
    final int offsetYPx = screen.y % fontHeightPx;
    final int offsetXPx = screen.x % fontWidthPx;
    final int extraLettersY = offsetYPx > 0 ? 1 : 0;
    final int extraLettersX = offsetXPx > 0 ? 1 : 0;
    int letterX = screen.x / fontWidthPx;
    int letterY = screen.y / fontHeightPx;
    int lettersWide = screen.width / fontWidthPx + extraLettersX;
    int lettersHigh = screen.height / fontHeightPx + extraLettersY;
    final int linesToDraw = Math.min(lettersHigh, editor.getLineCount() - letterY);

    Matrix matrix = new Matrix(linesToDraw, lettersWide, fontDescentPx, fontHeightPx, offsetYPx, offsetXPx);

    for (int i = 0; i < matrix.getHeight(); i++) {
      int lineY = letterY + i;
      setLine(i, letterX, lettersWide, matrix, editor.getLine(lineY));
    }
    return matrix;
  }

  private String safeSubstring(String line, int x, int length) {
    int endIndex = Math.min(line.length(), x + length);
    return line.substring(x, endIndex);
  }

  private void setLine(int y, int startX, int length, Matrix matrix, String line) {
    line = safeSubstring(line, startX, length);
    for (int i = 0; i < Math.min(matrix.getWidth(), line.length()); i++) {
      matrix.setLetter(y, i, line.charAt(i));
    }
  }
}
