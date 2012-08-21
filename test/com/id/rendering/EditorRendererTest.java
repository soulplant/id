package com.id.rendering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;

import org.junit.Test;

import com.id.editor.Patterns;
import com.id.editor.Visual;
import com.id.events.KeyStroke;
import com.id.test.EditorTestBase;

public class EditorRendererTest extends EditorTestBase {

  @Test
  public void simple() {
    setFileContents("abcdef");
    renderMatrix(new Rectangle(0, 0, 10, 10), 10, 10);
    assertMatrixContents("a");
  }

  @Test
  public void multiLine() {
    setFileContents("abc", "def");
    renderMatrix(new Rectangle(10, 10, 20, 20), 10, 10);
    assertMatrixContents("ef");
    assertEquals(1, matrix.getLineOffset());
    assertEquals(1, matrix.getCharOffset());
  }

  @Test
  public void notAlignedWithScreen() {
    setFileContents("abc", "def");
    renderMatrix(new Rectangle(5, 5, 20, 20), 10, 10);
    assertMatrixContents("abc", "def");
  }

  @Test
  public void outOfBoundsForFile() {
    setFileContents("abc", "de", "ghi");
    renderMatrix(new Rectangle(5, 5, 20, 20), 10, 10);
    assertMatrixContents("abc", "de ", "ghi");
  }

  @Test
  public void skipFirstLine() {
    setFileContents("abc", "def");
    renderMatrix(new Rectangle(0, 10, 10, 10), 10, 10);
    assertMatrixContents("d");
    assertEquals(1, matrix.getLineOffset());
  }

  @Test
  public void checkPaintingGlitch() {
    setFileContents("abc", "def", "ghi");
    renderMatrix(new Rectangle(1, 9, 1, 2), 10, 10);
    assertMatrixContents("a", "d");
    assertEquals(0, matrix.getLineOffset());
    assertEquals(0, matrix.getCharOffset());
  }

  @Test
  public void onePixelCase() {
    setFileContents("abc");
    renderMatrix(new Rectangle(0, 0, 1, 1), 10, 10);
    assertMatrixContents("a");
  }

  @Test
  public void checkVisual() {
    setFileContents("abc");
    editor.toggleVisual(Visual.Mode.CHAR);
    renderMatrix(new Rectangle(0, 0, 1, 1), 10, 10);
    assertMatrixContents("a");
    assertTrue(matrix.isVisual(0, 0));
  }

  @Test
  public void checkHighlight() {
    setFileContents("ab");
    editor.setHighlightPattern(Patterns.partWord("a"));
    renderMatrix(new Rectangle(0, 0, 20, 10), 10, 10);
    assertMatrixContents("ab");
    assertTrue(matrix.get(0, 0).isHighlight);
    assertFalse(matrix.get(0, 1).isHighlight);
  }

  @Test
  public void checkSearch() {
    setFileContents("abc");
    editor.enterSearch();
    editor.handleSearchKeyStroke(KeyStroke.fromChar('a'));
    renderMatrix(new Rectangle(0, 0, 20, 20), 10, 10);
    assertTrue(matrix.get(0, 0).isSearchHighlight);
  }

  @Test
  public void testTrailingWhitespaceIndicator() {
    setFileContents("abc  ");
    renderMatrix(new Rectangle(0, 0, 50, 50), 10, 10);
    assertTrue(matrix.get(0, 4).isWhitespaceIndicator);
  }

  @Test
  public void test80CharsIndicator() {
    String text = "a";
    for (int i = 0; i < 7; i++) {
      text += text;
    }
    setFileContents(text);
    renderMatrix(new Rectangle(0, 0, 810, 810), 10, 10);
    assertTrue(matrix.get(0, EditorRenderer.MAX_LINE_LENGTH - 1).is80CharIndicator);
  }

  @Test
  public void testVisualPastEndOfFile() {
    setFileContents("a");
    typeString("v$");
    renderMatrix(new Rectangle(0, 0, 20, 10), 10, 10);
    assertTrue(matrix.get(0, 1).isVisual);
  }

  private void renderMatrix(Rectangle screen, int fontWidthPx, int fontHeightPx) {
    renderer = new EditorRenderer(editor, screen, fontWidthPx, fontHeightPx, 0);
    matrix = renderer.render();
  }

  private void assertMatrixContents(String... lines) {
    assertEquals(lines.length, matrix.getHeight());
    assertEquals(lines[0].length(), matrix.getWidth());
    for (int y = 0; y < lines.length; y++) {
      assertEquals(lines[y], matrix.getLine(y));
    }
  }
  private EditorRenderer renderer;
  private Matrix matrix;
}
