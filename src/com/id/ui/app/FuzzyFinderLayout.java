package com.id.ui.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class FuzzyFinderLayout implements LayoutManager {
  public static final String ITEMLIST = "itemlist";
  public static final String MINIBUFFER = "minibuffer";

  private Component itemList = null;
  private LinewisePanel minibuffer = null;

  @Override
  public void addLayoutComponent(String name, Component component) {
    if (name == MINIBUFFER) {
      minibuffer = (LinewisePanel) component;
    } else if (name == ITEMLIST) {
      itemList = component;
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public void layoutContainer(Container parent) {
    if (minibuffer == null) {
      throw new IllegalStateException();
    }
    if (itemList == null) {
      throw new IllegalStateException();
    }
    minibuffer.setBounds(0, 0, parent.getWidth(), minibuffer.getFontHeightPx());
    int itemListY = minibuffer.getFontHeightPx();
    itemList.setBounds(0, itemListY, parent.getWidth(), parent.getHeight() - itemListY);
  }

  @Override
  public Dimension minimumLayoutSize(Container arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Dimension preferredLayoutSize(Container arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeLayoutComponent(Component arg0) {
    // TODO Auto-generated method stub

  }

}
