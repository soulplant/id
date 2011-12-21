package com.id.ui.app;

import java.util.ArrayList;
import java.util.List;

import com.id.app.ListModel;
import com.id.editor.Editor;

@SuppressWarnings("serial")
public class FileListView extends ItemListPanel implements ListModel.Listener<Editor> {
  private final ListModel<Editor> model;

  public FileListView(ListModel<Editor> model) {
    this.model = model;
  }

  @Override
  public void onAdded(int i, Editor t) {
    updateItems();
  }

  @Override
  public void onFocusChanged(int i, Editor t) {
    updateItems();
  }

  @Override
  public void onRemoved(int i, Editor t) {
    updateItems();
  }

  @Override
  public void onFocusLost() {
    updateItems();
  }

  private void updateItems() {
    List<String> lines = new ArrayList<String>();
    for (int i = 0; i < model.size(); i++) {
      lines.add(model.get(i).getFilename());
    }
    setItems(lines);
    setSelection(model.getFocusedIndex());
  }
}
