package net.fikovnik.projects.taco.core.graphwiz;

import org.eclipse.core.runtime.IStatus;

import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz.GraphwizOutputType;

public interface IGraphwizService {

	public IGraphwiz getGraphwiz();

	public IGraphwizBuilder createBuilder(GraphwizOutputType type);
	
	public IStatus validate();
	
	public IStatus validatePath(String dotPath);
	
}
