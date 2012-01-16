package net.fikovnik.projects.taco.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public final class EcoreDocGenCorePlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.fikovnik.projects.ecoredocgen.core"; //$NON-NLS-1$

	// The shared instance
	private static EcoreDocGenCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public EcoreDocGenCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static EcoreDocGenCorePlugin getDefault() {
		return plugin;
	}
}
