package net.fikovnik.projects.taco.core.util;

import net.fikovnik.projects.taco.core.EcoreDocGenCorePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Collection of useful methods that are somehow connected to Eclipse Platform
 * and tends to simplify some common tasks.
 * 
 * @author fikr
 * 
 */
public final class PlatformUtil {

	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static IStatus createOk(String message, Throwable cause,
			String pluginId) {
		return createStatus(IStatus.OK, message, cause, pluginId);
	}

	public static IStatus createInfo(String message, Throwable cause,
			String pluginId) {
		return createStatus(IStatus.INFO, message, cause, pluginId);
	}

	public static IStatus createWarning(String message, Throwable cause,
			String pluginId) {
		return createStatus(IStatus.WARNING, message, cause, pluginId);
	}

	public static IStatus createError(String message, Throwable cause,
			String pluginId) {
		return createStatus(IStatus.ERROR, message, cause, pluginId);
	}

	private static IStatus createStatus(int severity, String message,
			Throwable cause, String pluginId) {
		// inject some plug-in id
		pluginId = pluginId != null ? pluginId : EcoreDocGenCorePlugin.PLUGIN_ID;

		return new Status(severity, pluginId, message, cause);
	}

}
