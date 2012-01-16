package net.fikovnik.projects.taco.ui.util;

import java.io.File;

import net.fikovnik.projects.taco.core.util.PlatformUtil;
import net.fikovnik.projects.taco.ui.EcoreDocGenUIPlugin;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public final class DataBindingUtil {

	private DataBindingUtil() {

	}

	private static final IValidator VALID_WRITABLE_DIR = new IValidator() {

		@Override
		public IStatus validate(Object value) {
			File f = (File) value;
			if (!f.isDirectory()) {
				return PlatformUtil.createError(
						"Destination directory does not exist", null,
						EcoreDocGenUIPlugin.PLUGIN_ID);
			}
			if (!f.canWrite()) {
				return PlatformUtil.createError(
						"Destination directoryt is not writable", null,
						EcoreDocGenUIPlugin.PLUGIN_ID);
			}
			return Status.OK_STATUS;
		}
	};

	private static final IConverter STRING_TO_FILE = new IConverter() {

		@Override
		public Object getToType() {
			return File.class;
		}

		@Override
		public Object getFromType() {
			return String.class;
		}

		@Override
		public Object convert(Object fromObject) {
			return new File((String) fromObject);
		}
	};

	public static IValidator getValidVritableDirectoryValidator() {
		return VALID_WRITABLE_DIR;
	}
	
	public static IConverter getStringToFileConverter() {
		return STRING_TO_FILE;
	}

	public static IValidator createNotEmptyValidator(final String message) {
		return new IValidator() {
			
			@Override
			public IStatus validate(Object value) {
				if (value == null) { 
					return PlatformUtil.createError(message, null, EcoreDocGenUIPlugin.PLUGIN_ID);
				} else {
					return Status.OK_STATUS;
				}
			}
		};
	}

}
