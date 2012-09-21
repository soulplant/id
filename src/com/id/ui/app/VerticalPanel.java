package com.id.ui.app;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.id.ui.Constants;
import com.id.ui.VerticalListLayout;

@SuppressWarnings("serial")
public class VerticalPanel extends JPanel {
  public VerticalPanel(int verticalPaddingPx) {
    setLayout(new VerticalListLayout(verticalPaddingPx, 0));
    setBackground(Constants.BG_COLOR);
  }
}
