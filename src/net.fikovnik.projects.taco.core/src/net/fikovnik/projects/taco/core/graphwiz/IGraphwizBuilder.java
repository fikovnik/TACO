package net.fikovnik.projects.taco.core.graphwiz;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IGraphwizBuilder {

	public void registerExistingFiles(File sourceFolder, IProgressMonitor monitor) throws IOException;

	public void regenerate(File targetFolder, boolean ignoreFailures, IProgressMonitor monitor) throws IOException, GraphwizException;

}
