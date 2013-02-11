package it.filippor.bndtools.pde.target.util;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class StringToIPathConverter extends Converter {

	public StringToIPathConverter() {
		super( String.class,IPath.class);
	}

	@Override
	public Object convert(Object fromObject) {
		return  Path.fromPortableString((String) fromObject);
	}

}
