package com.id.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public class VerticalListLayout implements LayoutManager {
  private final int inbetweenPadding;
  private final int bottomPadding;

  public VerticalListLayout(int inbetweenPadding, int bottomPadding) {
    this.inbetweenPadding = inbetweenPadding;
    this.bottomPadding = bottomPadding;
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
      currentHeight += preferredSize.getHeight() + inbetweenPadding;
    }
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    // Do nothing.
    return null;
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    int maxWidth = 0;
    int totalHeight = 0;
    for (int i = 0; i < parent.getComponentCount(); i++) {
      Component component = parent.getComponent(i);
      if (i != 0) {
        totalHeight += inbetweenPadding;
      }
      totalHeight += component.getSize().height;
      maxWidth = Math.max(maxWidth, component.getSize().width);
    }
    // Don't add padding unless we have height to avoid the possibility of
    // the padding scrolling the viewport before a child is added, because
    // otherwise the child may get unexpected scrolling behaviour (eg: start
    // life as the first element in a vertical list pushed off the top of the
    // screen.
    int padding = totalHeight == 0 ? 0 : bottomPadding;
    return new Dimension(maxWidth, totalHeight + padding);
  }

  @Override
  public void removeLayoutComponent(Component parent) {
    // Do nothing.
  }
}
