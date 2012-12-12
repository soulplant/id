package com.id.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.id.app.ControllerTest;
import com.id.app.HighlightStateTest;
import com.id.app.ListModelTest;
import com.id.editor.CachingHighlightTest;
import com.id.editor.EditorFileViewTest;
import com.id.editor.EditorTest;
import com.id.editor.EditorTypingTest;
import com.id.editor.MinibufferTest;
import com.id.editor.StackListTest;
import com.id.editor.VisualTest;
import com.id.events.KeyStrokeParserTest;
import com.id.events.KeyStrokeTest;
import com.id.file.FileTest;
import com.id.file.FileViewTest;
import com.id.file.GraveyardTest;
import com.id.file.PatchworkTest;
import com.id.file.TokenCounterTest;
import com.id.file.TrieTest;
import com.id.fuzzy.FuzzyFinderTest;
import com.id.platform.InMemoryFileSystemTest;
import com.id.util.UtilTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EditorTest.class, VisualTest.class, FileTest.class,
    FileViewTest.class, GraveyardTest.class, FuzzyFinderTest.class,
    InMemoryFileSystemTest.class, EditorTypingTest.class,
    CachingHighlightTest.class, MinibufferTest.class, EditorFileViewTest.class,
    ControllerTest.class, KeyStrokeParserTest.class, PatchworkTest.class,
    ListModelTest.class, TokenCounterTest.class, TrieTest.class,
    HighlightStateTest.class, UtilTest.class, StackListTest.class,
    KeyStrokeTest.class })
public class AllTests {
}
