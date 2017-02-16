package bfui.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Importance {
	
	public enum Level {
		LOW, MEDIUM, HIGH
	}
	Level level() default Level.MEDIUM;
}
