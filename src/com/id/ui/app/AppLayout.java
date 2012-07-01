package com.id.ui.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import com.id.ui.editor.TextPanel;

public class AppLayout implements LayoutManager {
  private static final int FUZZY_FINDER_TOP_PADDING_PX = 5;
  private static final int FILE_LIST_RIGHT_PADDING_PX = 15;

  private Component filelist = null;
  private Component spotlight = null;
  private Component stack = null;
  private Component fuzzyFinder = null;
  private TextPanel minibuffer = null;

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

  // TODO(koz): Much of this work should be done by configuring panels with
  // simpler, more general layouts, rather than laying them all out here.
  @Override
  public void layoutContainer(Container parent) {
    Insets insets = parent.getInsets();

    int parentHeight = parent.getHeight() - insets.top - insets.bottom;
    int parentWidth = parent.getWidth() - insets.left - insets.right;
    int remainingHeight = parentHeight - getMinibufferHeight();
    int fileListWidth = filelist.getPreferredSize().width;
    int remainingWidth = parentWidth - fileListWidth - FILE_LIST_RIGHT_PADDING_PX;

    filelist.setBounds(insets.left, insets.top, fileListWidth, remainingHeight);
    int editorsLeft = insets.left + fileListWidth + FILE_LIST_RIGHT_PADDING_PX;
    divideHorizontalSpace(remainingWidth, editorsLeft, insets.top,
        remainingHeight, getVisibleEditorComponents());
    if (minibuffer != null) {
      minibuffer.setBounds(insets.left, insets.top + remainingHeight,
          remainingWidth, getMinibufferHeight());
    }
    if (fuzzyFinder != null) {
      int width = (int) fuzzyFinder.getPreferredSize().getWidth();
      fuzzyFinder.setBounds(fileListWidth, minibuffer.getFontHeightPx() + FUZZY_FINDER_TOP_PADDING_PX,
          width, remainingHeight);
    }
  }

  private List<Component> getVisibleEditorComponents() {
    List<Component> result = new ArrayList<Component>();
    result.add(spotlight);
    if (stack.isVisible()) {
      result.add(stack);
    }
    return result;
  }

  private void divideHorizontalSpace(int remainingWidth, int left, int top, int height, List<Component> components) {
    int componentWidth = remainingWidth / components.size();
    for (Component component : components) {
      component.setBounds(left, top, componentWidth, height);
      left += componentWidth;
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

  private int getMinibufferHeight() {
    if (minibuffer == null) {
      return 0;
    }
    return minibuffer.getFontHeightPx();
  }
}
