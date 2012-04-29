package com.id.ui.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Box;
import javax.swing.JPanel;

import com.id.app.App;
import com.id.app.Constants;
import com.id.app.ListModel;
import com.id.editor.Editor;

@SuppressWarnings("serial")
class FileListEntryView extends JPanel {
  private static final int BOTTOM_PADDING_PX = 3;
  private static final int LEFT_PADDING_PX = 3;

  private final Editor editor;
  private final boolean focused;

  public FileListEntryView(Editor editor, boolean focused) {
    this.editor = editor;
    this.focused = focused;
    setPreferredSize(new Dimension(200, Constants.CHAR_HEIGHT_PX));
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);
    int leftDraw = LEFT_PADDING_PX;
    if (editor.isModified()) {
      g.drawString("*", leftDraw, Constants.CHAR_HEIGHT_PX - BOTTOM_PADDING_PX);
    }
    leftDraw += Constants.CHAR_WIDTH_PX + LEFT_PADDING_PX;
    if (editor.getHighlightMatchCount() > 0) {
      g.setColor(Color.CYAN);
      g.fillRect(leftDraw, 0, Constants.CHAR_WIDTH_PX, Constants.CHAR_HEIGHT_PX - 1);
    }
    leftDraw += Constants.CHAR_WIDTH_PX + LEFT_PADDING_PX;
    if (!editor.isMarkersClear()) {
      g.setColor(Color.ORANGE);
      g.fillRect(leftDraw, 0, Constants.CHAR_WIDTH_PX, Constants.CHAR_HEIGHT_PX - 1);
    }
    leftDraw += Constants.CHAR_WIDTH_PX + LEFT_PADDING_PX;
    g.setColor(Color.BLACK);
    g.drawString(editor.getFilename(),
        leftDraw,
        Constants.CHAR_HEIGHT_PX - BOTTOM_PADDING_PX);
    if (focused) {
      g.drawRect(0, 0, g.getClipBounds().width - 1, Constants.CHAR_HEIGHT_PX - 1);
    }
  }
}

@SuppressWarnings("serial")
public class FileListView extends JPanel implements ListModel.Listener<Editor> {
  private final ListModel<Editor> editors;
  private final Box box;

  public FileListView(ListModel<Editor> editors) {
    this.editors = editors;
    box = Box.createVerticalBox();
    add(box);
  }

  private void updateItems() {
    box.removeAll();
    int i = 0;
    for (Editor editor : editors) {
      box.add(new FileListEntryView(editor, editors.getFocusedIndex() == i));
      i++;
    }
  }

  @Override
  public void onAdded(int i, Editor t) {
    updateItems();
  }

  @Override
  public void onSelectionChanged(int i, Editor t) {
    updateItems();
  }

  @Override
  public void onRemoved(int i, Editor t) {
    updateItems();
  }

  @Override
  public void onSelectionLost() {
    updateItems();
  }

  @Override
  public void onFocusChanged(boolean isFocused) {
    // File list can't have focus.
  }
}
