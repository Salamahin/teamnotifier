package com.home.teamnotifier.core.responses.notification;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

class ReflectionTools {
    private ReflectionTools() {
        throw new AssertionError();
    }

    static Field getField(final Class<?> tClass, final String fieldName) {
        Optional<Field> f = Optional.empty();
        Class<?> entityClass = tClass;
        while (entityClass != null) {
            if((f = tryGetField(entityClass, fieldName)).isPresent())
                break;

            entityClass = entityClass.getSuperclass();
        }

        if (!f.isPresent())
            throw new IllegalArgumentException(String.format(
                    "No field %s in class %s hierarchy",
                    fieldName,
                    tClass.getCanonicalName())
            );

        return f.get();
    }

    static void setValueInFinalField(final Object instance, final Field f, final Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

        f.setAccessible(true);
        f.set(instance, value);
    }

    private static Optional<Field> tryGetField(final Class<?> tClass, final String fieldName) {
        try {
            return Optional.of(tClass.getDeclaredField(fieldName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
