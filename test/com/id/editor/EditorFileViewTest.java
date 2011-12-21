package com.id.editor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.id.app.HighlightState;
import com.id.file.File;
import com.id.file.FileView;

public class EditorFileViewTest {
  private File file;
  private FileView fileView;
  private Editor editor;

  @Test
  public void itShouldntCountMatchesOutsideTheViewRange() {
    setup(0, 1, "a", "b", "c");
    editor.setHighlightPattern("c");
    assertEquals(0, editor.getHighlightMatchCount());
  }

  @Test
  public void whenTheViewChangesSizeTheHighlightShouldUpdateAutomatically() {
    setup(0, 1, "a", "b", "c");
    editor.setHighlightPattern("c");
    file.insertLine(2, "c");
    assertEquals(1, editor.getHighlightMatchCount());
    file.removeLine(2);
    assertEquals(0, editor.getHighlightMatchCount());
  }

  private void setup(int start, int end, String... lines) {
    file = new File(lines);
    fileView = new FileView(file, start, end);
    editor = new Editor(fileView, new HighlightState());
  }
}
