package com.id.ui.app;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.VerticalListView;
import com.id.ui.ViewContainer;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class StackView extends JPanel implements ViewContainer<Editor, EditorPanel> {
  private final VerticalListView<Editor, EditorPanel> panel;

  public StackView() {
    panel = new VerticalListView<Editor, EditorPanel>(2000);
    JScrollPane scrollPane = new JScrollPane(panel);

    setLayout(new BorderLayout());
    scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    add(scrollPane);
  }

  @Override
  public void refreshFrom(ListModel<Editor> models,
      Map<Editor, EditorPanel> viewMap) {
    panel.refreshFrom(models, viewMap);
  }
}
