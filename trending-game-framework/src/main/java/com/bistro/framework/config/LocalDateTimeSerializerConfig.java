package com.bistro.framework.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class LocalDateTimeSerializerConfig {
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;
    private static  DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, localDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class,localDateTimeDeserializer());
            builder.serializerByType(BigDecimal.class,new BigDecimalSerializer(BigDecimal.class));
        };
    }

    @Bean
    public ChinaStringToLocalDateTimeConverter localDateTimeConvert() {
        return source -> LocalDateTime.parse(source, df);
    }

    interface ChinaStringToLocalDateTimeConverter extends Converter<String, LocalDateTime> {

    }

    public static class BigDecimalSerializer extends ToStringSerializerBase {


        public BigDecimalSerializer(Class<?> handledType) {
            super(handledType);
        }

        @Override
        public String valueToString(Object o) {
            BigDecimal bd = (BigDecimal)o;
            return bd.stripTrailingZeros().toPlainString();
        }
    }
}
