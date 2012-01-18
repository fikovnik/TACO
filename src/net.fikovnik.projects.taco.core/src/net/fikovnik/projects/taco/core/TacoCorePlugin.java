package net.fikovnik.projects.taco.core;

import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;
import net.fikovnik.projects.taco.core.internal.graphwiz.GraphwizService;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.framework.BundleContext;

public final class TacoCorePlugin extends Plugin {

	private GraphwizService graphwizService;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "net.fikovnik.projects.taco.core"; //$NON-NLS-1$

	// The shared instance
	private static TacoCorePlugin plugin;

	/**
	 * The constructor
	 */
	public TacoCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		graphwizService = new GraphwizService(ConfigurationScope.INSTANCE.getNode(PLUGIN_ID));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		graphwizService = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TacoCorePlugin getDefault() {
		return plugin;
	}
	
	public IGraphwizService getGraphwizService() {
		// TODO: assert running
		return graphwizService;
	}
}
