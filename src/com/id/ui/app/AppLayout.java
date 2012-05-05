package com.id.ui.app;

import com.id.app.Constants;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class AppLayout implements LayoutManager {
  private Component filelist = null;
  private Component spotlight = null;
  private Component stack = null;
  private Component fuzzyFinder = null;
  private Component minibuffer = null;

  @Override
  public void addLayoutComponent(String name, Component component) {
    if (name.equals("filelist")) {
      filelist = component;
    } else if (name.equals("spotlight")) {
      spotlight = component;
    } else if (name.equals("stack")) {
      stack = component;
    } else if (name.equals("fuzzyfinder")) {
      fuzzyFinder = component;
    } else if (name.equals("minibuffer")) {
      minibuffer = component;
    }
  }

  @Override
  public void layoutContainer(Container parent) {
    int minibufferHeight = Constants.CHAR_HEIGHT_PX;
    int height = parent.getHeight();
    if (minibuffer != null) {
      height -= minibufferHeight;
    }
    int fileListWidth = 250;
    int remainingWidth = parent.getWidth() - fileListWidth;
    int editorWidth = remainingWidth / 2;
    filelist.setBounds(0, 0, fileListWidth, height);
    spotlight.setBounds(fileListWidth, 0, editorWidth, height);
    stack.setBounds(fileListWidth + editorWidth, 0, editorWidth, height);
    if (minibuffer != null) {
      minibuffer.setBounds(0, height, parent.getWidth(), minibufferHeight);
    }
    if (fuzzyFinder != null) {
      fuzzyFinder.setBounds(250, 0, 200, height);
    }
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
    } else if (fuzzyFinder == comp) {
      fuzzyFinder = null;
    }
  }
}
