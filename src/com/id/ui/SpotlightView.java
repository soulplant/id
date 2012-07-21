package com.id.ui;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class SpotlightView extends JPanel implements ViewContainer<Editor, EditorPanel> {
  public SpotlightView() {
    setLayout(new BorderLayout());
  }

  @Override
  public void refreshFrom(ListModel<Editor> models,
      Map<Editor, EditorPanel> viewMap) {
    removeAll();
    if (models.isEmpty()) {
      return;
    }
    add(viewMap.get(models.getFocusedItem()));
  }
}
