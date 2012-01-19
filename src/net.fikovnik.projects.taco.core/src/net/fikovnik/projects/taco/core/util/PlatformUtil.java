package net.fikovnik.projects.taco.core.util;

import java.io.File;
import java.io.IOException;

import net.fikovnik.projects.taco.core.TacoCorePlugin;

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
		pluginId = pluginId != null ? pluginId : TacoCorePlugin.PLUGIN_ID;

		return new Status(severity, pluginId, message, cause);
	}

	public static File createTempDir() throws IOException {
		File tmpFolder = File.createTempFile("TACO", "");
	
		if (!(tmpFolder.delete())) {
			throw new IOException("Could not delete temp file: "
					+ tmpFolder.getAbsolutePath());
		}
	
		if (!(tmpFolder.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ tmpFolder.getAbsolutePath());
		}
		return tmpFolder;
	}

}
