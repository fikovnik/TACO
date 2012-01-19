package net.fikovnik.projects.taco.ui;

import net.fikovnik.projects.taco.core.TacoCorePlugin;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class TacoUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.fikovnik.projects.ecoredocgen.ui"; //$NON-NLS-1$
	public static final String TACO_DEF_PREFERENCE_PAGE_ID = "net.fikovnik.projects.taco.ui.preferences.DefaultPreferences";
	// The shared instance
	private static TacoUIPlugin plugin;

	/**
	 * The constructor
	 */
	public TacoUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// TODO: DI
		GraphwizDiagnostics.diagnoze(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), TacoCorePlugin
				.getDefault().getGraphwizService());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static TacoUIPlugin getDefault() {
		return plugin;
	}
}
