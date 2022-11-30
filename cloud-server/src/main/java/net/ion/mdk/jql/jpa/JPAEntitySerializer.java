package net.ion.mdk.jql.jpa;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Stack;

public class JPAEntitySerializer extends StdSerializer<Object> {
    private final JsonSerializer<Object> serializer;
    private static final String JQL_STACK_KEY = "jql-entity-stack";

    public JPAEntitySerializer(BeanDescription beanDesc, JsonSerializer<?> serializer) {
        super((Class<Object>)beanDesc.getBeanClass());
        this.serializer = (JsonSerializer<Object>) serializer;
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Object value) {
        return value == null || getStack(provider).contains(value);
    }

    private Stack<Object> getStack(SerializerProvider provider) {
        Stack<Object> stack = (Stack<Object>) provider.getAttribute(JQL_STACK_KEY);
        if (stack == null) {
            stack = new Stack<>();
            provider.setAttribute(JQL_STACK_KEY, stack);
        }
        return stack;
    }

    @Override
    public void serialize(
            Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        Stack<Object> stack = getStack(provider);
        if (stack.contains(value)) {
            serializeIdentifier(value, gen, provider);
            return;
        }

        stack.push(value);
        try {
            serializer.serialize(value, gen, provider);
        }
        finally {
            stack.pop();
        }
    }

    private void serializeIdentifier(Object entity, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Field idField = JPAUtils.findIdField(entity.getClass());
        String name = idField.getName();
        Object value;
        try {
            idField.setAccessible(true);
            value = idField.get(entity);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        gen.writeStartObject();
        gen.writeObjectField(name, value);
        gen.writeEndObject();
    }
}