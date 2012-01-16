package net.fikovnik.projects.taco.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import net.fikovnik.projects.taco.ui.EcoreDocGenUIPlugin;
import net.fikovnik.projects.taco.ui.util.DataBindingUtil;
import net.fikovnik.projects.taco.ui.util.PlatformUIUtil;
import net.fikovnik.projects.taco.ui.wizard.EcoreDocumentationExportWizard.ExportModel;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public final class SelectModelFileWizardPage extends WizardPage {

	private static final String C_DESTINATION = "destination";

	private final ExportModel model;

	private TableViewer ecoreModelView;
	private Combo targetOutputCombo;
	private Button browseDirectory;

	protected SelectModelFileWizardPage(String pageName, ExportModel model) {
		super("Select Ecore model");
		this.model = model;

		setTitle("Ecore Documentation Export");
		setDescription("Select Ecore file from which you would like to export documentation");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Ecore models");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1));

		ecoreModelView = new TableViewer(composite, SWT.BORDER);
		ecoreModelView.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));
		ecoreModelView.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				return findAllEcoreFiles((IWorkspaceRoot) inputElement);
			}
		});
		ecoreModelView.setLabelProvider(new WorkbenchLabelProvider() {
			@Override
			protected String decorateText(String input, Object element) {
				IResource resource = (IResource) element;
				return resource.getFullPath().toString();
			}
		});

		Label entryRuleLabel = new Label(composite, SWT.NONE);
		entryRuleLabel.setText("Destination:");
		entryRuleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1));

		targetOutputCombo = new Combo(composite, SWT.BORDER);
		targetOutputCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		browseDirectory = new Button(composite, SWT.NONE);
		browseDirectory.setText("Browse");
		browseDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		browseDirectory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseDestination(targetOutputCombo);
			}
		});

		setControl(composite);
		Dialog.applyDialogFont(getControl());

		initPage();
	}

	@Override
	public void dispose() {
		PlatformUIUtil.saveCombo(getDialogSettings(), C_DESTINATION,
				targetOutputCombo);

		super.dispose();
	}

	private void initPage() {
		PlatformUIUtil.initializeCombo(getDialogSettings(), C_DESTINATION,
				targetOutputCombo);

		ecoreModelView.setInput(ResourcesPlugin.getWorkspace().getRoot());

		DataBindingContext dbc = new DataBindingContext();

		// @formatter:off
		dbc.bindValue(
				ViewersObservables.observeSingleSelection(ecoreModelView),
				PojoObservables.observeValue(model, "sourceEcoreFile"),
				new UpdateValueStrategy().setBeforeSetValidator(DataBindingUtil
						.createNotEmptyValidator("No Ecore file selected")),
				null);

		dbc.bindValue(
				SWTObservables.observeText(targetOutputCombo),
				PojoObservables.observeValue(model, "targetOutput"),
				new UpdateValueStrategy()
						.setBeforeSetValidator(
								DataBindingUtil
										.createNotEmptyValidator("Output directory is empty"))
						.setConverter(
								DataBindingUtil.getStringToFileConverter())
						.setAfterConvertValidator(
								DataBindingUtil
										.getValidVritableDirectoryValidator()),
				null);
		// @formatter:on

		// link the binding
		WizardPageSupport.create(this, dbc);
	}

	private void chooseDestination(Combo combo) {
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SAVE);
		String path = combo.getText();
		if (path.trim().length() == 0) {
			// TODO: IoC
			path = ResourcesPlugin.getWorkspace().getRoot().getLocation()
					.toString();
		}
		dialog.setFilterPath(path);
		dialog.setText("Select Folder");
		dialog.setMessage("Select output destination folder");

		String res = dialog.open();
		if (res != null) {
			if (combo.indexOf(res) == -1)
				combo.add(res, 0);
			combo.setText(res);
		}
	}

	protected Object[] findAllEcoreFiles(IWorkspaceRoot root) {
		final List<IResource> ecoreFiles = new ArrayList<IResource>();
		try {
			root.accept(new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getType() == IResource.FILE) {
						if (resource.getFileExtension().equals("ecore")) {
							ecoreFiles.add(resource);
						}
						return false;
					} else {
						return true;
					}
				}
			});
		} catch (CoreException e) {
			PlatformUIUtil.handleError(e, EcoreDocGenUIPlugin.PLUGIN_ID);
		}

		return ecoreFiles.toArray(new IResource[] {});
	}
}
