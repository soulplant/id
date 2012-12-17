package com.id.events;

import java.util.ArrayList;
import java.util.List;


public class ShortcutTree {
  static class Node {
    private final List<Node> children = new ArrayList<Node>();
    private final KeyStroke label;
    private Action action;

    public Node(KeyStroke label) {
      this.label = label;
    }

    public Node() {
      this(null);
    }

    private boolean match(KeyStroke keyStroke) {
      return label.equals(keyStroke);
    }

    public Node lookupChild(KeyStroke key) {
      for (Node node : children) {
        if (node.match(key)) {
          return node;
        }
      }
      return null;
    }

    public boolean isLeaf() {
      return children.isEmpty();
    }

    public Action getAction() {
      return action;
    }

    public Node insertChild(KeyStroke key) {
      Node node = new Node(key);
      children.add(node);
      return node;
    }

    public void setAction(Action action) {
      this.action = action;
    }
  }

  private final Node rootNode = new Node();
  private Node currentNode = rootNode;
  private KeyStrokeAction pendingKeyStrokeAction = null;

  public interface Action {
    void execute();
  }

  public static abstract class KeyStrokeAction implements Action {
    public void execute() {
      throw new UnsupportedOperationException();
    }
    public abstract void execute(KeyStroke keyStroke);
  }

  public void setShortcut(List<KeyStroke> keyStrokes, Action action) {
    Node node = rootNode;
    for (KeyStroke key : keyStrokes) {
      Node child = node.lookupChild(key);
      if (child == null) {
        child = node.insertChild(key);
      }
      node = child;
    }
    node.setAction(action);
  }

  private boolean step(KeyStroke key) {
    boolean wasAtTop = isAtTop();
    currentNode = currentNode.lookupChild(key);
    if (currentNode == null) {
      currentNode = rootNode;
      return !wasAtTop;
    }
    return true;
  }

  private Action getCurrentAction() {
    if (currentNode.isLeaf()) {
      return currentNode.getAction();
    }
    return null;
  }

  public boolean stepAndExecute(KeyStroke key) {
    if (pendingKeyStrokeAction != null) {
      pendingKeyStrokeAction.execute(key);
      pendingKeyStrokeAction = null;
      return true;
    }
    boolean wasHandled = step(key);
    Action action = getCurrentAction();
    if (action != null) {
      if (action instanceof KeyStrokeAction) {
        pendingKeyStrokeAction = (KeyStrokeAction) action;
        reset();
        return true;
      }
      action.execute();
      reset();
    }
    return wasHandled;
  }

  public void reset() {
    currentNode = rootNode;
  }

  public boolean isAtTop() {
    return currentNode == rootNode;
  }
}
