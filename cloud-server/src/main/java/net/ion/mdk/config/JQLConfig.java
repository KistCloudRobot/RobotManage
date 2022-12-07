package net.ion.mdk.config;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import net.ion.mdk.jql.jpa.JPAEntitySerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;

@Configuration
public class JQLConfig {

    /**
     * Serialize 시 무한 루프 방지를 위한 Jackson 모듈
     * 참고) @JsonBackReference 와 @JsonIdentityInfo 대체
     */
    static class JQLModule extends Hibernate5Module {
        @Override
        public void setupModule(Module.SetupContext context) {
            super.setupModule(context);
            context.addBeanSerializerModifier(new BeanSerializerModifier() {
                @Override
                public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                    Class<?> clazz = beanDesc.getBeanClass();
                    if (clazz.getAnnotation(Entity.class) != null) {
                        serializer = new JPAEntitySerializer(beanDesc, serializer);
                    }
                    return serializer;
                }
            });
        }
    }

    /**
     * @return Hibernate5Module
     */
    @Bean
    public Module datatypeHibernateModule() {
        Hibernate5Module hm = new JQLModule();
        hm.configure(Hibernate5Module.Feature.WRITE_MISSING_ENTITIES_AS_NULL, true);
        hm.configure(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        return hm;
    }
}

