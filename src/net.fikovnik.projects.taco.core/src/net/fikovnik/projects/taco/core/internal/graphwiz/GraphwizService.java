package net.fikovnik.projects.taco.core.internal.graphwiz;

import net.fikovnik.projects.taco.core.PreferenceConstants;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public final class GraphwizService implements IGraphwizService {

	private final IEclipsePreferences prefs;
	
	public GraphwizService(IEclipsePreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public IGraphwiz getGraphwiz() {
		return new Graphwiz(prefs.get(PreferenceConstants.DOT_PATH, PreferenceConstants.DOT_PATH_DEFAULT));
	}
	
}
