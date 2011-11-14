package com.id.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.file.Tombstone;

@SuppressWarnings("serial")
public class MarkerPanel extends JPanel {
  private final Editor editor;

  public MarkerPanel(Editor editor) {
    this.editor = editor;
    setPreferredSize(new Dimension(10, 768));
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setFont(App.FONT);
    int fontHeightPx = g.getFontMetrics().getHeight();
    int widthPx = g.getClipBounds().width;
    int height = Math.min(editor.getLineCount(), g.getClipBounds().height / fontHeightPx);

    for (int i = 0; i < height; i++) {
      Color color = g.getColor();
      boolean draw = true;
      Tombstone.Status status = editor.getStatus(i);
      switch (status) {
      case MODIFIED:
        g.setColor(Color.ORANGE);
        break;
      case NEW:
        g.setColor(Color.GREEN);
        break;
      case NORMAL:
        draw = false;
        break;
      }
      if (draw) {
        g.fillRect(0, i * fontHeightPx, widthPx, fontHeightPx);
      }
      if (!editor.getGrave(i).isEmpty()) {
        g.setColor(Color.RED);
        g.fillRect(0, (i + 1) * fontHeightPx - 5, widthPx - 1, fontHeightPx);
      }
      g.setColor(color);
    }
  }
}
