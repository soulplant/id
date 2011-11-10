package com.id.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

import com.id.fuzzy.FuzzyFinder;

@SuppressWarnings("serial")
public class FuzzyFinderFrame extends JFrame implements KeyListener, FocusListener {
  public interface Listener {
    void onSelected(String item);
  }
  private final FuzzyFinder fuzzyFinder;
  private final JTextField textField = new JTextField();
  private final ItemListPanel itemList = new ItemListPanel();
  private final Listener listener;

  public FuzzyFinderFrame(FuzzyFinder fuzzyFinder, Listener listener) {
    this.fuzzyFinder = fuzzyFinder;
    this.listener = listener;

    textField.setPreferredSize(new Dimension(200, 20));
    textField.addKeyListener(this);
    add(textField, BorderLayout.PAGE_START);
    add(itemList, BorderLayout.CENTER);
    pack();
    textField.addFocusListener(this);
  }

  @Override
  public void keyPressed(KeyEvent event) {
    boolean handled = true;
    switch (event.getKeyCode()) {
    case KeyEvent.VK_UP:
      itemList.up();
      break;
    case KeyEvent.VK_DOWN:
      itemList.down();
      break;
    case KeyEvent.VK_ESCAPE:
      exit();
      break;
    case KeyEvent.VK_ENTER:
      listener.onSelected(itemList.getSelectedItem());
      exit();
      break;
    default:
      handled = false;
      break;
    }

    if (handled) {
      return;
    }
    updateResults();
  }

  private void exit() {
    setVisible(false);
  }

  @Override
  public void keyReleased(KeyEvent event) {
    // Do nothing.
  }

  @Override
  public void keyTyped(KeyEvent event) {
    // Do nothing.
  }

  private void updateResults() {
    itemList.setItems(fuzzyFinder.getMatches(textField.getText()));
    pack();
  }

  @Override
  public void focusGained(FocusEvent event) {
    // Do nothing.
  }

  @Override
  public void focusLost(FocusEvent event) {
    setVisible(false);
  }
}
