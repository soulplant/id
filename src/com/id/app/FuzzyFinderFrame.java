package com.id.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;

import com.id.fuzzy.FuzzyFinder;

@SuppressWarnings("serial")
public class FuzzyFinderFrame extends JFrame implements FocusListener, FuzzyFinder.Listener {
  private final FuzzyFinder fuzzyFinder;
  private final JTextField textField = new JTextField();
  private final ItemListPanel itemList = new ItemListPanel();

  public FuzzyFinderFrame(FuzzyFinder fuzzyFinder, KeyListener keyListener) {
    this.fuzzyFinder = fuzzyFinder;
    textField.setPreferredSize(new Dimension(200, 20));
    add(textField, BorderLayout.PAGE_START);
    add(itemList, BorderLayout.CENTER);
    pack();
    textField.addFocusListener(this);
    textField.addKeyListener(keyListener);
  }

  @Override
  public void focusGained(FocusEvent event) {
    // Do nothing.
  }

  @Override
  public void focusLost(FocusEvent event) {
    fuzzyFinder.setVisible(false);
  }

  @Override
  public void onQueryChanged() {
    List<String> matches = fuzzyFinder.getMatches();
    itemList.setItems(matches);
    pack();
  }

  @Override
  public void onSetVisible(boolean visible) {
    textField.setText(fuzzyFinder.getCurrentQuery());
    onQueryChanged();
    setVisible(visible);
  }
}
