package tools.descartes.petstore.image.cache.entry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestSimpleEntry.class, TestCountedEntry.class, TestTimedEntry.class
})
public class TestSuiteCacheEntries {

}
