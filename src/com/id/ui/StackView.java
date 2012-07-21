package com.id.ui;

import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.app.StackLayout;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class StackView extends JPanel implements ViewContainer<Editor, EditorPanel> {
  private final static int SNIPPET_PADDING_PX = 12;

  public StackView() {
    setLayout(new StackLayout(SNIPPET_PADDING_PX));
    setBackground(Constants.BG_COLOR);
  }

  @Override
  public void refreshFrom(ListModel<Editor> models,
      Map<Editor, EditorPanel> viewMap) {
    removeAll();
    for (Editor editor : models) {
      add(viewMap.get(editor));
    }
  }
}
