package net.fikovnik.projects.taco.core.internal.graphwiz;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz.GraphwizOutputType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public final class GraphwizTest {

	private static final String dotPath = System.getProperty("GRAPHWIZ_PATH",
			"/usr/local/bin/dot");

	@Test
	public void testSampleExecution() throws Exception {
		Graphwiz graphwiz = new Graphwiz(dotPath);

		File tmpIn = File.createTempFile("TACO-test", ".dot");
		tmpIn.deleteOnExit();
		File tmpOut = File.createTempFile("TACO-test", ".pdf");
		tmpOut.deleteOnExit();

		FileUtils.copyURLToFile(getClass().getResource("sample.dot"), tmpIn);

		graphwiz.generate(tmpIn, tmpOut, GraphwizOutputType.PDF);

		IOUtils.contentEquals(new FileInputStream(tmpOut), getClass()
				.getResourceAsStream("sample.dot.pdf"));
	}

	@Test
	public void testVersion() throws Exception {
		Graphwiz graphwiz = new Graphwiz(dotPath);
		int[] v = graphwiz.getVersion();
		assertEquals(2, v[0]);
	}

	@Test
	public void testInvalidCommand() throws Exception {
		Graphwiz graphwiz = new Graphwiz("/non-existenment");
		try {
			graphwiz.getVersion();
			fail();
		} catch (IOException e) {

		}
	}

}
