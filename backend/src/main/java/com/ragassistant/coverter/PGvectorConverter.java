package com.ragassistant.converter;
import com.pgvector.PGvector;
import jakarta.persistence.AttributeConverter;

import jakarta.persistence.Converter;
import java.sql.SQLException;

@Converter(autoApply = false)
public class PGvectorConverter implements AttributeConverter<PGvector, Object> {

    @Override
    public Object convertToDatabaseColumn(PGvector attribute) {
        return attribute;
    }


    @Override
    public PGvector convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }

        if (dbData instanceof PGvector vector) {


            return vector;

        }

        try {
            return new PGvector(dbData.toString());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to convert database value to PGvector", e);
        }
    }


}