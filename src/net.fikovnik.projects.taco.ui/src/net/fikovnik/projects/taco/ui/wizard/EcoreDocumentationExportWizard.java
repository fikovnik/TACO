package net.fikovnik.projects.taco.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fikovnik.projects.taco.latex.main.Main;
import net.fikovnik.projects.taco.ui.EcoreDocGenUIPlugin;
import net.fikovnik.projects.taco.ui.util.PlatformUIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public final class EcoreDocumentationExportWizard extends Wizard implements
		IExportWizard {

	public final class ExportModel {
		private IFile sourceEcoreFile;
		private File targetOutput;
		private Map<String, String> properties = new HashMap<String, String>();
		
		public final IFile getSourceEcoreFile() {
			return sourceEcoreFile;
		}
		
		public final void setSourceEcoreFile(IFile sourceEcoreFile) {
			this.sourceEcoreFile = sourceEcoreFile;
		}
		
		public final File getTargetOutput() {
			return targetOutput;
		}
		
		public final void setTargetOutput(File targetOutput) {
			this.targetOutput = targetOutput;
		}
		
		public Map<String, String> getProperties() {
			return properties;
		}
	}
	
	private static final String DIALOG_SETTINGS_KEY = "EcoreDocumentationExportWizard";

	private static final String REVEL_DOC_TOGGLE = DIALOG_SETTINGS_KEY+".revealDocToggle";

	private final ExportModel model = new ExportModel();
	
	private SelectModelFileWizardPage selectPage;

	public EcoreDocumentationExportWizard() {
	
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Export Ecore Documentation");

		initDialogSettings();

		if (!selection.isEmpty()) {
			Object o = selection.getFirstElement();
			if (o instanceof IFile) {
				model.setSourceEcoreFile((IFile)o);
			}
		}
		this.selectPage = new SelectModelFileWizardPage("page", model);
	}

	private void initDialogSettings() {
		IDialogSettings settings = EcoreDocGenUIPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_KEY);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS_KEY);
		}
		setDialogSettings(section);
	}

	@Override
	public void addPages() {
		addPage(selectPage);
	}

	@Override
	public boolean performFinish() {

		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

				URI modelURI = URI.createPlatformResourceURI(model.getSourceEcoreFile().getFullPath()
						.toString(), true);
				List<Object> arguments = Collections.<Object> emptyList();

				try {
					Main generator = new Main(modelURI, model.getTargetOutput(),
							arguments);
					generator.doGenerate(BasicMonitor.toMonitor(monitor));
				} catch (IOException e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		try {
			getContainer().run(false, true, op);

			MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(getShell(),
					"Ecore Documentation Generated",
					"Ecore documentation has been generated. Do you want to reveal it?",
                    null,
                    false,
                    EcoreDocGenUIPlugin.getDefault().getPreferenceStore(),
                    REVEL_DOC_TOGGLE);
			if (dialog.getReturnCode() == IDialogConstants.YES_ID) {
				Program.launch(model.getTargetOutput().getAbsolutePath());
			}

			return true;
		} catch (InvocationTargetException e) {
			PlatformUIUtil.handleInvocationException(
					"Generating Ecore documentation", e,
					EcoreDocGenUIPlugin.PLUGIN_ID);
			return false;
		} catch (InterruptedException e) {
			return false;
		}

	}
}
