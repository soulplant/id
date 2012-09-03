package com.id.ui.editor;

import java.awt.Color;
import java.awt.Graphics;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.file.Tombstone;
import com.id.ui.Constants;
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
    int fontHeightPx = getFontHeightPx();
    int fontWidthPx = getFontWidthPx();
    int lineCount = editor.getLineCount();
    int linesToDraw = Math.min(getLinesHigh(), lineCount - getTopLine());

    for (int i = 0; i < linesToDraw; i++) {
      Color color = g.getColor();
      boolean draw = true;
      Tombstone.Status status = editor.getStatus(i + getTopLine());
      switch (status) {
      case MODIFIED:
        g.setColor(Constants.MARKER_MODIFIED_COLOR);
        break;
      case NEW:
        g.setColor(Constants.MARKER_ADDED_COLOR);
        break;
      case NORMAL:
        draw = false;
        break;
      }
      if (draw) {
        g.fillRect(0, i * fontHeightPx, fontWidthPx, fontHeightPx);
      }
      if (!editor.getGrave(i).isEmpty()) {
        g.setColor(Constants.MARKER_REMOVED_COLOR);
        g.fillRect(0, (i + 1) * fontHeightPx - 5, fontWidthPx, 5);
      }
      if (i == 0 && !editor.getGrave(-1).isEmpty()) {
        g.setColor(Color.RED);
        g.fillRect(0, i * fontHeightPx, fontWidthPx, 5);
      }
      g.setColor(color);
    }
  }
}
