package net.fikovnik.projects.taco.latex.core;

import static net.fikovnik.projects.taco.core.util.PropertySerializer.serialize;
import static net.fikovnik.projects.taco.core.util.PropertySerializer.serializeProperty;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.List;

import net.fikovnik.projects.taco.core.graphwiz.GraphwizException;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;
import net.fikovnik.projects.taco.latex.core.main.Main;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;

public final class LatexGenerator {

	// TODO: extrcat
	private static final String DOT_OUTPUT_FOLDER_NAME = "dot";
	private static final String PDF_OUTPUT_FOLDER_NAME = "figures";
	
	private final IFile ecoreFile;
	private final File targetFolder;
	private final LatexExportProperties properties;
	private final IGraphwizService graphwiz;

	private final File pdfOutputFolder;
	private final File dotOutputFolder;
	
	public LatexGenerator(IFile ecoreFile, File targetFolder, LatexExportProperties properties, IGraphwizService graphwiz) {
		this.ecoreFile = ecoreFile;
		this.targetFolder = targetFolder;
		this.properties = properties;
		this.graphwiz = graphwiz;
		
		this.pdfOutputFolder = new File(targetFolder,PDF_OUTPUT_FOLDER_NAME);
		this.dotOutputFolder = new File(targetFolder,DOT_OUTPUT_FOLDER_NAME);
	}

	public void generate(IProgressMonitor monitor) throws Exception {
		URI modelURI = URI.createPlatformResourceURI(ecoreFile.getFullPath()
				.toString(), true);
		List<Object> arguments = Collections.<Object> emptyList();
		
		Main delegate = new Main(modelURI, targetFolder, arguments);
		delegate.getProperties().add(serializeProperty(serialize(properties)).getAbsolutePath());
		
		delegate.doGenerate(BasicMonitor.toMonitor(monitor));
		
		processDotFiles();
	}

	private void processDotFiles() {
		if (!dotOutputFolder.canRead()) {
			return;
		}
		
		if (!pdfOutputFolder.exists()) {
			if (!pdfOutputFolder.mkdirs()) {
				// TODO: handle
			}
		}
		
		File[] files = dotOutputFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".dot");
			}
		});
		
		IGraphwiz g = graphwiz.getGraphwiz();
		for (File in : files) {
			try {
				String inFileName = in.getName();
				File out = new File(pdfOutputFolder, inFileName.substring(0, inFileName.lastIndexOf(".dot"))+".pdf");
				g.generate(in, out);
			} catch (GraphwizException e) {
				// TODO: handle
				e.printStackTrace();
			}
		}
	}
}
