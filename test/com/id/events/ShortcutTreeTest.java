package com.id.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Test;

import com.id.events.ShortcutTree.Action;
import com.id.events.ShortcutTree.KeyStrokeAction;

public class ShortcutTreeTest {
  @Test
  public void test() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction = mock(ShortcutTree.Action.class);
    tree.setShortcut(KeyStroke.fromString("gg"), mockAction);
    tree.stepAndExecute(KeyStroke.fromChar('g'));
    tree.stepAndExecute(KeyStroke.fromChar('g'));
    verify(mockAction).execute();
  }

  @Test
  public void branching() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction1 = mock(ShortcutTree.Action.class);
    Action mockAction2 = mock(ShortcutTree.Action.class);
    tree.setShortcut(KeyStroke.fromString("gg"), mockAction1);
    tree.setShortcut(KeyStroke.fromString("gf"), mockAction2);
    tree.stepAndExecute(KeyStroke.fromChar('g'));
    tree.stepAndExecute(KeyStroke.fromChar('f'));
    verify(mockAction2).execute();
    assertFalse(tree.isAtTop());
    tree.reset();
    assertTrue(tree.isAtTop());
  }

  @Test
  public void stepAndExecute() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction = mock(ShortcutTree.Action.class);
    tree.setShortcut(KeyStroke.fromString("gg"), mockAction);
    tree.stepAndExecute(KeyStroke.fromChar('g'));
    verify(mockAction, times(0)).execute();
    tree.stepAndExecute(KeyStroke.fromChar('g'));
    verify(mockAction, times(1)).execute();
  }

  @Test
  public void fanout() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction = mock(ShortcutTree.Action.class);
    tree.setShortcut(KeyStroke.fromString("cc"), mockAction);
    tree.stepAndExecute(KeyStroke.fromChar('c'));
    tree.stepAndExecute(KeyStroke.fromChar('c'));
    verify(mockAction, times(1)).execute();
  }

  @Test
  public void handledKeys() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction = mock(ShortcutTree.Action.class);
    tree.setShortcut(KeyStroke.fromString("cc"), mockAction);
    assertTrue(tree.stepAndExecute(KeyStroke.fromChar('c')));
    assertTrue(tree.stepAndExecute(KeyStroke.fromChar('d')));
    assertTrue(tree.isAtTop());
    assertTrue(tree.stepAndExecute(KeyStroke.fromChar('c')));
    assertTrue(tree.stepAndExecute(KeyStroke.fromChar('c')));
    assertTrue(tree.isAtTop());
  }

  @Test
  public void uppercase() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction = mock(ShortcutTree.Action.class);
    tree.setShortcut(KeyStroke.fromString("g"), mock(ShortcutTree.Action.class));
    tree.setShortcut(KeyStroke.fromString("G"), mockAction);
    tree.stepAndExecute(KeyStroke.fromChar('G'));
    verify(mockAction, times(1)).execute();
  }

  @Test
  public void escape() {
    ShortcutTree tree = new ShortcutTree();
    Action mockAction = mock(ShortcutTree.Action.class);
    tree.setShortcut(Arrays.asList(KeyStroke.escape()), mockAction);
    tree.stepAndExecute(KeyStroke.escape());
    verify(mockAction, times(1)).execute();
  }

  @Test
  public void keyStrokeAction() {
    ShortcutTree tree = new ShortcutTree();
    KeyStrokeAction mockAction = mock(KeyStrokeAction.class);
    Action mockDAction = mock(Action.class);
    tree.setShortcut(KeyStroke.fromString("D"), mockDAction);
    tree.setShortcut(KeyStroke.fromString("f"), mockAction);
    step(tree, "fl");
    verify(mockAction, times(1)).execute(eq(KeyStroke.fromChar('l')));
    step(tree, "D");
    verify(mockDAction, times(1)).execute();
  }

  private boolean step(ShortcutTree tree, String keys) {
    boolean result = false;
    for (KeyStroke key : KeyStroke.fromString(keys)) {
      result = tree.stepAndExecute(key);
    }
    return result;
  }
}
