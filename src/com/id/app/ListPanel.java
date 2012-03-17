package com.id.app;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.id.app.ListModel.Listener;

@SuppressWarnings("serial")
public class ListPanel<T> extends JPanel implements Listener<T> {
  public interface Factory<T> {
    JComponent makeComponentFor(T t);
  }

  private final ListModel<T> model;
  private final Factory<T> factory;

  public ListPanel(ListModel<T> model, Factory<T> factory) {
    this.model = model;
    this.factory = factory;
    this.model.addListener(this);
    setLayout(new FlowLayout(FlowLayout.LEADING));
    for (int i = 0; i < model.size(); i++) {
      onAdded(i, model.get(i));
    }
    if (!model.isEmpty()) {
      onSelectionChanged(model.getFocusedIndex(), model.getFocusedItem());
    }
  }

  @Override
  public void onAdded(int i, T t) {
    JComponent component = factory.makeComponentFor(model.get(i));
    add(component, i);
    revalidate();
  }

  @Override
  public void onSelectionChanged(int i, T t) {

  }

  @Override
  public void onRemoved(int i, T t) {
    remove(i);
    revalidate();
  }

  @Override
  public void onSelectionLost() {

  }

  @Override
  public void onFocusChanged(boolean isFocused) {

  }
}
