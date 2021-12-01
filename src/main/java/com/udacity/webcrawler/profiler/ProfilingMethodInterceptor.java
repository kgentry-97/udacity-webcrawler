package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final Object object;
  private final ProfilingState profilingState;

  // TODO: You will need to add more instance fields and constructor arguments to this class.

  public ProfilingMethodInterceptor(Clock clock, Object object, ProfilingState profilingState) {
    this.clock = Objects.requireNonNull(clock);
    this.object = Objects.requireNonNull(object);
    this.profilingState = Objects.requireNonNull(profilingState);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.

    Object results;
    Instant startTime = null;
    boolean isProfiled = isMethodProfiled(method);

    if(isProfiled){
      startTime = clock.instant();
    }
    try {
      results = method.invoke(object, args);
    } catch (InvocationTargetException targetException) {
      throw targetException.getTargetException();
    } catch (IllegalAccessException accessException) {
      throw new RuntimeException(accessException);
    } finally {
      if (isProfiled) {
        Duration duration = Duration.between(startTime, clock.instant());
        profilingState.record(object.getClass(), method, duration);
      }
    }
    return results;
  }

  private boolean isMethodProfiled(Method method){
    return method.getAnnotation(Profiled.class) != null;
  }
}
