package net.fikovnik.projects.taco.ui.internal.preferences;

import net.fikovnik.projects.taco.core.PreferenceConstants;
import net.fikovnik.projects.taco.core.TacoCorePlugin;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
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
		{
			FileFieldEditor fileFieldEditor = new FileFieldEditor(
					PreferenceConstants.DOT_PATH, "Graphiwiz dot path:",
					getFieldEditorParent());
			fileFieldEditor.setEmptyStringAllowed(false);
			addField(fileFieldEditor);
		}
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {

	}

}
