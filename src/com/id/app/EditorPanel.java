package com.id.app;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.id.editor.Editor;
import com.id.editor.Point;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel {
  private final Editor editor;

  public EditorPanel(Editor editor) {
    this.editor = editor;
    setPreferredSize(new Dimension(600, 768));
    System.out.println(getFont());
    setFont(new Font("system", Font.PLAIN, 14));
  }

  @Override
  public void setSize(Dimension d) {
    super.setSize(d);
    System.out.println("setSize(" + d + ")");
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setFont(new Font("Monospaced.plain", Font.PLAIN, 14));
    int fontDescentPx = g.getFontMetrics().getDescent();
    int fontWidthPx = g.getFontMetrics().getWidths()[70];
    int fontHeightPx = g.getFontMetrics().getHeight();
    int height = (int) Math.floor(g.getClipBounds().getHeight() / g.getFontMetrics().getHeight());

    Point point = editor.getCursorPosition();
    int cursorWidthPx = editor.isInInsertMode() ? 2 : fontWidthPx;
    g.drawRect(point.getX() * fontWidthPx, point.getY() * fontHeightPx, cursorWidthPx, fontHeightPx);
    for (int i = 0; i < height && i < editor.getLineCount(); i++) {
      g.drawString(editor.getLine(i), 0, (i + 1) * fontHeightPx - fontDescentPx);
    }
    g.drawRect(0, 0, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
  }
}
