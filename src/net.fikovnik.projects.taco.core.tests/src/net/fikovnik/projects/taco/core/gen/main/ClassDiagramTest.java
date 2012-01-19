package net.fikovnik.projects.taco.core.gen.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.fikovnik.projects.taco.core.util.PlatformUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;

public final class ClassDiagramTest {

	@Test
	public void testSampleClassDiagram() throws Exception {
		URI modelURI = URI.createPlatformPluginURI("/net.fikovnik.projects.taco.core.tests/tests/graphwiz/1/model.ecore", true);
		File targetFolder = PlatformUtil.createTempDir();
		System.out.println(targetFolder.getAbsolutePath());
		
		//use model instead of model uri
		
		List<String> arguments = new ArrayList<String>();
		arguments.add("diagram.dot");
		
		ClassDiagram generator = new ClassDiagram(modelURI, targetFolder, arguments);
		generator.doGenerate(BasicMonitor.toMonitor(new NullProgressMonitor()));
	}
	
}
