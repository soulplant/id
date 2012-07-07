package com.id.ui.app;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.editor.EditorPanel;

@SuppressWarnings("serial")
public class SpotlightView extends JPanel implements ListModel.Listener<Editor> {
  private final Map<Editor, EditorPanel> map = new HashMap<Editor, EditorPanel>();
  private final ListModel<Editor> editors;
  private final LinewisePanel placeholder = new LinewisePanel();

  public SpotlightView(ListModel<Editor> editors) {
    this.editors = editors;
    setLayout(new BorderLayout());
    for (int i = 0; i < editors.size(); i++) {
      Editor editor = editors.get(i);
      EditorPanel editorPanel = new EditorPanel(editor, editors, true);
      map.put(editor, editorPanel);
    }
    refresh();
  }

  private void refresh() {
    super.removeAll();
    if (editors.isEmpty()) {
      super.add(placeholder);
      return;
    }
    super.add(map.get(editors.getFocusedItem()));
  }

  @Override
  public void onAdded(int i, Editor editor) {
    // TODO(koz): This is some hax here. We don't remove old editors, in case they are
    // being added and removed straight away. This currently happens when ctrl-j/k is
    // typed. We don't want to throw away the EditorPanel because it holds state such
    // as the scroll (which is also a design flaw). What should happen is we should
    // send move events as well as add/remove, so that we don't leak EditorPanels.
    EditorPanel editorPanel = map.get(editor);
    if (editorPanel == null) {
      editorPanel = new EditorPanel(editor, editors, true);
      map.put(editor, editorPanel);
    }
    refresh();
  }

  @Override
  public void onSelectionChanged(int i, Editor editor) {
    refresh();
  }

  @Override
  public void onRemoved(int i, Editor editor) {
    // EditorPanel editorPanel = map.remove(editor);
    refresh();
  }

  @Override
  public void onSelectionLost() {
    // Do nothing.
  }

  @Override
  public void onFocusChanged(boolean isFocused) {
    // Do nothing.
  }
}
