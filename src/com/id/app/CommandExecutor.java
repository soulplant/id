package com.id.app;

import com.id.editor.Editor;

class CommandExecutor {
  public interface Environment {
    void openFile(String filename);
  }

  private class EmptyEnvironment implements Environment {
    @Override
    public void openFile(String filename) {
      // Do nothing.
      System.out.println("open file: " + filename);
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
      environment.openFile(parts[1]);
      return;
    }
    System.out.println("unknown command: " + command);
  }
}
