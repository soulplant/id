package com.id.app;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class ListModelTest {
  private ListModel<String> model;
  private ListModel.Listener<String> listener;

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
    verify(listener).onFocusChanged(0, "hi");
    model.add("there");
    verify(listener).onAdded(1, "there");
    verify(listener).onFocusChanged(1, "there");
  }

  @Test
  public void itFirstFocusChangedEventsWhenTheFocusChanges() {
    model.add("hi");
    model.add("there");
    model.addListener(listener);
    model.moveUp();
    verify(listener).onFocusChanged(0, "hi");
    model.moveDown();
    verify(listener).onFocusChanged(1, "there");
  }

  @Test
  public void itFirstFocusChangedOnlyOnceWhenWeRemoveAnItem() {
    model.add("hi");
    model.add("there");
    model.addListener(listener);
    model.removeFocused();
    verify(listener).onFocusChanged(0, "hi");
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
}
