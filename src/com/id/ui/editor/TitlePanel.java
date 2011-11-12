package com.id.ui.editor;

import javax.swing.JLabel;

import com.id.editor.Editor;

@SuppressWarnings("serial")
public class TitlePanel extends JLabel {

  public TitlePanel(Editor editor) {
    setText(editor.getFilename());
  }
}
