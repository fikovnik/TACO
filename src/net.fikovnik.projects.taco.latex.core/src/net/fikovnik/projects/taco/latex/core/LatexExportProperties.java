package net.fikovnik.projects.taco.latex.core;

import net.fikovnik.projects.taco.core.AbstractBean;
import net.fikovnik.projects.taco.core.util.Property;
import net.fikovnik.projects.taco.core.util.PropertyPrefix;

@PropertyPrefix("latex")
public final class LatexExportProperties extends AbstractBean {

	// TODO: externalize
	private String modelReferenceFileName = "model-reference.tex";
	private String masterFileName = "main.tex";
	private boolean generateMasterFile = false;

	@Property
	public boolean isGenerateMasterFile() {
		return generateMasterFile;
	}

	public void setGenerateMasterFile(boolean generateMasterFile) {
		firePropertyChange("generateMasterFile", this.generateMasterFile,
				this.generateMasterFile = generateMasterFile);
	}

	@Property
	public String getModelReferenceFileName() {
		return modelReferenceFileName;
	}

	public void setModelReferenceFileName(String modelReferenceFileName) {
		firePropertyChange("modelReferenceFileName",
				this.modelReferenceFileName,
				this.modelReferenceFileName = modelReferenceFileName);
	}

	@Property
	public String getMasterFileName() {
		return masterFileName;
	}

	public void setMasterFileName(String masterFileName) {
		firePropertyChange("masterFileName", this.masterFileName,
				this.masterFileName = masterFileName);
	}
}