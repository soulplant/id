package com.id.ui.editor;

import java.awt.Dimension;
import java.awt.Graphics;
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
    setPreferredSize(new Dimension(getFontWidthPx(), getFontHeightPx() * editor.getLineCount()));
    invalidate();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);

    final int fontDescentPx = g.getFontMetrics().getDescent();

    EditorRenderer renderer = new EditorRenderer(editor, g.getClipBounds(), getFontWidthPx(), getFontHeightPx(), fontDescentPx);
    Matrix matrix = renderer.render();
    matrix.render(g);
    Point point = editor.getCursorPosition();
    int cursorYPx = point.getY() * getFontHeightPx();
    int cursorXPx = point.getX() * getFontWidthPx();
    int cursorWidthPx = editor.isInInsertMode() ? 2 : getFontWidthPx();
    int cursorHeightPx = getFontHeightPx();
    if (editor.isInInsertMode()) {
      g.fillRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
    } else {
      g.drawRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
    }
    drawOutlineBox(g);
  }

  public int getTopLineVisible() {
    return getVisibleRect().y / getFontHeightPx();
  }

  private void drawOutlineBox(Graphics g) {
    int x = g.getClipBounds().x;
    int y = g.getClipBounds().y;
    int height = g.getClipBounds().height - 1;
    int width = g.getClipBounds().width - 1;
    int realWidth = getSize().width - 1;
    int realHeight = getSize().height - 1;
    if (x == 0) {
      g.drawLine(x, y, x, y + height);
    }
    if (x + width == realWidth) {
      g.drawLine(x + width, y, x + width, y + height);
    }
    if (y == 0) {
      g.drawLine(x, y, x + width, y);
    }
    if (y + height == realHeight) {
      g.drawLine(x, y + height, x + width, y + height);
    }
  }

  public boolean handleKeyPress(KeyEvent e) {
    return handler.handleKeyPress(KeyStroke.fromKeyEvent(e), editor);
  }
}
