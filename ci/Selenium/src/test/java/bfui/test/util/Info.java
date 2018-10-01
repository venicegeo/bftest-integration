package bfui.test.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Info {
	
	public enum Importance {
		LOW, MEDIUM, HIGH, NONE
	}
	Importance importance() default Importance.NONE;
}
