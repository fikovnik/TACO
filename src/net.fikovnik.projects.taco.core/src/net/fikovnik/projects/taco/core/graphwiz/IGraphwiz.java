package net.fikovnik.projects.taco.core.graphwiz;

import java.io.File;


public interface IGraphwiz {

	public static final String DOT_FILE_EXT = "dot";
	public static final String PDF_FILE_EXT = "pdf";

	public void generate(File in, File out) throws GraphwizException;

}
