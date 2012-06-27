package com.id.ui.app;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import com.id.fuzzy.Finder;
import com.id.ui.editor.TextPanel;

@SuppressWarnings("serial")
public class FinderPanel extends JPanel implements Finder.Listener {
  private final Finder finder;
  private final TextPanel textPanel;
  private final ItemListPanel itemList = new ItemListPanel();
  private Finder.Listener listener;

  public FinderPanel(Finder finder) {
    this.finder = finder;
    textPanel = new TextPanel(finder.getQueryEditor());
    FinderLayout fuzzyFinderLayout = new FinderLayout();
    setLayout(fuzzyFinderLayout);
    add(textPanel, FinderLayout.MINIBUFFER);
    add(itemList, FinderLayout.ITEMLIST);
    finder.addListener(this);
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension result = new Dimension();
    result.width = itemList.getPreferredSize().width;
    result.height = textPanel.getPreferredSize().height + itemList.getPreferredSize().height;
    return result;
  }

  public void setListener(Finder.Listener listener) {
    this.listener = listener;
  }

  @Override
  public void onQueryChanged() {
    List<String> matches = finder.getMatches();
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
