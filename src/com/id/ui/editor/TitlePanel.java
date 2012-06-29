package com.id.ui.editor;

import java.awt.Graphics;

import javax.swing.JLabel;

import com.id.app.App;
import com.id.editor.Editor;
import com.id.ui.Constants;

@SuppressWarnings("serial")
public class TitlePanel extends JLabel {

  public TitlePanel(Editor editor) {
    setText(editor.getFilename());
    setBackground(Constants.BG_COLOR);
  }

  @Override
  public void paint(Graphics g) {
    App.configureFont(g);
    super.paint(g);
  }
}
