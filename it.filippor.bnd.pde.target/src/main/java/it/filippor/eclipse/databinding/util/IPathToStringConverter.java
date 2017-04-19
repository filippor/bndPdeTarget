package it.filippor.eclipse.databinding.util;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.runtime.IPath;

public class IPathToStringConverter extends Converter {

	public IPathToStringConverter() {
		super(IPath.class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {
		return fromObject == null ?"":((IPath) fromObject).toPortableString();
	}

}
