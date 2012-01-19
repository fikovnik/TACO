package net.fikovnik.projects.taco.core.internal.graphwiz;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public final class GraphwizTest {

	private static final String dotPath = System.getProperty("GRAPHWIZ_PATH", "/usr/local/bin/dot");
	
	@Test
	public void testSampleExecution() throws Exception {
		Graphwiz graphwiz = new Graphwiz(dotPath);
		
		File tmpIn = File.createTempFile("TACO-test", ".dot");
		tmpIn.deleteOnExit();
		File tmpOut = File.createTempFile("TACO-test", ".pdf");
		tmpOut.deleteOnExit();
		
		FileUtils.copyURLToFile(getClass().getResource("sample.dot"), tmpIn);
				
		graphwiz.generate(tmpIn, tmpOut);
		
		IOUtils.contentEquals(new FileInputStream(tmpOut), getClass().getResourceAsStream("sample.dot.pdf"));
	}
	
}
