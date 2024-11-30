package ru.qwonix.empioner.bot.telegram.config;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates that an annotated method is a "Command" handler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChatCommand {
    /**
     * This is an alias for command.
     */
    @AliasFor("command")
    String value() default "";

    /**
     * Name of the command for the bot to be processed by the method.
     */
    @AliasFor("value")
    String command() default "";

}
