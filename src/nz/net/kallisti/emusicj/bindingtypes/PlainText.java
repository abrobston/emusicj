package nz.net.kallisti.emusicj.bindingtypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * <p>This annotation is used to mark the file filters what will match the 
 * files that are to be read from the drop dir</p>
 * 
 * $Id:$
 *
 * @author robin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface PlainText {}
