package net.ion.mdk.jql.jpa;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.proxy.pojo.BasicLazyInitializer;

import javax.persistence.EntityNotFoundException;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class JQLProxySerializer extends StdSerializer<HibernateProxy> {
    public JQLProxySerializer() {
        this(null);
    }

    public JQLProxySerializer(Class<HibernateProxy> t) {
        super(t);
    }

    @Override
    public void serialize(
            HibernateProxy value, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        Object proxiedValue = findProxied(value);
        // TO_DO: figure out how to suppress nulls, if necessary? (too late for that here)
        if (proxiedValue == null) {
            provider.defaultSerializeNull(gen);
            return;
        }
        JsonSerializer<Object> s = provider.findValueSerializer(proxiedValue.getClass());
        s.serialize(proxiedValue, gen, provider);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, HibernateProxy value) {
        return (value == null) || (findProxied(value) == null);
    }

    protected Object findProxied(HibernateProxy proxy)
    {

        boolean _forceLazyLoading = false;
        boolean _serializeIdentifier = true;
        boolean _wrappedIdentifier = true;
        boolean _nullMissingEntities = false;

        LazyInitializer init = proxy.getHibernateLazyInitializer();
        if (!_forceLazyLoading && init.isUninitialized()) {
            if (_serializeIdentifier) {
                final Object idValue = init.getIdentifier();
                if (_wrappedIdentifier) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(getIdentifierPropertyName(init), idValue);
                    return map;
                } else {
                    return idValue;
                }
            }
            return null;
        }
        try {
            return init.getImplementation();
        } catch (EntityNotFoundException e) {
            if (_nullMissingEntities) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private String getIdentifierPropertyName(final LazyInitializer init) {
        String idName;
//        if (_mapping != null) {
//            idName = _mapping.getIdentifierPropertyName(init.getEntityName());
//        } else {
        idName = null;//ProxySessionReader.getIdentifierPropertyName(init);
        if (idName == null) {
            idName = ProxyReader.getIdentifierPropertyName(init);
            if (idName == null) {
                idName = init.getEntityName();
            }
        }
//        }
        return idName;
    }

    protected static class ProxySessionReader {

        /**
         * The getSession method must be executed using reflection for compatibility purpose.
         * For efficiency keep the method cached.
         */
        protected static final Method lazyInitializerGetSessionMethod;

        static {
            try {
                lazyInitializerGetSessionMethod = LazyInitializer.class.getMethod("getSession");
            } catch (Exception e) {
                // should never happen: the class and method exists in all versions of hibernate 5
                throw new RuntimeException(e);
            }
        }

        static String getIdentifierPropertyName(LazyInitializer init) {
            final Object session;
            try{
                session = lazyInitializerGetSessionMethod.invoke(init);
            } catch (Exception e) {
                // Should never happen
                throw new RuntimeException(e);
            }
            if(session instanceof SessionImplementor){
                SessionFactoryImplementor factory = ((SessionImplementor)session).getFactory();
                return factory.getIdentifierPropertyName(init.getEntityName());
            }else if (session != null) {
                // Should never happen: session should be an instance of org.hibernate.internal.SessionImpl
                // factory = session.getClass().getMethod("getFactory").invoke(session);
                throw new RuntimeException("Session is not instance of SessionImplementor");
            }
            return null;
        }
    }

    protected static class ProxyReader {

        // static final so the JVM can inline the lookup
        private static final Field getIdentifierMethodField;

        static {
            try {
                getIdentifierMethodField = BasicLazyInitializer.class.getDeclaredField("getIdentifierMethod");
                getIdentifierMethodField.setAccessible(true);
            } catch (Exception e) {
                // should never happen: the field exists in all versions of hibernate 4 and 5
                throw new RuntimeException(e);
            }
        }

        /**
         * @return the name of the identifier property, or null if the name could not be determined
         */
        static String getIdentifierPropertyName(LazyInitializer init) {
            try {
                Method idGetter = (Method) getIdentifierMethodField.get(init);
                if (idGetter == null) {
                    return null;
                }
                String name = idGetter.getName();
                if (name.startsWith("get")) {
                    name = Introspector.decapitalize(name.substring(3));
                }
                return name;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }}
