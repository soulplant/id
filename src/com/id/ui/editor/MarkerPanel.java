package com.id.ui.editor;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.file.Tombstone;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class MarkerPanel extends LinewisePanel {
  private final Editor editor;

  public MarkerPanel(Editor editor) {
    this.editor = editor;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setFont(App.FONT);
    // TODO Make this only render things in the clip rect.
    int fontHeightPx = getFontHeightPx();
    int widthPx = getFontWidthPx();
    int height = editor.getLineCount();

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
        g.fillRect(0, (i + 1) * fontHeightPx - 5, widthPx, 5);
      }
      if (i == 0 && !editor.getGrave(-1).isEmpty()) {
        g.setColor(Color.RED);
        g.fillRect(0, i * fontHeightPx, widthPx, 5);
      }
      g.setColor(color);
    }
  }
}
