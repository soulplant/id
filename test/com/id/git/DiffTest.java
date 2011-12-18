package com.id.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.id.util.Util;

public class DiffTest {

  @Test
  public void test() {
    List<String> lines = Util.readFile("resources/diff");
    Diff diff = Diff.fromLines(lines);
    assertEquals(3, diff.getFileCount());
    FileDelta delta = diff.getDelta("src/com/id/app/App.java");
    assertNotNull(delta);
    assertEquals(13, delta.getAdditions().size());
    assertEquals(0, delta.getDeletions().size());

    delta = diff.getDelta("src/com/id/app/Controller.java");
    assertNotNull(delta);
    assertEquals(1, delta.getDeletions().size());
  }
}
