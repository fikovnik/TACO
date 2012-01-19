package net.fikovnik.projects.taco.ui.internal.preferences;

import net.fikovnik.projects.taco.core.PreferenceConstants;
import net.fikovnik.projects.taco.core.TacoCorePlugin;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;
import net.fikovnik.projects.taco.ui.util.PlatformUIUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DefaultPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Create the preference page.
	 */
	public DefaultPreferencePage() {
		super(FLAT);

		setPreferenceStore(new ScopedPreferenceStore(
				ConfigurationScope.INSTANCE, TacoCorePlugin.PLUGIN_ID));
		setDescription("TACO Default Configuration");
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		addField(new FileFieldEditor(PreferenceConstants.DOT_PATH, "Graphiwiz dot path:", getFieldEditorParent())
		// comment if need to window builder
		{
			@Override
			protected boolean doCheckState() {
				// TODO: DI
				IGraphwizService service = TacoCorePlugin.getDefault()
						.getGraphwizService();
				IStatus status = service.validatePath(getStringValue());

				if (!status.isOK()) {
					PlatformUIUtil.logStatus(status);

					if (status.getSeverity() == IStatus.ERROR) {
						getPage().setMessage(status.getMessage(),
								IMessageProvider.ERROR);
					} else {
						getPage().setMessage(status.getMessage(),
								IMessageProvider.WARNING);
					}
					return false;
				} else {
					getPage().setMessage(null, IMessageProvider.ERROR);
					getPage().setMessage(null, IMessageProvider.WARNING);
					return true;
				}
			}
		}
		// ---
		);
	}

	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {

	}

}
