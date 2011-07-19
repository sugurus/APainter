package apainter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Explanation {
	String value() default AnnotationConstant.undefined;
	String file() default AnnotationConstant.undefined;
}
