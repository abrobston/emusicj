package nz.net.kallisti.emusicj.bindingtypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * <p>This annotation is used to mark the metafile filter used for eMusic's
 * .emx files</p>
 * 
 * $Id:$
 *
 * @author robin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface EmusicEmx {}
