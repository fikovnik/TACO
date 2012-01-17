package net.fikovnik.projects.taco.latex.core;

import static net.fikovnik.projects.taco.core.util.PropertySerializer.serialize;
import static net.fikovnik.projects.taco.core.util.PropertySerializer.serializeProperty;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.fikovnik.projects.taco.latex.core.main.Main;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;

public final class LatexGenerator {

	private final IFile ecoreFile;
	private final File targetFolder;
	private final LatexExportProperties properties;
	
	public LatexGenerator(IFile ecoreFile, File targetFolder, LatexExportProperties properties) {
		this.ecoreFile = ecoreFile;
		this.targetFolder = targetFolder;
		this.properties = properties;
	}

	public void generate(IProgressMonitor monitor) throws Exception {
		URI modelURI = URI.createPlatformResourceURI(ecoreFile.getFullPath()
				.toString(), true);
		List<Object> arguments = Collections.<Object> emptyList();
		
		Main delegate = new Main(modelURI, targetFolder, arguments);
		delegate.getProperties().add(serializeProperty(serialize(properties)).getAbsolutePath());
		
		delegate.doGenerate(BasicMonitor.toMonitor(monitor));
	}
	
}
