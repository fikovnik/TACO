package net.fikovnik.projects.taco.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import net.fikovnik.projects.taco.core.AbstractBean;
import net.fikovnik.projects.taco.ui.TACOUIPlugin;
import net.fikovnik.projects.taco.ui.util.PlatformUIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public abstract class AbstractEcoreDocumentationExportWizard extends Wizard
		implements IExportWizard {

	protected final class ExportModel extends AbstractBean {
		private IFile sourceEcoreFile;
		private File targetOutput;

		public final IFile getSourceEcoreFile() {
			return sourceEcoreFile;
		}

		public final void setSourceEcoreFile(IFile sourceEcoreFile) {
			firePropertyChange("sourceEcoreFile", this.sourceEcoreFile,
					this.sourceEcoreFile = sourceEcoreFile);
		}

		public final File getTargetOutput() {
			return targetOutput;
		}

		public final void setTargetOutput(File targetOutput) {
			firePropertyChange("targetOutput", this.targetOutput,
					this.targetOutput = targetOutput);
		}
	}

	private static final String DIALOG_SETTINGS_KEY = "EcoreDocumentationExportWizard";

	private static final String REVEL_DOC_TOGGLE = DIALOG_SETTINGS_KEY
			+ ".revealDocToggle";

	protected final ExportModel model;

	protected SelectModelFileWizardPage selectPage;

	public AbstractEcoreDocumentationExportWizard() {
		this.model = new ExportModel();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);

		initDialogSettings();

		if (!selection.isEmpty()) {
			Object o = selection.getFirstElement();
			if (o instanceof IFile) {
				model.setSourceEcoreFile((IFile) o);
			}
		}

		this.selectPage = new SelectModelFileWizardPage("select-page", model);
	}

	private void initDialogSettings() {
		IDialogSettings settings = TACOUIPlugin.getDefault()
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

				try {
					doGenerate(monitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		try {
			getContainer().run(false, true, op);

			MessageDialogWithToggle dialog = MessageDialogWithToggle
					.openYesNoQuestion(
							getShell(),
							"Ecore Documentation Generated",
							"Ecore documentation has been generated. Do you want to reveal it?",
							null, false, TACOUIPlugin.getDefault()
									.getPreferenceStore(), REVEL_DOC_TOGGLE);
			if (dialog.getReturnCode() == IDialogConstants.YES_ID) {
				Program.launch(model.getTargetOutput().getAbsolutePath());
			}

			return true;
		} catch (InvocationTargetException e) {
			PlatformUIUtil.handleInvocationException(
					"Generating Ecore documentation", e,
					TACOUIPlugin.PLUGIN_ID);
			return false;
		} catch (InterruptedException e) {
			return false;
		}
	}

	protected abstract void doGenerate(IProgressMonitor monitor)
			throws Exception;
}
