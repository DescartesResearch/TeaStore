package tools.descartes.petsupplystore.image.cache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.BeforeClass;

import tools.descartes.petsupplystore.image.cache.entry.SimpleEntry;

public class TestRandomReplacement {

	private class RRTest extends RandomReplacement<Dummy> {
		
		public LinkedList<SimpleEntry<Dummy>> getEntries() {
			return entries;
		}
		
	}
	
	private static long randomSeed = 100;
	private static int nrOfDummyEntries = 200;
	private static List<Dummy> entries = new ArrayList<>(nrOfDummyEntries);

	@BeforeClass
	public static void oneTimeInit() {
		Random r = new Random(randomSeed);
		entries = Stream.generate(() -> Math.abs(r.nextLong()))
				.distinct()
				.limit(nrOfDummyEntries)
				.map(l -> new Dummy(l))
				.collect(Collectors.toList());
	}
	
}
