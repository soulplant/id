package com.id.ui.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.events.EditorKeyHandler;
import com.id.file.File;
import com.id.rendering.EditorRenderer;
import com.id.rendering.Matrix;

@SuppressWarnings("serial")
public class TextPanel extends JPanel {
  private final Editor editor;

  public TextPanel(Editor editor) {
    this.editor = editor;
    setFont(new Font("system", Font.PLAIN, 14));
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
        // Do nothing.
      }
    });
  }

  private void updateSize() {
    //  final int fontWidthPx = getFontMetrics(getFont()).getWidths()[70];
    //  final int fontHeightPx = getFontMetrics(getFont()).getHeight();
    // TODO Make these not static.
    final int fontWidthPx = 8;
    final int fontHeightPx = 14;

    setPreferredSize(new Dimension(fontWidthPx, fontHeightPx * editor.getLineCount()));
    invalidate();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);

    final int fontDescentPx = g.getFontMetrics().getDescent();
    final int fontWidthPx = g.getFontMetrics().getWidths()[70];
    final int fontHeightPx = g.getFontMetrics().getHeight();

    EditorRenderer renderer = new EditorRenderer(editor, g.getClipBounds(), fontWidthPx, fontHeightPx, fontDescentPx);
    Matrix matrix = renderer.render();
    matrix.render(g);
    Point point = editor.getCursorPosition();
    int cursorYPx = point.getY() * fontHeightPx;
    int cursorXPx = point.getX() * fontWidthPx;
    int cursorWidthPx = editor.isInInsertMode() ? 2 : fontWidthPx;
    int cursorHeightPx = fontHeightPx;
    g.drawRect(cursorXPx, cursorYPx, cursorWidthPx, cursorHeightPx);
    drawOutlineBox(g);
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
    return new EditorKeyHandler().handleKeyPress(e, editor);
  }
}
