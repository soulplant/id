package com.id.editor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StackListTest {
  @Test
  public void startsHidden() {
    StackList list = new StackList();
    assertTrue(list.isHidden());
  }

  @Test
  public void becomesUnhiddenWhenItemsAreAdded() {
    StackList list = new StackList();
    list.add(new Stack());
    assertFalse(list.isHidden());
  }

  @Test
  public void becomesHiddenWhenEmpty() {
    StackList list = new StackList();
    list.add(new Stack());
    list.removeFocused();
    assertTrue(list.isHidden());
  }
}
