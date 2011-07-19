package apainter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Author {
	/** 作者の名前*/
	String name();
	/**作者のURL.（省略可能)*/
	String url() default AnnotationConstant.undefined;
	/**作者のemail。（省略可能)*/
	String email() default AnnotationConstant.undefined;
	/**何かメモ。（省略可能)*/
	String memo() default AnnotationConstant.undefined;
}
