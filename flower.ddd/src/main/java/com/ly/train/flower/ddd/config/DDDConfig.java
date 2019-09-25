package com.ly.train.flower.ddd.config;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.scanner.DefaultClassScanner;
import com.ly.train.flower.common.scanner.DefaultMethodScanner;
import com.ly.train.flower.ddd.annotation.CommandHandler;
import com.ly.train.flower.ddd.annotation.EventHandler;
import com.ly.train.flower.ddd.proxy.MethodProxy;

/**
 * @author leeyazhou
 */
public class DDDConfig {
  private static final Logger logger = LoggerFactory.getLogger(DDDConfig.class);
  private final ConcurrentMap<Class<?>, MethodProxy> commandHandlerCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<Class<?>, MethodProxy> eventHandlerCache = new ConcurrentHashMap<>();

  public MethodProxy getCommandHandler(Class<?> clazz) {
    return commandHandlerCache.get(clazz);
  }

  public MethodProxy getEventHandler(Class<?> clazz) {
    return eventHandlerCache.get(clazz);
  }

  public void scan(String basePackage) {
    Set<Class<?>> classSet = DefaultClassScanner.getInstance().getClassList(basePackage, null);
    for (Class<?> clazz : classSet) {
      List<Method> commandHandlers =
          DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, CommandHandler.class);
      List<Method> eventHandlers =
          DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, EventHandler.class);
      dealCommandHandlers(commandHandlers, clazz);
      dealEventHandlers(eventHandlers, clazz);
    }

  }

  private void dealCommandHandlers(List<Method> commandHandlers, Class<?> target) {
    for (Method method : commandHandlers) {
      doDealCommandHandler(method, target);
    }
  }

  private void doDealCommandHandler(Method method, Class<?> target) {
    Class<?>[] types = method.getParameterTypes();
    if (types == null || types.length == 0) {
      return;
    }
    logger.info("命令处理器：{}", method);
    commandHandlerCache.putIfAbsent(types[0], new MethodProxy(target, method));
  }

  private void dealEventHandlers(List<Method> eventHandlers, Class<?> target) {
    for (Method method : eventHandlers) {
      doDealEventHandler(method, target);
    }
  }

  private void doDealEventHandler(Method method, Class<?> target) {

    Class<?>[] types = method.getParameterTypes();
    if (types == null || types.length == 0) {
      return;
    }
    logger.info("事件处理器：{}", method);
    eventHandlerCache.putIfAbsent(types[0], new MethodProxy(target, method));

  }
}
