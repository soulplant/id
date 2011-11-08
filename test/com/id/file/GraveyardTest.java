package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.id.file.Grave;
import com.id.file.Graveyard;
import com.id.file.Tombstone;

public class GraveyardTest {
  @Test
  public void addRemoveOnEmpty() {
    Graveyard graveyard = new Graveyard(Arrays.<String>asList());
    graveyard.onLineInserted(0, "hi");
    assertEquals(Tombstone.Status.NEW, graveyard.getStatus(0));
    graveyard.onLineRemoved(0, "hi");
    assertEquals(0, graveyard.size());
  }

  @Test
  public void graveSplitting() {
    Graveyard graveyard = new Graveyard(Arrays.asList("a", "b", "c"));

    assertTrue(graveyard.getGrave(1).isEmpty());
    graveyard.onLineRemoved(2, "c");
    assertEquals(1, graveyard.getGrave(1).size());
    graveyard.onLineInserted(2, "c");
    assertTrue(graveyard.getGrave(1).isEmpty());
  }

  @Test
  public void graveTests() {
    Graveyard graveyard = new Graveyard(Arrays.asList("a", "b", "c",
        "d", "e"));

    graveyard.onLineRemoved(4, "e");
    graveyard.onLineRemoved(2, "c");
    graveyard.onLineRemoved(2, "d");

    graveyard.onLineInserted(2, "d");
    graveyard.onLineInserted(2, "c");
    graveyard.onLineInserted(4, "e");

    assertTrue(graveyard.isAllGravesEmpty());
  }

  @Test
  public void splice() {
    List<String> list = new ArrayList<String>();
    list.add("a");
    list.add("b");
    list.add("c");
    List<String> result = Grave.splice(1, list);
    assertEquals("a", list.get(0));
    assertEquals(1, list.size());
    assertEquals("b", result.get(0));
    assertEquals("c", result.get(1));
    assertEquals(2, result.size());
  }
}
