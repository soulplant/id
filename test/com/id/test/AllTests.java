package com.id.test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.id.editor.EditorTest;
import com.id.editor.VisualTest;
import com.id.file.FileTest;
import com.id.file.FileViewTest;
import com.id.file.GraveyardTest;
import com.id.fuzzy.FuzzyFinderTest;
import com.id.platform.InMemoryFileSystemTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ EditorTest.class, VisualTest.class, FileTest.class,
    FileViewTest.class, GraveyardTest.class, FuzzyFinderTest.class,
    InMemoryFileSystemTest.class })
public class AllTests {

}
