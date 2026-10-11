package br.com.motiva.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FlagConverter implements AttributeConverter<Boolean, String> {
    @Override public String convertToDatabaseColumn(Boolean value) { return Boolean.TRUE.equals(value) ? "S" : "N"; }
    @Override public Boolean convertToEntityAttribute(String value) { return "S".equals(value); }
}
