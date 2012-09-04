package com.id.ui;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.editor.Hideable;

@SuppressWarnings("serial")
public class SpotlightView<M, V extends JPanel> extends JPanel implements ViewContainer<M, V>, Hideable.Listener {
  public SpotlightView() {
    setBackground(Constants.BG_COLOR);
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

  @Override
  public void onHiddenChanged(boolean hidden) {
    setVisible(!hidden);
  }
}
