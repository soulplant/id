package com.id.ui;

import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;

@SuppressWarnings("serial")
public class VerticalListView<M, V extends JPanel> extends JPanel implements ViewContainer<M, V> {
  private final static int SNIPPET_PADDING_PX = 12;

  public VerticalListView(int bottomPadding) {
    setLayout(new VerticalListLayout(SNIPPET_PADDING_PX, bottomPadding));
    setBackground(Constants.BG_COLOR);
  }

  @Override
  public void refreshFrom(ListModel<M> models, Map<M, V> viewMap) {
    removeAll();
    for (M model : models) {
      add(viewMap.get(model));
    }
  }
}
