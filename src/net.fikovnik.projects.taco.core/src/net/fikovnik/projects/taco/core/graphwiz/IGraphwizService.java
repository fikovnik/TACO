package net.fikovnik.projects.taco.core.graphwiz;

import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz.GraphwizOutputType;

public interface IGraphwizService {

	public IGraphwiz getGraphwiz();

	public IGraphwizBuilder createBuilder(GraphwizOutputType type);
	
}
