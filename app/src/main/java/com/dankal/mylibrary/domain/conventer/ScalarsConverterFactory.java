package com.dankal.mylibrary.domain.conventer;

/**
 * Created by IvanCai on 2016/2/22.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} for strings and both primitives and their boxed types
 * to {@code text/plain} bodies.
 */
public final class ScalarsConverterFactory extends Converter.Factory {
    public static ScalarsConverterFactory create() {
        return new ScalarsConverterFactory();
    }

    private ScalarsConverterFactory() {
    }

    @Override public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                                    Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (type == String.class
                || type == boolean.class
                || type == Boolean.class
                || type == byte.class
                || type == Byte.class
                || type == char.class
                || type == Character.class
                || type == double.class
                || type == Double.class
                || type == float.class
                || type == Float.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == short.class
                || type == Short.class) {
            return ScalarRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == String.class) {
            return ScalarResponseBodyConverters.StringResponseBodyConverter.INSTANCE;
        }
        if (type == Boolean.class) {
            return ScalarResponseBodyConverters.BooleanResponseBodyConverter.INSTANCE;
        }
        if (type == Byte.class) {
            return ScalarResponseBodyConverters.ByteResponseBodyConverter.INSTANCE;
        }
        if (type == Character.class) {
            return ScalarResponseBodyConverters.CharacterResponseBodyConverter.INSTANCE;
        }
        if (type == Double.class) {
            return ScalarResponseBodyConverters.DoubleResponseBodyConverter.INSTANCE;
        }
        if (type == Float.class) {
            return ScalarResponseBodyConverters.FloatResponseBodyConverter.INSTANCE;
        }
        if (type == Integer.class) {
            return ScalarResponseBodyConverters.IntegerResponseBodyConverter.INSTANCE;
        }
        if (type == Long.class) {
            return ScalarResponseBodyConverters.LongResponseBodyConverter.INSTANCE;
        }
        if (type == Short.class) {
            return ScalarResponseBodyConverters.ShortResponseBodyConverter.INSTANCE;
        }
        return null;
    }
}