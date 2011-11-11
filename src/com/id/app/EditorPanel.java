package com.id.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import com.id.editor.Editor;
import com.id.editor.Point;
import com.id.events.EditorKeyHandler;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel {
  private final Editor editor;

  public EditorPanel(Editor editor) {
    this.editor = editor;
    setPreferredSize(new Dimension(600, 768));
    setFont(new Font("system", Font.PLAIN, 14));
  }

  @Override
  public void setSize(Dimension d) {
    super.setSize(d);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setFont(new Font("Monospaced.plain", Font.PLAIN, 14));
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int fontDescentPx = g.getFontMetrics().getDescent();
    int fontWidthPx = g.getFontMetrics().getWidths()[70];
    int fontHeightPx = g.getFontMetrics().getHeight();
    int height = (int) Math.floor(g.getClipBounds().getHeight() / fontHeightPx);
    int width = (int) Math.floor(g.getClipBounds().getWidth() / fontWidthPx);

    Point point = editor.getCursorPosition();
    int cursorWidthPx = editor.isInInsertMode() ? 2 : fontWidthPx;
    g.drawRect(point.getX() * fontWidthPx, point.getY() * fontHeightPx, cursorWidthPx, fontHeightPx);
    for (int y = 0; y < height && y < editor.getLineCount(); y++) {
      for (int x = 0; x < width && x < editor.getLine(y).length(); x++) {
        if (editor.isInVisual(y, x)) {
          Color color = g.getColor();
          g.setColor(Color.GRAY);
          g.fillRect(x * fontWidthPx, y * fontHeightPx - fontDescentPx, fontWidthPx, fontHeightPx + fontDescentPx);
          g.setColor(color);
        }
      }
      g.drawString(editor.getLine(y), 0, (y + 1) * fontHeightPx - fontDescentPx);
    }
    g.drawRect(0, 0, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
  }

  public boolean handleKeyPress(KeyEvent e) {
    return new EditorKeyHandler().handleKeyPress(e, editor);
  }

  public String getFilename() {
    return editor.getFilename();
  }

  public Editor getEditor() {
    return editor;
  }
}
