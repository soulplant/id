package com.id.ui.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.id.app.App;

@SuppressWarnings("serial")
public class ItemListPanel extends JPanel {
  private final List<String> items = new ArrayList<String>();
  private int selectedIndex = -1;

  public ItemListPanel() {
    setPreferredSize(new Dimension(200, 0));
  }

  public void setItems(List<String> items) {
    this.items.clear();
    this.items.addAll(items);
    moveSelection(0);
    setPreferredSize(new Dimension(200, 30 * this.items.size()));
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
    repaint();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    App.configureFont(g);
    int i = 0;
    for (String item : items) {
      g.drawString(item, 0, 30 * (i + 1));
      if (selectedIndex == i) {
        g.drawRect(0, 30 * i, g.getClipBounds().width - 1, 30);
      }
      i++;
    }
  }

  public String getSelectedItem() {
    return items.get(selectedIndex);
  }
}