package ru.qwonix.empioner.bot.telegram.config;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.bot.entity.TelegramBotUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


@Component
public class ChatCommandAnnotationBeanPostProcessor implements ApplicationContextAware {

    private static final Map<String, Method> COMMAND_TO_METHOD = new HashMap<>();

    private ApplicationContext context;

    public ChatCommandAnnotationBeanPostProcessor(ApplicationContext context) {
        this.context = context;
    }

    private static Class<?>[] extractParameters(Method method, String command, ChatCommand annotation) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 2) {
            throw new IllegalStateException("Number of parameters is not valid. command " + command +
                                            " from " + annotation.getClass() +
                                            " must take as its first parameter " + TelegramBotUser.class.getName() +
                                            " and " + String[].class.getName() + " as second"
            );
        }
        return parameterTypes;
    }

    @PostConstruct
    public void scanForAnnotations() {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(BotService.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            Object bean = entry.getValue();
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ChatCommand.class)) {
                    ChatCommand annotation = method.getAnnotation(ChatCommand.class);
                    String command = StringUtils.defaultIfEmpty(annotation.command(), annotation.value());
                    if (COMMAND_TO_METHOD.get(command) != null) {
                        throw new IllegalStateException("bot command already exists. Command " + command +
                                                        " is already defined in class " + annotation.getClass()
                        );
                    }
                    if (method.getModifiers() != Modifier.PUBLIC) {
                        throw new IllegalStateException("method " + method.getName() +
                                                        " from " + method.getDeclaringClass() +
                                                        " annotated by @ChatCommand must be public"
                        );
                    }
                    Class<?>[] parameterTypes = extractParameters(method, command, annotation);
                    if (!parameterTypes[0].isAssignableFrom(TelegramBotUser.class)) {
                        throw new IllegalStateException("first parameter is not valid. command " + command +
                                                        " from " + annotation.getClass() +
                                                        " must take as its first parameter " + TelegramBotUser.class.getName() +
                                                        " and " + String[].class.getName() + " as second"
                        );
                    }
                    if (!parameterTypes[1].isAssignableFrom(String[].class)) {
                        throw new IllegalStateException("second parameter is not valid. command " + command +
                                                        " from " + annotation.getClass() +
                                                        " must take as its first parameter " + TelegramBotUser.class.getName() +
                                                        " and " + String[].class.getName() + " as second"
                        );
                    }
                    COMMAND_TO_METHOD.put(command, method);
                }
            }
        }
    }

    public void handle(TelegramBotUser user, String command, String[] args) {
        Method method = COMMAND_TO_METHOD.get(command);
        if (method == null) {
            return;
        }
        try {
            method.invoke(context.getBean(method.getDeclaringClass()), user, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
