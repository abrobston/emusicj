package nz.net.kallisti.emusicj.bindingtypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * <p>
 * This marks the tagger as supporting ID3 type tags
 * </p>
 * 
 * @author robin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@BindingAnnotation
public @interface ID3Tagger {
}
