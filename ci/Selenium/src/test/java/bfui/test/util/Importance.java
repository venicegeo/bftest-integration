package bfui.test.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Importance {
	
	public enum Level {
		LOW, MEDIUM, HIGH, NONE
	}
	Level level() default Level.NONE;
}
