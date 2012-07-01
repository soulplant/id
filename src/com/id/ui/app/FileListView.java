package com.id.ui.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JScrollPane;

import com.id.app.App;
import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.Constants;

@SuppressWarnings("serial")
class FileListEntryView extends LinewisePanel {
  private static final int TOP_PADDING_PX = 9;
  private static final int BOTTOM_PADDING_PX = 13;
  private static final int LEFT_PADDING_PX = 3;
  private static final int PADDED_ELEMENT_COUNT = 3;
  private static final int RIBBON_WIDTH_PX = 2;
  private static final int RIBBON_PADDING_PX = 4;

  private final Editor editor;
  private boolean focused;

  public FileListEntryView(Editor editor, boolean focused) {
    this.editor = editor;
    this.focused = focused;
    setPreferredSize(new Dimension(getPreferredWidth(), getPreferredHeight()));
  }

  public void setFocused(boolean focused) {
    this.focused = focused;
    invalidate();
  }

  private int getPreferredWidth() {
    int filenameLengthPx = editor.getBaseFilename().length() * getFontWidthPx();
    int leftPaddingPx = LEFT_PADDING_PX + PADDED_ELEMENT_COUNT * (getFontWidthPx() + LEFT_PADDING_PX);
    int rightPaddingPx = 2 * LEFT_PADDING_PX;
    return leftPaddingPx + filenameLengthPx + rightPaddingPx;
  }

  private int getPreferredHeight() {
    return getFontHeightPx() + TOP_PADDING_PX + BOTTOM_PADDING_PX;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);
    if (focused) {
      int textAreaLeft = LEFT_PADDING_PX + getFontWidthPx() + LEFT_PADDING_PX +
          RIBBON_WIDTH_PX + RIBBON_PADDING_PX + RIBBON_WIDTH_PX;
      g.setColor(Constants.SELECTED_FILE_LIST_COLOR);
      g.fillRect(textAreaLeft, 0, getWidth() - textAreaLeft, getHeight());
    }
    int leftDraw = LEFT_PADDING_PX;
    if (editor.isModified()) {
      g.setColor(Constants.TEXT_COLOR);
      g.drawString("*", leftDraw, getHeight() - BOTTOM_PADDING_PX);
    }
    leftDraw += getFontWidthPx() + LEFT_PADDING_PX;
    if (editor.getHighlightMatchCount() > 0) {
      g.setColor(Constants.HIGHLIGHT_COLOR);
      g.fillRect(leftDraw, 0, RIBBON_WIDTH_PX, getHeight());
    }
    leftDraw += RIBBON_WIDTH_PX + RIBBON_PADDING_PX;
    if (!editor.isMarkersClear()) {
      g.setColor(Constants.FILE_MODIFIED_COLOR);
      g.fillRect(leftDraw, 0, RIBBON_WIDTH_PX, getHeight());
    }
    leftDraw += getFontWidthPx() + LEFT_PADDING_PX;
    g.setColor(Constants.TEXT_COLOR);
    g.drawString(editor.getBaseFilename(),
        leftDraw,
        getHeight() - BOTTOM_PADDING_PX);
  }
}

@SuppressWarnings("serial")
public class FileListView extends LinewisePanel implements ListModel.Listener<Editor> {
  private static final int MIN_WIDTH_PX = 200;
  private static final int MAX_WIDTH_PX = 400;
  private static final int FILE_LIST_ENTRY_PADDING_PX = 2;

  private final ListModel<Editor> editors;
  private int minWidth;

  private final VerticalPanel panel;
  private final JScrollPane scrollPane;

  public FileListView(ListModel<Editor> editors) {
    this.editors = editors;
    this.panel = new VerticalPanel(FILE_LIST_ENTRY_PADDING_PX);
    this.scrollPane = new JScrollPane(panel);
    setLayout(new GridLayout(1, 1));

    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane);
  }

  private void updatePreferredSize() {
    minWidth = MIN_WIDTH_PX;
    for (int i = 0; i < panel.getComponentCount(); i++) {
      minWidth = Math.max(minWidth, panel.getComponent(i).getPreferredSize().width);
    }
    minWidth = Math.min(MAX_WIDTH_PX, minWidth);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(minWidth, editors.size() * getFontHeightPx());
  }

  @Override
  public void onAdded(int i, Editor editor) {
    panel.add(new FileListEntryView(editor, editors.isFocused(editor)), i);
    updatePreferredSize();
  }

  @Override
  public void onSelectionChanged(int i, Editor t) {
    for (int j = 0; j < panel.getComponentCount(); j++) {
      FileListEntryView c = (FileListEntryView) panel.getComponent(j);
      boolean focused = j == i;
      c.setFocused(focused);
      if (focused) {
        panel.scrollRectToVisible(c.getBounds());
      }
    }
  }

  @Override
  public void onRemoved(int i, Editor t) {
    panel.remove(i);
    updatePreferredSize();
  }

  @Override
  public void onSelectionLost() {
    onSelectionChanged(-1, null);
  }

  @Override
  public void onFocusChanged(boolean isFocused) {
    // File list can't have focus.
  }
}
