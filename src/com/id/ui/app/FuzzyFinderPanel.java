package com.id.ui.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.id.fuzzy.FuzzyFinder;
import com.id.ui.editor.TextPanel;

@SuppressWarnings("serial")
public class FuzzyFinderPanel extends JPanel implements FuzzyFinder.Listener {
  private final FuzzyFinder fuzzyFinder;
  private final JTextField textField = new JTextField();
  private final TextPanel textPanel;
  private final ItemListPanel itemList = new ItemListPanel();
  private FuzzyFinder.Listener listener;

  public FuzzyFinderPanel(FuzzyFinder fuzzyFinder) {
    this.fuzzyFinder = fuzzyFinder;
    textPanel = new TextPanel(fuzzyFinder.getQueryEditor());
    textPanel.setPreferredSize(new Dimension(200, 14));
    add(textPanel, BorderLayout.PAGE_START);
    add(itemList, BorderLayout.CENTER);
    fuzzyFinder.addListener(this);
  }

  public void setListener(FuzzyFinder.Listener listener) {
    this.listener = listener;
  }

  @Override
  public void repaint() {
    super.repaint();
    if (textField != null) {
      textField.repaint();
    }
  }

  @Override
  public void onQueryChanged() {
    List<String> matches = fuzzyFinder.getMatches();
    itemList.setItems(matches);
    listener.onQueryChanged();
  }

  @Override
  public void onSelectionChanged(int index) {
    itemList.setSelection(index);
  }

  @Override
  public void onSetVisible(boolean visible) {
    textField.setText(fuzzyFinder.getCurrentQuery());
    onQueryChanged();
    setVisible(visible);
    listener.onSetVisible(visible);
  }
}
