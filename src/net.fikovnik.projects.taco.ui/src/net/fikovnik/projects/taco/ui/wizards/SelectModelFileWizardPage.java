package net.fikovnik.projects.taco.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.fikovnik.projects.taco.ui.EcoreDocGenUIPlugin;
import net.fikovnik.projects.taco.ui.PlatformUIUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

	private IStructuredSelection initalSelection;

	private TableViewer ecoreModelView;
	private Combo targetFolderCombo;
	private Button browseDirectory;

	protected SelectModelFileWizardPage(String pageName,
			IStructuredSelection selection) {
		super("Select Ecore model");
		this.initalSelection = selection;

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
		ecoreModelView
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						validatePage();
					}
				});

		Label entryRuleLabel = new Label(composite, SWT.NONE);
		entryRuleLabel.setText("Destination:");
		entryRuleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1));

		targetFolderCombo = new Combo(composite, SWT.BORDER);
		targetFolderCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		targetFolderCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});

		browseDirectory = new Button(composite, SWT.NONE);
		browseDirectory.setText("Browse");
		browseDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		browseDirectory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseDestination(targetFolderCombo);
			}
		});

		setControl(composite);
		Dialog.applyDialogFont(getControl());

		initPage();
	}

	@Override
	public void dispose() {
		PlatformUIUtils.saveCombo(getDialogSettings(), C_DESTINATION, targetFolderCombo);

		super.dispose();
	}

	private void initPage() {
		PlatformUIUtils.initializeCombo(getDialogSettings(), C_DESTINATION, targetFolderCombo);

		ecoreModelView.setInput(ResourcesPlugin.getWorkspace().getRoot());
		ecoreModelView.setSelection(initalSelection, true);

		// this is here to prevent showing errors when user has not done any input
		// but allowing to show finish if it has been already well filled
		String message = checkPage();
		if (message == null) {
			validatePage();
		} else {
			setPageComplete(false);			
		}
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
			PlatformUIUtils.handleError(e, EcoreDocGenUIPlugin.PLUGIN_ID);
		}

		return ecoreFiles.toArray(new IResource[] {});
	}

	private String checkPage() {
		String message = null;

		String destination = targetFolderCombo.getText().trim();
		IFile selectedFile = getSelectedEcoreFile();

		if (selectedFile == null) {
			message = "No Ecore file selected";
		} else if (destination.length() == 0) {
			message = "Output destination is missing";
		} else if (!isValidLocation(destination)) {
			message = "Output destination is not valid (does not exist or is not writable)";
		}
		
		return message;
	}
	
	
	private void validatePage() {
		String message = checkPage();
		
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public IFile getSelectedEcoreFile() {
		IStructuredSelection selection = (IStructuredSelection) ecoreModelView
				.getSelection();
		// TODO assert
		return (IFile) selection.getFirstElement();
	}

	public File getTargetFolder() {
		File dir = new File(targetFolderCombo.getText().trim());
		return dir;
	}

	// TODO: to util
	protected static boolean isValidLocation(String location) {
		File dir = new File(location);
		return dir.canWrite() && dir.isDirectory();
	}

}
