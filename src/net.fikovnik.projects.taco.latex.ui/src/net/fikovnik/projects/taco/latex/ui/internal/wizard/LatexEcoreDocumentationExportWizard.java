package net.fikovnik.projects.taco.latex.ui.internal.wizard;


import net.fikovnik.projects.taco.latex.core.LatexExportProperties;
import net.fikovnik.projects.taco.latex.core.LatexGenerator;
import net.fikovnik.projects.taco.ui.wizard.AbstractEcoreDocumentationExportWizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public final class LatexEcoreDocumentationExportWizard extends
		AbstractEcoreDocumentationExportWizard implements IExportWizard {

	private LatexGeneratorPropertyPage propertiesPage;
	private LatexExportProperties properties;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		
		properties = new LatexExportProperties();		
		propertiesPage = new LatexGeneratorPropertyPage("properties", properties);
		
		setWindowTitle("Export LaTeX Documentation");
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(propertiesPage);
	}
	
	@Override
	protected void doGenerate(IProgressMonitor monitor) throws Exception {
		LatexGenerator generator = new LatexGenerator(model.getSourceEcoreFile(), model.getTargetOutput(), properties);
		generator.generate(monitor);
	}
	
}