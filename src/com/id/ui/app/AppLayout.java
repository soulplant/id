package com.id.ui.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import com.id.ui.editor.TextPanel;

public class AppLayout implements LayoutManager {
  private static final int FUZZY_FINDER_TOP_PADDING_PX = 5;
  private Component filelist = null;
  private Component spotlight = null;
  private Component stack = null;
  private Component fuzzyFinder = null;
  private TextPanel minibuffer = null;
  private boolean isStackVisible = false;

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
      minibuffer = (TextPanel) component;
    }
  }

  @Override
  public void layoutContainer(Container parent) {
    int parentHeight = parent.getHeight();
    if (minibuffer != null) {
      parentHeight -= getMinibufferHeight();
    }
    int fileListWidth = filelist.getPreferredSize().width;
    int remainingWidth = parent.getWidth() - fileListWidth;
    filelist.setBounds(0, 0, fileListWidth, parentHeight);
    divideHorizontalSpace(remainingWidth, fileListWidth, parentHeight, getVisibleEditorComponents());
    stack.setVisible(isStackVisible);
    if (minibuffer != null) {
      minibuffer.setBounds(0, parentHeight, parent.getWidth(), getMinibufferHeight());
    }
    if (fuzzyFinder != null) {
      int width = (int) fuzzyFinder.getPreferredSize().getWidth();
      int height = parent.getHeight();
      fuzzyFinder.setBounds(fileListWidth, minibuffer.getFontHeightPx() + FUZZY_FINDER_TOP_PADDING_PX,
          width, height);
    }
  }

  private List<Component> getVisibleEditorComponents() {
    List<Component> result = new ArrayList<Component>();
    result.add(spotlight);
    if (isStackVisible) {
      result.add(stack);
    }
    return result;
  }

  private void divideHorizontalSpace(int remainingWidth, int left, int height, List<Component> components) {
    int componentWidth = remainingWidth / components.size();
    for (Component component : components) {
      component.setBounds(left, 0, componentWidth, height);
      left += componentWidth;
    }
  }

  public void setStackVisible(boolean isStackVisible) {
    this.isStackVisible = isStackVisible;
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

  private int getMinibufferHeight() {
    return minibuffer.getFontHeightPx();
  }
}
