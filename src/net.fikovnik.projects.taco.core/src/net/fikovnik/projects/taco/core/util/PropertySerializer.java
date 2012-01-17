package net.fikovnik.projects.taco.core.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public final class PropertySerializer {

	private PropertySerializer() {
		// TODO Auto-generated constructor stub
	}
	
	// TODO; crate custom exception
	public static Properties serialize(Object bean) throws Exception {
		Properties properties = new Properties();
		
		BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
		
		String prefix = "";
		PropertyPrefix pprefix = bean.getClass().getAnnotation(PropertyPrefix.class);
		if (pprefix != null) {
			prefix = pprefix.value()+".";
		}
		
		for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
			Method m = prop.getReadMethod();
			if (m == null) {
				continue;
			}
			Property p = m.getAnnotation(Property.class);
			if (p != null) {
				String key = p.value();
				if (key == null || key.length() == 0) {
					key = prop.getName();
				}
				
				Object value = m.invoke(bean, (Object[]) null);
				properties.put(prefix+key, value.toString());
			}
		}
		
		return properties;
	}

	public static File serializeProperty(Properties properties)
			throws IOException {
		// TODO: assert properties
		File f = File.createTempFile("TACO", ".properties");
		f.deleteOnExit();

		properties.store(new FileWriter(f), null);

		return f;
	}


}
