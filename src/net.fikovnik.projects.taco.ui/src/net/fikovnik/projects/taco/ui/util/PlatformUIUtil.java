package net.fikovnik.projects.taco.ui.util;

import java.lang.reflect.InvocationTargetException;

import net.fikovnik.projects.taco.core.util.PlatformUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

public final class PlatformUIUtil {

	// No instance
	private PlatformUIUtil() {

	}

	/**
	 * Shows an dialog to report an exception
	 * 
	 * @param title
	 *            dialog title
	 * @param message
	 *            dialog message
	 * @param pluginId
	 *            pluginId which code caused the exception
	 * @param shell
	 *            parent shell
	 * @param t
	 *            exception
	 * @see PlatformUIUtil#showExceptionDialog(String, String, String,
	 *      Throwable)
	 * @see ErrorDialog#openError(Shell, String, String, IStatus)
	 */
	public static void showExceptionDialog(final String title,
			final String message, final String pluginId, final Shell shell,
			final Throwable t) {
		final IStatus status = new Status(IStatus.ERROR, pluginId,
				t.getLocalizedMessage(), t);

		ErrorDialog.openError(shell, title, message, status);
	}

	/**
	 * Shows an dialog to report an exception
	 * 
	 * @param title
	 *            dialog title
	 * @param message
	 *            dialog message
	 * @param pluginId
	 *            pluginId which code caused the exception
	 * @param t
	 *            exception
	 * @see PlatformUIUtil#showExceptionDialog(String, String, String, Shell,
	 *      Throwable)
	 * @see ErrorDialog#openError(Shell, String, String, IStatus)
	 */
	public static void showExceptionDialog(final String title,
			final String message, final String pluginId, final Throwable t) {
		// TODO: checking for active shell
		showExceptionDialog(title, message, pluginId, PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), t);
	}

	/**
	 * This method can be called from any thread.
	 * 
	 * @param title
	 *            of the error
	 * @param message
	 *            the error message
	 * @param status
	 *            the associated status
	 */
	public static void showErrorDialog(final String dialogTitle,
			final String message, final IStatus status) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			ErrorDialog.openError(Display.getDefault().getActiveShell(),
					dialogTitle, message, status);
		} else {
			Runnable runnable = new Runnable() {
				public void run() {
					ErrorDialog.openError(
							Display.getDefault().getActiveShell(), dialogTitle,
							message, status);
				}
			};
			Display.getDefault().asyncExec(runnable);
		}
	}

	public static void handleError(String message,
			InvocationTargetException cause, String pluginId) {
		if (cause != null) {
			handleError(message, cause.getTargetException(), pluginId);
		} else {
			handleError(message, (Throwable) null, pluginId);
		}
	}

	public static void handleError(String message, Throwable cause,
			String pluginId) {
		IStatus status = PlatformUtil.createError(message, cause, pluginId);
		handleStatus(status);
	}

	public static void handleStatus(IStatus status) {
		StatusManager.getManager().handle(status,
				StatusManager.BLOCK | StatusManager.LOG);
	}

	public static void handleStatusNoLog(IStatus status) {
		StatusManager.getManager().handle(status, StatusManager.BLOCK);
	}

	/**
	 * Type safe method to get a service using given service locator. Will never
	 * return null, instead it will throw a runtime exception when no service is
	 * available.
	 * 
	 * @param <T>
	 *            type of the service
	 * @param locator
	 *            to be used to lookup the service
	 * @param service
	 *            to get looked up
	 * @return an instance of the service
	 * @throws RuntimeException
	 *             if no such a service can be found using given service locator
	 */
	public static void handleInterruptedException(InterruptedException e,
			String pluginId) {
		IStatus status = PlatformUtil.createInfo("Action interrupted", e,
				pluginId);
		StatusManager.getManager().handle(status, StatusManager.LOG);
	}

	public static void handleInvocationException(String action,
			InvocationTargetException e, String pluginId) {
		IStatus status = PlatformUtil.createError("Invocation of: " + action
				+ " failed", e, pluginId);
		StatusManager.getManager().handle(status,
				StatusManager.BLOCK | StatusManager.LOG);
	}

	public static void handleError(Throwable cause, String pluginId) {
		handleError(cause.getMessage(), cause, pluginId);
	}

	public static void saveCombo(IDialogSettings settings, String key,
			Combo combo) {
		if (combo.getText().trim().length() > 0) {
			settings.put(key + String.valueOf(0), combo.getText().trim());
			String[] items = combo.getItems();
			int nEntries = Math.min(items.length, 5);
			for (int i = 0; i < nEntries; i++) {
				settings.put(key + String.valueOf(i + 1), items[i].trim());
			}
		}
	}

	public static void initializeCombo(IDialogSettings settings, String key,
			Combo combo) {
		for (int i = 0; i < 6; i++) {
			String curr = settings.get(key + String.valueOf(i));
			if (curr != null && combo.indexOf(curr) == -1) {
				combo.add(curr);
			}
		}
		if (combo.getItemCount() > 0)
			combo.setText(combo.getItem(0));
	}

}
