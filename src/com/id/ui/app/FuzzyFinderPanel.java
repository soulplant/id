package com.id.ui.app;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import com.id.fuzzy.FuzzyFinder;
import com.id.ui.editor.TextPanel;

@SuppressWarnings("serial")
public class FuzzyFinderPanel extends JPanel implements FuzzyFinder.Listener {
  private final FuzzyFinder fuzzyFinder;
  private final TextPanel textPanel;
  private final ItemListPanel itemList = new ItemListPanel();
  private FuzzyFinder.Listener listener;

  public FuzzyFinderPanel(FuzzyFinder fuzzyFinder) {
    this.fuzzyFinder = fuzzyFinder;
    textPanel = new TextPanel(fuzzyFinder.getQueryEditor());
    FuzzyFinderLayout fuzzyFinderLayout = new FuzzyFinderLayout();
    setLayout(fuzzyFinderLayout);
    add(textPanel, FuzzyFinderLayout.MINIBUFFER);
    add(itemList, FuzzyFinderLayout.ITEMLIST);
    fuzzyFinder.addListener(this);
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension result = new Dimension();
    result.width = itemList.getPreferredSize().width;
    result.height = textPanel.getPreferredSize().height + itemList.getPreferredSize().height;
    return result;
  }

  public void setListener(FuzzyFinder.Listener listener) {
    this.listener = listener;
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
    onQueryChanged();
    setVisible(visible);
    listener.onSetVisible(visible);
  }
}
