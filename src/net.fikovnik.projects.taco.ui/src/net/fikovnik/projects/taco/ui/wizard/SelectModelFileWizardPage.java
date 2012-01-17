package net.fikovnik.projects.taco.ui.wizard;

import static net.fikovnik.projects.taco.ui.util.DataBindingUtil.createNotNullValidator;
import static net.fikovnik.projects.taco.ui.util.DataBindingUtil.createWritableDirectoryValidator;
import static net.fikovnik.projects.taco.ui.util.DataBindingUtil.getStringToFileConverter;

import java.util.ArrayList;
import java.util.List;

import net.fikovnik.projects.taco.ui.TACOUIPlugin;
import net.fikovnik.projects.taco.ui.util.PlatformUIUtil;
import net.fikovnik.projects.taco.ui.wizard.AbstractEcoreDocumentationExportWizard.ExportModel;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
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
import org.eclipse.swt.widgets.Table;
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
		setControl(composite);

		composite.setLayout(new GridLayout(3, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Ecore models");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1));

		ecoreModelView = new TableViewer(composite, SWT.BORDER);
		Table table = ecoreModelView.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		ecoreModelView.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

		Label entryRuleLabel = new Label(composite, SWT.NONE);
		entryRuleLabel.setText("Destination:");

		targetOutputCombo = new Combo(composite, SWT.BORDER);
		targetOutputCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		browseDirectory = new Button(composite, SWT.NONE);
		browseDirectory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chooseDestination(targetOutputCombo);
			}
		});
		browseDirectory.setText("Browse");
		browseDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));

		initControls();
		DataBindingContext dbc = initDataBindings();
		WizardPageSupport.create(this, dbc);
	}

	@Override
	public void dispose() {
		PlatformUIUtil.saveCombo(getDialogSettings(), C_DESTINATION,
				targetOutputCombo);

		super.dispose();
	}

	private void initControls() {
		// TODO: convert to data binding
		PlatformUIUtil.initializeCombo(getDialogSettings(), C_DESTINATION,
				targetOutputCombo);

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
		
		ecoreModelView.setInput(ResourcesPlugin.getWorkspace().getRoot());
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
			PlatformUIUtil.handleError(e, TACOUIPlugin.PLUGIN_ID);
		}

		return ecoreFiles.toArray(new IResource[] {});
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext dbc = new DataBindingContext();
		 
		// @formatter:off
		dbc.bindValue(
				ViewersObservables.observeSingleSelection(ecoreModelView),
				BeansObservables.observeValue(model, "sourceEcoreFile"),
				new UpdateValueStrategy().setBeforeSetValidator(createNotNullValidator("No Ecore file selected.")),
				null);
		dbc.bindValue(
				SWTObservables.observeText(targetOutputCombo),
				BeansObservables.observeValue(model, "targetOutput"),
				new UpdateValueStrategy()
						.setBeforeSetValidator(createNotNullValidator("Destination directory is not set."))
						.setConverter(getStringToFileConverter())
						.setAfterConvertValidator(createWritableDirectoryValidator("Destination directory is invalid.")),
				null);
		// @formatter:on
		
		return dbc;
	}
}
