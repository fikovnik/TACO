package net.fikovnik.projects.taco.core.internal;

import net.fikovnik.projects.taco.core.PreferenceConstants;
import net.fikovnik.projects.taco.core.TACOCorePlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public final class CorePreferencesInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs = DefaultScope.INSTANCE
				.getNode(TACOCorePlugin.PLUGIN_ID);
		
		prefs.put(PreferenceConstants.DOT_PATH,
				PreferenceConstants.DOT_PATH_DEFAULT);
	}

}
