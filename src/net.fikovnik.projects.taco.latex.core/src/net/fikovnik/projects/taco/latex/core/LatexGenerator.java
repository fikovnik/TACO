package net.fikovnik.projects.taco.latex.core;

import static net.fikovnik.projects.taco.core.util.PropertySerializer.serialize;
import static net.fikovnik.projects.taco.core.util.PropertySerializer.serializeProperty;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz.GraphwizOutputType;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizBuilder;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;
import net.fikovnik.projects.taco.latex.core.main.Main;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;

public final class LatexGenerator {

	// TODO: extract
	// TODOL propage to mtl 
	private static final String DOT_OUTPUT_FOLDER_NAME = "dot";
	private static final String PDF_OUTPUT_FOLDER_NAME = "figures";
	
	private final IFile ecoreFile;
	private final File targetFolder;
	private final LatexExportProperties properties;
	private final IGraphwizService graphwizService;

	private final File figuresFolder;
	private final File dotFolder;
	
	public LatexGenerator(IFile ecoreFile, File targetFolder, LatexExportProperties properties, IGraphwizService graphwizService) {
		this.ecoreFile = ecoreFile;
		this.targetFolder = targetFolder;
		this.properties = properties;
		this.graphwizService = graphwizService;
		
		this.figuresFolder = new File(targetFolder,PDF_OUTPUT_FOLDER_NAME);
		this.dotFolder = new File(targetFolder,DOT_OUTPUT_FOLDER_NAME);
	}

	public void generate(IProgressMonitor monitor) throws Exception {
		SubMonitor mainMonitor = SubMonitor.convert(monitor, 110);
		
		URI modelURI = URI.createPlatformResourceURI(ecoreFile.getFullPath().toString(), true);
		List<Object> arguments = Collections.<Object> emptyList();
		
		IGraphwizBuilder builder = null;
		if (graphwizService != null) {
			builder = graphwizService.createBuilder(GraphwizOutputType.PDF);
			builder.registerExistingFiles(dotFolder, mainMonitor.newChild(10));
		}
		
		Main delegate = new Main(modelURI, targetFolder, arguments);
		delegate.getProperties().add(serializeProperty(serialize(properties)).getAbsolutePath());
		
		if (builder != null) {
			delegate.doGenerate(BasicMonitor.toMonitor(mainMonitor.newChild(50)));
		} else {
			delegate.doGenerate(BasicMonitor.toMonitor(mainMonitor.newChild(110)));			
		}
		
		if (builder != null) {
			builder.regenerate(figuresFolder, true, mainMonitor.newChild(50));
		}
		mainMonitor.done();
	}
}
