package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final Map<Class<?>, Object> appComponentsByType = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<ComponentDescriptor> descriptors = new ArrayList<>();
            Method[] declaredMethods = configClass.getDeclaredMethods();
            for (int i = 0; i < declaredMethods.length; i++) {
                Method m = declaredMethods[i];
                AppComponent ann = m.getAnnotation(AppComponent.class);
                if (ann != null) {
                    descriptors.add(new ComponentDescriptor(m, ann.order(), ann.name(), i));
                }
            }
            descriptors.sort(java.util.Comparator
                    .comparingInt(ComponentDescriptor::order)
                    .thenComparingInt(ComponentDescriptor::declarationIndex));
            for (ComponentDescriptor d : descriptors) {
                Method m = d.method();
                m.setAccessible(true);
                Class<?>[] paramTypes = m.getParameterTypes();
                Object[] args = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    Object dep = resolveDependency(paramTypes[i]);
                    if (dep == null) {
                        throw new IllegalArgumentException("Cannot resolve dependency " + paramTypes[i].getName() + " for component " + d.name());
                    }
                    args[i] = dep;
                }
                Object component = m.invoke(configInstance, args);
                registerComponent(d.name(), component);
                appComponents.add(component);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process config " + configClass.getName(), e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Object resolveDependency(Class<?> type) {
        List<Object> candidates = new ArrayList<>();
        for (Object comp : appComponents) {
            if (type.isAssignableFrom(comp.getClass())) {
                candidates.add(comp);
            }
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        return null;
    }

    private void registerComponent(String name, Object component) {
        if (appComponentsByName.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate component name: " + name);
        }
        appComponentsByName.put(name, component);
        Class<?> clazz = component.getClass();
        appComponentsByType.put(clazz, component);
        for (Class<?> iface : clazz.getInterfaces()) {
            appComponentsByType.putIfAbsent(iface, component);
        }
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            appComponentsByType.putIfAbsent(superClass, component);
            superClass = superClass.getSuperclass();
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> candidates = new ArrayList<>();
        for (Object comp : appComponents) {
            if (componentClass.isAssignableFrom(comp.getClass())) {
                candidates.add(comp);
            }
        }
        if (candidates.size() == 1) {
            return componentClass.cast(candidates.get(0));
        }
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No component found for " + componentClass.getName());
        }
        throw new IllegalArgumentException("Multiple components found for " + componentClass.getName());
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        Object comp = appComponentsByName.get(componentName);
        if (comp == null) {
            throw new IllegalArgumentException("No component found with name " + componentName);
        }
        @SuppressWarnings("unchecked")
        C result = (C) comp;
        return result;
    }

    private record ComponentDescriptor(Method method, int order, String name, int declarationIndex) {}
}
