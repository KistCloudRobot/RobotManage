package net.ion.mdk.jql;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface SecuredEntity {

    @Target({FIELD})
    @Retention(RUNTIME)
    @interface AuthorID {}
}
