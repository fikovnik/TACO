package net.fikovnik.projects.taco.ui.util;

import static org.eclipse.core.databinding.validation.ValidationStatus.error;
import static org.eclipse.core.databinding.validation.ValidationStatus.ok;

import java.io.File;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

public final class DataBindingUtil {

	public static class NotNullValidator implements IValidator {
		private final String message;

		public NotNullValidator(String message) {
			this.message = message;
		}

		@Override
		public IStatus validate(Object value) {
			if (value == null) { 
				return error(message);
			} else {
				return ok();
			}
		}
	}
	
	public static final class StringToFileConverter implements IConverter {
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
	}

	public static final class WritableDirectoryValidator implements IValidator {
		private final String messagePrefix;
		
		public WritableDirectoryValidator(String messagePrefix) {
			this.messagePrefix = messagePrefix;
		}

		@Override
		public IStatus validate(Object value) {
			File f = (File) value;
			if (f == null) {
				return error(
						messagePrefix+" Not directory set");				
			} else if (!f.isDirectory()) {
				return error(
						messagePrefix+" Directory does not exist");
			} else if (!f.canWrite()) {
				return error(
						messagePrefix+" Directory is not writable");
			}
			return ok();
		}
	}

	private DataBindingUtil() {

	}

	private static final IConverter STRING_TO_FILE_CONVERTER = new StringToFileConverter();

	public static IValidator createWritableDirectoryValidator(String messagePrefix) {
		return new WritableDirectoryValidator(messagePrefix);
	}
	
	public static IConverter getStringToFileConverter() {
		return STRING_TO_FILE_CONVERTER;
	}

	public static IValidator createNotNullValidator(final String message) {
		return new NotNullValidator(message);
	}

}
