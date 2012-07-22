package com.id.ui;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;

@SuppressWarnings("serial")
public class SpotlightView<M, V extends JPanel> extends JPanel implements ViewContainer<M, V> {
  public SpotlightView() {
    setLayout(new BorderLayout());
  }

  @Override
  public void refreshFrom(ListModel<M> models,
      Map<M, V> viewMap) {
    removeAll();
    if (models.isEmpty()) {
      return;
    }
    add(viewMap.get(models.getFocusedItem()));
  }
}
