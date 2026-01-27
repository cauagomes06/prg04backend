package com.fithub.fithub_api.infraestructure.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;


@Component
public class ObjectMapperUtil {

    private static final ModelMapper  MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();
    }
    public<Input, Output> Output map(final Input object,
                                     final Class<Output> clazz) {

        MODEL_MAPPER.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        Output c = MODEL_MAPPER.map(object, clazz);

        return c;
    }
}
