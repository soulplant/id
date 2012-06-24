package com.id.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.id.app.HighlightState;
import com.id.file.File;
import com.id.file.FileView;
import com.id.file.Range;

public class EditorFileViewTest {
  private File file;
  private FileView fileView;
  private Editor editor;

  @Test
  public void itShouldntCountMatchesOutsideTheViewRange() {
    setup(0, 1, "a", "b", "c");
    editor.setHighlightPattern(Patterns.partWord("c"));
    assertEquals(0, editor.getHighlightMatchCount());
  }

  @Test
  public void whenTheViewChangesSizeTheHighlightShouldUpdateAutomatically() {
    setup(0, 1, "a", "b", "c");
    editor.setHighlightPattern(Patterns.partWord("c"));
    file.insertLine(2, "c");
    assertEquals(1, editor.getHighlightMatchCount());
    file.removeLine(2);
    assertEquals(0, editor.getHighlightMatchCount());
  }

  @Test
  public void testModifiedLines() {
    setup(0, -1, "a", "b", "c", "d", "e", "f");
    fileView.changeLine(1, "B");
    assertTrue(fileView.hasModifiedMarkers(1));
  }

  @Test
  public void testGetDeltas() {
    setup(0, -1, "a", "b", "c", "d", "e", "f");
    fileView.changeLine(1, "B");
    fileView.changeLine(3, "D");
    assertTrue(fileView.hasModifiedMarkers(1));
    assertTrue(fileView.hasModifiedMarkers(3));
    List<Range> deltas = fileView.getDeltas(0);
    assertEquals(2, deltas.size());
  }

  @Test
  public void testGetDeltasWithPadding() {
    setup(0, -1, "a", "b", "c", "d", "e", "f");
    fileView.changeLine(1, "B");
    fileView.changeLine(3, "D");
    assertTrue(fileView.hasModifiedMarkers(1));
    assertTrue(fileView.hasModifiedMarkers(3));
    List<Range> deltas = fileView.getDeltas(3);
    assertEquals(1, deltas.size());
  }

  private void setup(int start, int end, String... lines) {
    file = new File(lines);
    fileView = new FileView(file, start, end);
    editor = new Editor(fileView, new HighlightState(), new Register(),
        new Editor.EmptyEditorEnvironment());
  }
}
