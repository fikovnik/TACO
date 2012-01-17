package net.fikovnik.projects.taco.latex.ui.internal.wizard;


import net.fikovnik.projects.taco.latex.core.LatexExportProperties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class LatexGeneratorPropertyPage extends WizardPage {
	
	public static class ModelReferenceFileNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			String s = (String) value;
			
			if (s == null || s.length() == 0) {
				return ValidationStatus.error("Model reference file name must not be empty");
			} else if (!s.endsWith(".tex")) {
				return ValidationStatus.error("Model reference file name must end with .tex");
			} else {
				return ValidationStatus.ok();
			}
		}
	}
	
	private final LatexExportProperties properties;
	
	private Button btnGenerateMasterFile;
	private Label lblReferenceModelLatex;
	private Text txtModelReferenceFileName;
	private Text txtMasterFileName;
	private Group grpOptions;
	
	public LatexGeneratorPropertyPage(String pageName, LatexExportProperties properties) {
		super(pageName);
		
		this.properties = properties;
		setTitle("LaTeX Documentation Properties");
		setDescription("Specify properties for the documentation export.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		grpOptions = new Group(container, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpOptions.setLayout(new GridLayout(2, false));
		
		lblReferenceModelLatex = new Label(grpOptions, SWT.NONE);
		lblReferenceModelLatex.setText("Reference model file name:");
		
		txtModelReferenceFileName = new Text(grpOptions, SWT.BORDER);
		txtModelReferenceFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		btnGenerateMasterFile = new Button(grpOptions, SWT.CHECK);
		btnGenerateMasterFile.setText("Generate master file");
		
		
		txtMasterFileName = new Text(grpOptions, SWT.BORDER);
		txtMasterFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtMasterFileName.setSize(137, 19);
		
		DataBindingContext dbc = initDataBindings();
		WizardPageSupport.create(this, dbc);		
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext dbc = new DataBindingContext();

		// targets
		final ISWTObservableValue masterFileNameTxt = SWTObservables.observeText(txtMasterFileName, SWT.Modify);
		final ISWTObservableValue modelReferenceFileNameTxt = SWTObservables.observeText(txtModelReferenceFileName, SWT.Modify);
		final ISWTObservableValue generateMasterFileBtn = SWTObservables.observeSelection(btnGenerateMasterFile);
		
		// model
		final IObservableValue generateMasterFileModel = BeansObservables.observeValue(properties, "generateMasterFile");
		
		// @formatter:off

		// model reference file name
		dbc.bindValue(
				modelReferenceFileNameTxt, 
				BeansObservables.observeValue(properties, "modelReferenceFileName"),
				new UpdateValueStrategy()
					.setAfterGetValidator(new ModelReferenceFileNameValidator()),
				null);

		// generate master file name check box
		dbc.bindValue(
				generateMasterFileBtn,
				generateMasterFileModel);
		
		// generate master file name -> master file name enabled
		dbc.bindValue(
				SWTObservables.observeEnabled(txtMasterFileName), 
				generateMasterFileModel,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
				null);
		
		// master file name
		dbc.bindValue(
				SWTObservables.observeText(txtMasterFileName), 
				BeansObservables.observeValue(properties, "masterFileName"));

		// @formatter:on

		MultiValidator validator = new MultiValidator() {
		 	protected IStatus validate() {
		 		if (generateMasterFileBtn.getValue() == Boolean.TRUE) {
		 			String s = (String) masterFileNameTxt.getValue();
					if (s == null || s.length() == 0) {
						return ValidationStatus.error("Master file name must not be empty");
		 			} else if (!s.endsWith(".tex")) {
		 				return ValidationStatus.error("Master file name must end with .tex");
		 			} else if (s.equalsIgnoreCase((String)modelReferenceFileNameTxt.getValue())) {
		 				return ValidationStatus.error("Master file name must not be the same as model reference file name");
		 			} 
		 		}		 		
 				return ValidationStatus.ok();
		 	}
		 };
		dbc.addValidationStatusProvider(validator);
		
		return dbc;
	}
}
