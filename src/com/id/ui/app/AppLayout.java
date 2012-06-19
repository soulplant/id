package com.id.ui.app;

import com.id.app.Constants;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import java.util.ArrayList;
import java.util.List;

public class AppLayout implements LayoutManager {
  private Component filelist = null;
  private Component spotlight = null;
  private Component stack = null;
  private Component fuzzyFinder = null;
  private Component minibuffer = null;
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
    filelist.setBounds(0, 0, fileListWidth, height);
    divideHorizontalSpace(remainingWidth, fileListWidth, height, getVisibleEditorComponents());
    stack.setVisible(isStackVisible);
    if (minibuffer != null) {
      minibuffer.setBounds(0, height, parent.getWidth(), minibufferHeight);
    }
    if (fuzzyFinder != null) {
      fuzzyFinder.setBounds(250, 0, 200, height);
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
}
