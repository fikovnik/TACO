package net.fikovnik.projects.taco.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import net.fikovnik.projects.taco.latex.main.Main;
import net.fikovnik.projects.taco.ui.EcoreDocGenUIPlugin;
import net.fikovnik.projects.taco.ui.PlatformUIUtils;

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

	private final static String DIALOG_SETTINGS_KEY = "EcoreDocumentationExportWizard";

	private static final String REVEL_DOC_TOGGLE = DIALOG_SETTINGS_KEY+".revealDocToggle";

	private SelectModelFileWizardPage selectPage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Export Ecore Documentation");

		initDialogSettings();

		this.selectPage = new SelectModelFileWizardPage("page", selection);
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

		final IFile file = selectPage.getSelectedEcoreFile();
		final File targetForlder = selectPage.getTargetFolder();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

				URI modelURI = URI.createPlatformResourceURI(file.getFullPath()
						.toString(), true);
				List<Object> arguments = Collections.<Object> emptyList();

				try {
					Main generator = new Main(modelURI, targetForlder,
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
				Program.launch(targetForlder.getAbsolutePath());
			}

			return true;
		} catch (InvocationTargetException e) {
			PlatformUIUtils.handleInvocationException(
					"Generating Ecore documentation", e,
					EcoreDocGenUIPlugin.PLUGIN_ID);
			return false;
		} catch (InterruptedException e) {
			return false;
		}

	}
}
