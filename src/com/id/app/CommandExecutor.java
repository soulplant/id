package com.id.app;

import com.id.editor.Editor;

public class CommandExecutor {
  public interface Environment {
    void openFile(String filename);
    void reloadFile(String filename);
    void jumpToLine(int line);
  }

  private class EmptyEnvironment implements Environment {
    @Override
    public void openFile(String filename) {
      // Do nothing.
      System.out.println("open file: " + filename);
    }

    @Override
    public void jumpToLine(int lineNumber) {
      // Do nothing.
      System.out.println("jump to line: " + lineNumber);
    }

    @Override
    public void reloadFile(String filename) {
      // Do nothing.
      System.out.println("reload file: " + filename);
    }
  }

  private Environment environment = new EmptyEnvironment();

  public CommandExecutor() {
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public void execute(String command, Editor editor) {
    String[] parts = command.split("\\s");
    if (parts[0].equals("e")) {
      if (parts.length == 2) {
        environment.openFile(parts[1]);
      } else {
        // e by itself means re-open the file.
        environment.reloadFile(editor.getFilename());
      }
      return;
    } else if (isInt(parts[0])) {
      environment.jumpToLine(Integer.parseInt(parts[0]) - 1);
      return;
    }

    System.out.println("unknown command: " + command);
  }

  private boolean isInt(String text) {
    try {
      Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}
