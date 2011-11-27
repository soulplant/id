package com.id.rendering;

import static org.junit.Assert.*;

import java.awt.Rectangle;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Editor;
import com.id.editor.Visual;
import com.id.file.File;
import com.id.file.FileView;

public class EditorRendererTest {

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
    editor.setHighlight("a");
    renderMatrix(new Rectangle(0, 0, 20, 10), 10, 10);
    assertMatrixContents("ab");
    assertTrue(matrix.isHighlight(0, 0));
    assertFalse(matrix.isHighlight(0, 1));
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


  private File file;
  private FileView fileView;
  private Editor editor;
  private EditorRenderer renderer;
  private Matrix matrix;

  @Before
  public void init() {
    setFileContents();
  }

  private void setFileContents(String... lines) {
    file = new File(lines);
    fileView = new FileView(file);
    editor = new Editor(fileView);
  }
}
