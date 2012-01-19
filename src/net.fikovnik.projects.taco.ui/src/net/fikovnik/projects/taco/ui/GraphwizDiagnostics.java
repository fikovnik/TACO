package net.fikovnik.projects.taco.ui;

import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public final class GraphwizDiagnostics {

	private static final String GRAPHWIZ_DIAGNOSTIC_RUN = "GraphwizDiagnostics";

	private GraphwizDiagnostics() {

	}

	public static boolean diagnoze(Shell shell, IGraphwizService service) {
		IStatus status = service.validate();

		if (!status.isOK()) {
			MessageDialogWithToggle dialog = MessageDialogWithToggle
					.openYesNoCancelQuestion(
							shell,
							"TACO diagnostics",
							"Your Graphwiz configuration does not seem to be correct. Do you want to check it now? Without it it will not be possible to generate class diagrams when exporting Ecore documentation using TACO.",
							null, false, TacoUIPlugin.getDefault()
									.getPreferenceStore(),
							GRAPHWIZ_DIAGNOSTIC_RUN);
			if (dialog.getReturnCode() == IDialogConstants.YES_ID) {
				PreferenceDialog pref = PreferencesUtil
						.createPreferenceDialogOn(shell,
								TacoUIPlugin.TACO_DEF_PREFERENCE_PAGE_ID, null,
								null);
				if (pref != null)
					pref.open();
			}
			return false;
		}
		return true;
	}

}
