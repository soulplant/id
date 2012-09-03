package com.id.ui.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.events.EditorKeyHandler;
import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.rendering.EditorRenderer;
import com.id.rendering.Matrix;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class TextPanel extends LinewisePanel {
  private final Editor editor;
  private final EditorKeyHandler handler = new EditorKeyHandler();

  public TextPanel(Editor editor) {
    this.editor = editor;
    editor.addFileListener(new File.Listener() {
      @Override
      public void onLineInserted(int y, String line) {
        updateSize();
      }

      @Override
      public void onLineRemoved(int y, String line) {
        updateSize();
      }

      @Override
      public void onLineChanged(int y, String oldLine, String newLine) {
        repaint();
      }
    });
    updateSize();
  }

  private void updateSize() {
    setPreferredSize(new Dimension(
        getFontWidthPx() + 1, getFontHeightPx() * editor.getLineCount() + 1));
    invalidate();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);

    final int fontDescentPx = g.getFontMetrics().getDescent();
    Rectangle rect = new Rectangle(0, getTopLine() * getFontHeightPx(),
        getLinesWide() * getFontWidthPx(), getLinesHigh() * getFontHeightPx());

    EditorRenderer renderer = new EditorRenderer(editor, rect,
        getFontWidthPx(), getFontHeightPx(), fontDescentPx);
    Matrix matrix = renderer.render();
    matrix.render(g);
    Point point = editor.getCursorPosition();
    int cursorYPx = (point.getY() - getTopLine()) * getFontHeightPx();
    int cursorXPx = point.getX() * getFontWidthPx();
    int cursorWidthPx = editor.isInInsertMode() ? 2 : getFontWidthPx();
    int cursorHeightPx = getFontHeightPx();
    if (editor.isFocused()) {
      if (editor.isInInsertMode()) {
        g.fillRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
      } else {
        g.drawRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
      }
    }
  }

  public boolean handleKeyPress(KeyEvent e) {
    return handler.handleKeyPress(KeyStroke.fromKeyEvent(e), editor);
  }
}
