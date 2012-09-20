package com.id.app;

import com.id.editor.Editor;
import com.id.editor.Editor.EditorEnvironment;
import com.id.editor.Register;
import com.id.editor.SharedEditorSettings;
import com.id.file.FileView;

public class EditorFactory {
  private final HighlightState highlightState;
  private final Register register;
  private EditorEnvironment editorEnvironment = null;
  private final ViewportTracker viewportTracker;
  private final SharedEditorSettings settings;

  public EditorFactory(HighlightState highlightState, Register register,
      ViewportTracker viewportTracker, SharedEditorSettings settings) {
    this.highlightState = highlightState;
    this.register = register;
    this.viewportTracker = viewportTracker;
    this.settings = settings;
  }

  public void setEditorEnvironment(EditorEnvironment editorEnvironment) {
    this.editorEnvironment = editorEnvironment;
  }

  public Editor makeEditor(FileView fileView) {
    Editor editor = new Editor(fileView, highlightState, register,
        editorEnvironment, settings);
    editor.setViewportTracker(viewportTracker);
    return editor;
  }
}
