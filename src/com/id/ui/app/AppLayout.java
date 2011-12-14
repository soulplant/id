package com.id.ui.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class AppLayout implements LayoutManager {
  Component filelist = null;
  Component spotlight = null;
  Component stack = null;
  @Override
  public void addLayoutComponent(String name, Component component) {
    if (name.equals("filelist")) {
      filelist = component;
    } else if (name.equals("spotlight")) {
      spotlight = component;
    } else if (name.equals("stack")) {
      stack = component;
    }
    component.repaint();
  }

  @Override
  public void layoutContainer(Container parent) {
    int height = parent.getHeight();
    int fileListWidth = filelist.getPreferredSize().width;
    int remainingWidth = parent.getWidth() - fileListWidth;
    int editorWidth = remainingWidth / 2;
    filelist.setBounds(0, 0, fileListWidth, height);
    spotlight.setBounds(fileListWidth, 0, editorWidth, height);
    stack.setBounds(fileListWidth + editorWidth, 0, editorWidth, height);
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    return new Dimension(800, 600);
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    return parent.getSize();
  }

  @Override
  public void removeLayoutComponent(Component comp) {
    if (filelist == comp) {
      filelist = null;
    } else if (spotlight == comp) {
      spotlight = null;
    } else if (stack == comp) {
      stack = null;
    }
  }
}
