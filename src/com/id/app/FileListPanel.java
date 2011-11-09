package com.id.app;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FileListPanel extends JPanel {
  public FileListPanel() {
    setPreferredSize(new Dimension(200, 100));
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.drawRect(0, 0, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
  }
}
