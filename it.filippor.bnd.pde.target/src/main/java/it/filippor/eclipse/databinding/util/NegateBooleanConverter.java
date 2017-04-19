package it.filippor.eclipse.databinding.util;

import org.eclipse.core.databinding.conversion.Converter;

public class NegateBooleanConverter extends Converter {
	public NegateBooleanConverter() {
		super(Boolean.class, Boolean.class);
	}

	@Override
	public Object convert(Object fromObject) {
		return !((Boolean) fromObject);
	}
}