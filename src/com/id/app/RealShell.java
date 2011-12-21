package com.id.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RealShell implements Shell {
  private final File workingDirectory;

  public RealShell(File workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  @Override
  public List<String> exec(String command) {
    try {
      Process process = Runtime.getRuntime().exec(command, null, workingDirectory);
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      List<String> result = new ArrayList<String>();
      String line;
      try {
        process.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
        return null;
      }
      while ((line = br.readLine()) != null) {
        result.add(line);
      }
//      System.out.println("Command[" + workingDirectory + "] = " + command);
//      System.out.println("results[" + process.exitValue() + "] = " + result);
      return result;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
