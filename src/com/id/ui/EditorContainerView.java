package com.id.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class EditorContainerView extends JPanel implements ViewContainer<EditorPanel> {
  public EditorContainerView() {
    setLayout(new BorderLayout());
  }

  @Override
  public void add(EditorPanel view) {
    super.add(view);
  }
}
