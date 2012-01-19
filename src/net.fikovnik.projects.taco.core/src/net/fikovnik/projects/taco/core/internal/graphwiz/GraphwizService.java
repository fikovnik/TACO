package net.fikovnik.projects.taco.core.internal.graphwiz;

import java.io.IOException;

import net.fikovnik.projects.taco.core.PreferenceConstants;
import net.fikovnik.projects.taco.core.graphwiz.GraphwizException;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz.GraphwizOutputType;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizBuilder;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;
import net.fikovnik.projects.taco.core.util.PlatformUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public final class GraphwizService implements IGraphwizService {

	private static final int EXPECTED_DOT_MAJOR_VERSION = 2;
	private final IEclipsePreferences prefs;

	public GraphwizService(IEclipsePreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public IGraphwiz getGraphwiz() {
		return new Graphwiz(prefs.get(PreferenceConstants.DOT_PATH,
				PreferenceConstants.DOT_PATH_DEFAULT));
	}

	@Override
	public IGraphwizBuilder createBuilder(GraphwizOutputType type) {
		return new GraphwizBuilder(getGraphwiz(), type);
	}

	@Override
	public IStatus validatePath(String dotPath) {
		IGraphwiz graphwiz = new Graphwiz(dotPath);
		try {
			int[] v = graphwiz.getVersion();
			if (v[0] >= EXPECTED_DOT_MAJOR_VERSION) {
				return Status.OK_STATUS;
			} else {
				return PlatformUtil
						.createWarning(
								String.format(
										"Unsupported version: %d.%d.%s (expecting version >= %d)",
										v[0], v[1], v[2],
										EXPECTED_DOT_MAJOR_VERSION), null, null);
			}
		} catch (IOException e) {
			return PlatformUtil.createError("Invalid executable. Unable to execute it with -V argument.",
					e, null);
		} catch (InterruptedException e) {
			return PlatformUtil.createError("Invalid executable",
					e, null);
		} catch (GraphwizException e) {
			return PlatformUtil.createError("Invalid executable. Not a graphwiz dot command.",
					e, null);
		}
	}

	@Override
	public IStatus validate() {
		return validatePath(prefs.get(PreferenceConstants.DOT_PATH,
				PreferenceConstants.DOT_PATH_DEFAULT));
	}

}
