package com.id.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Focusable;

public class ListModelTest {
  private ListModel<String> model;
  private ListModel.Listener<String> listener;

  @SuppressWarnings("unused")
  private class NoisyListener implements ListModel.Listener<String> {
    @Override
    public void onAdded(int i, String t) {
      System.out.println("onAdded(" + i + ", " + t + ")");
    }

    @Override
    public void onSelectionChanged(int i, String t) {
      System.out.println("onSelectionChanged(" + i + ", " + t + ")");
    }

    @Override
    public void onRemoved(int i, String t) {
      System.out.println("onRemoved(" + i + ", " + t + ")");
    }

    @Override
    public void onSelectionLost() {
      System.out.println("onSelectionLost()");
    }

    @Override
    public void onFocusChanged(boolean isFocused) {
      System.out.println("onFocusChanged(" + isFocused + ")");
    }
  }

  @SuppressWarnings("unchecked")
  @Before
  public void setup() {
    model = new ListModel<String>();
    listener = mock(ListModel.Listener.class);
  }

  @Test
  public void itFiresEventsForAddingAnItem() {
    model.addListener(listener);
    model.add("hi");
    verify(listener).onAdded(0, "hi");
    verify(listener).onSelectionChanged(0, "hi");
    model.add("there");
    verify(listener).onAdded(1, "there");
    verify(listener).onSelectionChanged(1, "there");
  }

  @Test
  public void itFirstFocusChangedEventsWhenTheFocusChanges() {
    model.add("hi");
    model.add("there");
    model.addListener(listener);
    model.moveUp();
    verify(listener).onSelectionChanged(0, "hi");
    model.moveDown();
    verify(listener).onSelectionChanged(1, "there");
  }

  @Test
  public void itFirstFocusChangedOnlyOnceWhenWeRemoveAnItem() {
    model.add("hi");
    model.add("there");
    model.addListener(listener);
    model.removeFocused();
    verify(listener).onSelectionChanged(0, "hi");
  }

  @Test
  public void itCanRetrieveItemsByIndex() {
    model.add("hi");
    model.add("there");
    assertEquals("hi", model.get(0));
    assertEquals("there", model.get(1));
  }

  @Test
  public void itKnowsItsSize() {
    assertEquals(0, model.size());
    model.add("hi");
    assertEquals(1, model.size());
    model.add("there");
    assertEquals(2, model.size());
  }

  @Test
  public void itDoesntFailWhenFocusIsMovedOnEmptyList() {
    model.moveUp();
    model.moveDown();
  }

  @Test
  public void moveFocusUp() {
    model.add("hi");
    model.add("there");
    model.add("mate");
    assertEquals(2, model.getFocusedIndex());
    model.moveFocusedItemUp();
    assertEquals(1, model.getFocusedIndex());
  }

  @Test
  public void insertAfterFocused() {
    model.add("hi");
    model.add("there");
    model.add("mate");
    model.moveUp();
    model.insertAfterFocused("test");
    assertEquals(2, model.getFocusedIndex());
    assertEquals("test", model.getFocusedItem());
  }

  @Test
  public void removeFocusedMovesFocusUpwards() {
    model.add("hi");
    model.add("there");
    model.add("mate");
    model.moveUp();
    model.removeFocused();
    assertEquals(0, model.getFocusedIndex());
  }

  @Test
  public void removingAfterFocusedDoesntMoveFocused() {
    model.add("hi");
    model.add("there");
    model.add("mate");
    model.moveUp();
    model.remove(2);
    assertEquals(1, model.getFocusedIndex());
  }

  @Test
  public void movingFocusedItemDownMovesFocusedIndex() {
    model.setFocusLatest(false);
    model.add("hi");
    model.add("there");
    model.moveUp();
    model.moveFocusedItemDown();
    assertEquals(1, model.getFocusedIndex());
  }

  private class FocusableThing implements Focusable {
    private boolean focused = false;

    @Override
    public void setFocused(boolean selected) {
      this.focused = selected;

    }

    @Override
    public boolean isFocused() {
      return focused;
    }
  }

  @Test
  public void itFocusesAndUnfocusesItsItemsCorrectly() {
    ListModel<FocusableThing> things = new ListModel<FocusableThing>();
    things.focus();
    FocusableThing f1 = new FocusableThing();
    FocusableThing f2 = new FocusableThing();

    things.add(f1);
    assertTrue(f1.isFocused());
    things.add(f2);
    assertFalse(f1.isFocused());
    assertTrue(f2.isFocused());
    things.remove(1);
    assertTrue(f1.isFocused());
    assertFalse(f2.isFocused());

    things.setFocused(false);
    assertFalse(f1.isFocused());
    things.setFocused(true);
    assertTrue(f1.isFocused());
  }

  @Test
  public void itDoesntFocusAnythingUnlessItHasTheFocus() {
    ListModel<FocusableThing> things = new ListModel<FocusableThing>();
    FocusableThing f1 = new FocusableThing();
    things.setFocused(false);
    things.add(f1);
    assertFalse(f1.isFocused());
    things.setFocused(true);
    assertTrue(f1.isFocused());
  }
}
