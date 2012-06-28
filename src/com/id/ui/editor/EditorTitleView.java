package com.id.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.id.app.App;
import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.app.LinewisePanel;

@SuppressWarnings("serial")
public class EditorTitleView extends LinewisePanel {
  private final Editor editor;
  private final ListModel<Editor> editors;

  public EditorTitleView(Editor editor, ListModel<Editor> editors) {
    this.editor = editor;
    this.editors = editors;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setFont(App.FONT);
    g.setColor(isFocused() ? Color.BLACK: Color.GRAY);
    g.drawString(getTitle(), 0, getFontHeightPx() - 3);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(getTitle().length() * getFontWidthPx(), getFontHeightPx());
  }

  private boolean isFocused() {
    return editors.isFocused() && editors.isFocused(editor);
  }

  private String getTitle() {
    String prefix = "";
    if (editor.isDogEared()) {
      prefix = editor.isModified() ? "o" : ".";
    } else {
      prefix = editor.isModified() ? "*" : "";
    }
    return prefix + " " + editor.getFilename();
  }
}
