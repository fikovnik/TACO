package net.fikovnik.projects.taco.core.graphwiz;

import java.io.File;
import java.io.IOException;


public interface IGraphwiz {

	public interface IGraphwizOutputType {
		public String getFileExtension();
		public String getName();
	}
	
	public enum GraphwizOutputType implements IGraphwizOutputType {
		PDF("PDF", "pdf", "PDF (Portable Document Format)"), PNG("PNG", "png","PNG (Portable Network Graphics)");
		
		private final String abbrv;
		private final String fileExtension;
		private final String name;
		
		GraphwizOutputType(String abbrv, String fileExtension, String name) {
			// TODO: assert
			this.abbrv = abbrv;
			this.fileExtension = fileExtension;
			this.name = name;
		}
		
		@Override
		public String getFileExtension() {
			return fileExtension;
		}
		
		@Override
		public String toString() {
			return abbrv;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	public static final String DOT_FILE_EXT = "dot";
	
	public void generate(File in, File out, GraphwizOutputType type) throws GraphwizException;

	public int[] getVersion() throws IOException, InterruptedException, GraphwizException;

}
