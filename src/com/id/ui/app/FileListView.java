package com.id.ui.app;

import java.awt.Dimension;
import java.awt.Graphics;

import com.id.app.App;
import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.Constants;

@SuppressWarnings("serial")
class FileListEntryView extends LinewisePanel {
  private static final int BOTTOM_PADDING_PX = 3;
  private static final int LEFT_PADDING_PX = 3;
  private static final int PADDED_ELEMENT_COUNT = 3;

  private final Editor editor;
  private final boolean focused;

  public FileListEntryView(Editor editor, boolean focused) {
    this.editor = editor;
    this.focused = focused;
    setPreferredSize(new Dimension(getPreferredWidth(), getFontHeightPx()));
  }

  private int getPreferredWidth() {
    int filenameLengthPx = editor.getBaseFilename().length() * getFontWidthPx();
    int leftPaddingPx = LEFT_PADDING_PX + PADDED_ELEMENT_COUNT * (getFontWidthPx() + LEFT_PADDING_PX);
    int rightPaddingPx = 2 * LEFT_PADDING_PX;
    return leftPaddingPx + filenameLengthPx + rightPaddingPx;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);
    int leftDraw = LEFT_PADDING_PX;
    if (editor.isModified()) {
      g.drawString("*", leftDraw, getFontHeightPx() - BOTTOM_PADDING_PX);
    }
    leftDraw += getFontWidthPx() + LEFT_PADDING_PX;
    if (editor.getHighlightMatchCount() > 0) {
      g.setColor(Constants.HIGHLIGHT_COLOR);
      g.fillRect(leftDraw, 0, getFontWidthPx(), getFontHeightPx() - 1);
    }
    leftDraw += getFontWidthPx() + LEFT_PADDING_PX;
    if (!editor.isMarkersClear()) {
      g.setColor(Constants.FILE_MODIFIED_COLOR);
      g.fillRect(leftDraw, 0, getFontWidthPx(), getFontHeightPx() - 1);
    }
    leftDraw += getFontWidthPx() + LEFT_PADDING_PX;
    g.setColor(focused ? Constants.TEXT_COLOR : Constants.FADED_TEXT_COLOR);
    g.drawString(editor.getBaseFilename(),
        leftDraw,
        getFontHeightPx() - BOTTOM_PADDING_PX);
  }
}

@SuppressWarnings("serial")
public class FileListView extends LinewisePanel implements ListModel.Listener<Editor> {
  private static final int MIN_WIDTH_PX = 200;
  private static final int MAX_WIDTH_PX = 400;

  private final ListModel<Editor> editors;
  private int minWidth;

  public FileListView(ListModel<Editor> editors) {
    this.editors = editors;
    setLayout(new StackLayout());
  }

  private void updateItems() {
    minWidth = MIN_WIDTH_PX;
    removeAll();
    int i = 0;
    for (Editor editor : editors) {
      FileListEntryView entryView = new FileListEntryView(editor, editors.getFocusedIndex() == i);
      add(entryView);
      minWidth = Math.max(minWidth, entryView.getPreferredSize().width);
      i++;
    }
    minWidth = Math.min(MAX_WIDTH_PX, minWidth);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(minWidth, editors.size() * getFontHeightPx());
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
