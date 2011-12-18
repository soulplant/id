package com.id.platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.file.File;

class Node {
  private final String name;
  private final List<Node> children = new ArrayList<Node>();
  private List<String> contents = null;

  public Node(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public List<Node> getChildren() {
    return children;
  }

  public void addChild(Node node) {
    children.add(node);
  }

  public int size() {
    return children.size();
  }

  public Node getChildNamed(String name) {
    for (Node child : children) {
      if (child.hasName(name)) {
        return child;
      }
    }
    return null;
  }

  private boolean hasName(String name) {
    return this.name.equals(name);
  }

  public boolean isFile() {
    return contents != null;
  }

  public void setContents(List<String> contents) {
    this.contents = contents;
  }

  public List<String> getContents() {
    return contents;
  }
}

public class InMemoryFileSystem implements FileSystem {
  private final Node root = new Node(".");

  @Override
  public boolean isFile(String path) {
    Node node = getNode(path);
    return node != null && node.isFile();
  }

  private Node getNode(String path) {
    String[] parts = path.split("/");

    Node node = root;
    for (String part : parts) {
      node = node.getChildNamed(part);
      if (node == null) {
        return null;
      }
    }
    return node;
  }

  @Override
  public boolean isDirectory(String path) {
    Node node = getNode(path);
    return node != null && !node.isFile();
  }

  @Override
  public File getFile(String path) {
    Node node = getNode(path);
    if (node == null) {
      return null;
    }
    File file = new File(node.getContents());
    file.setFilename(path);
    return file;
  }

  @Override
  public String[] getSubdirectories(String path) {
    Node node = getNode(path);
    if (node == null) {
      return null;
    }
    List<String> subDirs = new ArrayList<String>();
    for (Node child : node.getChildren()) {
      subDirs.add(child.getName());
    }
    String[] result = new String[subDirs.size()];
    return subDirs.toArray(result);
  }

  public void insertFile(String filename, String... contents) {
    String[] parts = filename.split("/");
    Node node = root;
    for (String part : parts) {
      Node currentChild = node.getChildNamed(part);
      if (currentChild == null) {
        currentChild = new Node(part);
        node.addChild(currentChild);
      }
      node = currentChild;
    }
    node.setContents(Arrays.asList(contents));
  }

  @Override
  public boolean isExistent(String path) {
    return getNode(path) != null;
  }

  @Override
  public void save(File file) {
    insertFile(file.getFilename(), file.getLines());
  }
}
