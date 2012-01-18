package net.fikovnik.projects.taco.core.graphwiz;

import java.io.File;


public interface IGraphwiz {

	public void generate(File in, File out) throws GraphwizException;

}
