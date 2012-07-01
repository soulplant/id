package com.id.ui.app;

import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class VerticalPanel extends JPanel {
  public VerticalPanel(int verticalPaddingPx) {
    setLayout(new StackLayout(verticalPaddingPx));
  }

  @Override
  public Dimension getPreferredSize() {
    int width = 0;
    int height = 0;
    for (int i = 0; i < getComponentCount(); i++) {
      Dimension preferredSize = getComponent(i).getPreferredSize();
      width = Math.max(width, preferredSize.width);
      height += preferredSize.height;
    }
    return new Dimension(Math.max(width, getParent().getWidth()), height);
  }
}
