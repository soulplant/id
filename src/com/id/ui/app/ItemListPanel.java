package com.id.ui.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.id.app.App;

@SuppressWarnings("serial")
public class ItemListPanel extends LinewisePanel {
  private static final int MIN_WIDTH_PX = 200;
  private static final int BOTTOM_PADDING_PX = 3;
  private final List<String> items = new ArrayList<String>();
  private int selectedIndex = -1;
  private int preferredWidth;

  public ItemListPanel() {
    setPreferredSize(new Dimension(200, 0));
  }

  public void setItems(List<String> items) {
    this.items.clear();
    this.items.addAll(items);
    moveSelection(0);
    preferredWidth = MIN_WIDTH_PX;
    for (String item : items) {
      preferredWidth = Math.max(preferredWidth, item.length() * getFontWidthPx());
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(preferredWidth, getFontHeightPx() * items.size());
  }

  public void down() {
    moveSelection(1);
  }

  public void up() {
    moveSelection(-1);
  }

  public void moveSelection(int delta) {
    setSelection(selectedIndex + delta);
  }

  public void setSelection(int selection) {
    selectedIndex = selection;
    selectedIndex = Math.max(0, Math.min(items.size() - 1, selectedIndex));
    invalidate();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);
    g.setColor(Color.BLACK);
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    int i = 0;
    for (String item : items) {
      g.drawString(item, 0, getFontHeightPx() * (i + 1) - BOTTOM_PADDING_PX);
      if (selectedIndex == i) {
        g.drawRect(0, getFontHeightPx() * i, g.getClipBounds().width - 1, getFontHeightPx());
      }
      i++;
    }
  }

  public String getSelectedItem() {
    return items.get(selectedIndex);
  }
}
