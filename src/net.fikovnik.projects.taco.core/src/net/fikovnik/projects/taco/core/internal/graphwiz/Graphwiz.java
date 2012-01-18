package net.fikovnik.projects.taco.core.internal.graphwiz;

import java.io.File;
import java.io.IOException;

import net.fikovnik.projects.taco.core.graphwiz.GraphwizException;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz;

public final class Graphwiz implements IGraphwiz {

	private final String dotPath;
	
	public Graphwiz(String dotPath) {
		this.dotPath = dotPath;
	}

	/* (non-Javadoc)
	 * @see net.fikovnik.projects.taco.core.internal.graphwiz.IGraphwitz#generatePDF(java.io.File, java.io.File)
	 */
	@Override
	public void generate(File in, File out) throws GraphwizException {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec(String.format("%s -T pdf %s -o %s", dotPath, in.getAbsolutePath(), out.getAbsolutePath()));
			int exitCode = p.waitFor();
			if (exitCode != 0) {
				// TODO: stderr
				throw new GraphwizException("Unable to generate PDF from "+in+". Dot process exited with "+exitCode);
			}
		} catch (IOException e) {
			throw new GraphwizException("Unable to generate PDF from "+in+".", e);
		} catch (InterruptedException e) {
			throw new GraphwizException("Unable to generate PDF from "+in+". Process interrupted", e);
		}
	}
	
}
