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
    App.configureFont(g);
    int fontHeightPx = getFontHeightPx();
    int fontWidthPx = getFontWidthPx();

    int clipHeight = g.getClipBounds().height;
    int startY = g.getClipBounds().y / getFontHeightPx();
    int linesToRender =
        (clipHeight + getFontHeightPx() - 1) / getFontHeightPx();
    int maxLine =
        Math.min(linesToRender + startY, editor.getLineCountForMode());
    Editor.Iterator it = editor.getIterator(startY);

    for (int offset = 0; offset < maxLine && !it.done();
        offset++, it.next()) {
      Color color = g.getColor();
      boolean draw = true;
      int y = (startY + offset) * fontHeightPx;
      if (it.isInGrave()) {
        g.setColor(Constants.MARKER_REMOVED_COLOR);
        g.fillRect(0, y, fontWidthPx, fontHeightPx);
        continue;
      }
      Tombstone.Status status = editor.getStatus(it.getY());
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
        g.fillRect(0, y, fontWidthPx, fontHeightPx);
      }
      if (!editor.getGrave(it.getY()).isEmpty()) {
        g.setColor(Constants.MARKER_REMOVED_COLOR);
        g.fillRect(0, y + fontHeightPx - 5, fontWidthPx, 5);
      }
      if ((startY + offset) == 0 && !editor.getGrave(-1).isEmpty()) {
        g.setColor(Color.RED);
        g.fillRect(0, y, fontWidthPx, 5);
      }
      g.setColor(color);
    }
  }
}
