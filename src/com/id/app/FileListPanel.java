package com.id.app;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FileListPanel extends ItemListPanel implements EditorList.Listener {
  private final EditorList editorList;

  public FileListPanel(EditorList editorList) {
    this.editorList = editorList;
    this.editorList.addListener(this);
    updateView();
  }

  private void updateView() {
    List<String> filenames = new ArrayList<String>();
    for (int i = 0; i < editorList.getEditorCount(); i++) {
      filenames.add(editorList.getEditor(i).getFilename());
    }
    setItems(filenames);
    setSelection(editorList.getSelectedIndex());
    repaint();
  }

  @Override
  public void onSelectedChanged(int i) {
    System.out.println("onSelectedChanged(" + i + ")");
    updateView();
  }

//  @Override
//  public void paint(Graphics g) {
//    super.paint(g);
//    g.drawRect(0, 0, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
//  }
}
