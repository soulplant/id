package com.id.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public class VerticalListLayout implements LayoutManager {
  private final int padding;

  public VerticalListLayout(int padding) {
    this.padding = padding;
  }

  @Override
  public void addLayoutComponent(String text, Component parent) {
    // Do nothing.
  }

  @Override
  public void layoutContainer(Container parent) {
    int currentHeight = 0;
    for (int i = 0; i < parent.getComponentCount(); i++) {
      Component component = parent.getComponent(i);
      Dimension preferredSize = component.getPreferredSize();
      component.setBounds(new Rectangle(0, currentHeight, parent.getWidth(),
          (int) preferredSize.getHeight()));
      currentHeight += preferredSize.getHeight() + padding;
    }
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    // Do nothing.
    return null;
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    // Do nothing.
    return null;
  }

  @Override
  public void removeLayoutComponent(Component parent) {
    // Do nothing.
  }
}
