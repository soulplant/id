package com.id.ui.app;

import java.util.HashMap;
import java.util.Map;

import com.id.app.ListModel;
import com.id.editor.Editor;
import com.id.ui.editor.EditorPanel;

// TODO Merge this into EditorSwapperPanel.
@SuppressWarnings("serial")
public class SpotlightView extends EditorSwapperPanel {
  public SpotlightView(ListModel<Editor> editors) {
    super(editors);
  }
}
