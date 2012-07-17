package com.id.file;

import com.id.app.Shell;
import com.id.platform.FileSystem;

public class FilesRenameInterpreter implements File.SaveAction {
  public FilesRenameInterpreter(FileSystem fileSystem, Shell shell) {
  }

  @Override
  public void onSave(File file) {
    // TODO(koz): Implement.
    System.out.println("Interpret deltas as file moves.");
  }
}
