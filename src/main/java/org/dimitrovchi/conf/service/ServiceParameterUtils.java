/*
 * Copyright 2014 Dmitry Ovchinnikov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dimitrovchi.conf.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.env.Environment;

/**
 *
 * @author Dmitry Ovchinnikov
 */
@SuppressWarnings("unchecked")
public class ServiceParameterUtils {
        
    static AnnotationParameters annotationParameters() {
        final Class<?>[] stack = ClassResolver.CLASS_RESOLVER.getClassContext();
        final Class<?> caller = stack[3];
        final List<Class<? extends Annotation>> interfaces = new ArrayList<>();
        Class<?> topCaller = null;
        for (int i = 3; i < stack.length && caller.isAssignableFrom(stack[i]); i++) {
            final Class<?> c = stack[i];
            topCaller = stack[i];
            if (c.getTypeParameters().length != 0) {
                final TypeVariable<? extends Class<?>> var = c.getTypeParameters()[0];
                final List<Class<? extends Annotation>> bounds = new ArrayList<>(var.getBounds().length);
                for (final Type type : var.getBounds()) {
                    if (type instanceof Class<?> && ((Class<?>) type).isAnnotation()) {
                        bounds.add((Class) type);
                    }
                }
                if (bounds.size() > interfaces.size()) {
                    interfaces.clear();
                    interfaces.addAll(bounds);
                }
            }
        }
        final Map<Class<? extends Annotation>, List<Annotation>> annotationMap = new IdentityHashMap<>();
        for (int i = 3; i < stack.length && caller.isAssignableFrom(stack[i]); i++) {
            final Class<?> c = stack[i];
            for (final Class<? extends Annotation> itf : interfaces) {
                final Annotation annotation = c.getAnnotation(itf);
                if (annotation != null) {
                    List<Annotation> annotationList = annotationMap.get(itf);
                    if (annotationList == null) {
                        annotationMap.put(itf, annotationList = new ArrayList<>());
                    }
                    annotationList.add(0, annotation);
                }
            }
        }
        return new AnnotationParameters(topCaller, interfaces, annotationMap);
    }

    @SuppressWarnings({"element-type-mismatch"})
    public static <P> P mergeAnnotationParameters() {
        final AnnotationParameters aParameters = annotationParameters();
        return (P) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                aParameters.annotations.toArray(new Class[aParameters.annotations.size()]),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("toString".equals(method.getName())) {
                            return reflectToString(aParameters.topCaller.getSimpleName(), proxy);
                        }
                        final Class<?> annotationClass = method.getDeclaringClass();
                        final List<Annotation> annotations = aParameters.annotationMap.containsKey(annotationClass)
                                ? aParameters.annotationMap.get(annotationClass)
                                : Collections.<Annotation>emptyList();
                        for (final Annotation annotation : annotations) {
                            final Object value = method.invoke(annotation, args);
                            if (!Objects.deepEquals(method.getDefaultValue(), value)) {
                                return value;
                            }
                        }
                        return method.getDefaultValue();
                    }
                });
    }

    public static <P> P parameters(final String prefix, final Environment environment) {
        final AnnotationParameters aParameters = annotationParameters();
        return (P) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                aParameters.annotations.toArray(new Class[aParameters.annotations.size()]),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("toString".equals(method.getName())) {
                            return reflectToString(prefix, proxy);
                        }
                        return environment.getProperty(
                                prefix + "." + method.getName(),
                                (Class) method.getReturnType(),
                                method.getDefaultValue());
                    }
                });
    }
    
    public static String reflectToString(String name, Object proxy) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (final Method method : proxy.getClass().getMethods()) {
            if (method.getDeclaringClass() == Object.class || method.getDeclaringClass() == Proxy.class) {
                continue;
            }
            switch (method.getName()) {
                case "toString":
                case "hashCode":
                case "annotationType":
                    continue;
            }
            if (method.getParameterCount() == 0) {
                try {
                    map.put(method.getName(), method.invoke(proxy));
                } catch (ReflectiveOperationException x) {
                    throw new IllegalStateException(x);
                }
            }
        }
        return name + map;
    }

    static class AnnotationParameters {

        final Class<?> topCaller;
        final List<Class<? extends Annotation>> annotations;
        final Map<Class<? extends Annotation>, List<Annotation>> annotationMap;

        AnnotationParameters(
                Class<?> topCaller,
                List<Class<? extends Annotation>> annotations,
                Map<Class<? extends Annotation>, List<Annotation>> annotationMap) {
            this.topCaller = topCaller;
            this.annotations = annotations;
            this.annotationMap = annotationMap;
        }
    }

    static final class ClassResolver extends SecurityManager {

        @Override
        protected Class[] getClassContext() {
            return super.getClassContext();
        }

        static final ClassResolver CLASS_RESOLVER = new ClassResolver();
    }
}
