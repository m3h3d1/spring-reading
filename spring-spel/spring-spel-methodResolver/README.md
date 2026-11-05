## MethodResolver

- [MethodResolver](#methodresolver)
    - [1. Overview](#1-overview)
    - [2. Background Knowledge](#2-background-knowledge)
    - [3. Description](#3-description)
    - [4. Main Features](#4-main-features)
    - [5. Interface Source](#5-interface-source)
    - [6. Main Implementations](#6-main-implementations)
    - [7. Best Practices](#7-best-practices)
    - [8. Relationship with Other Components](#8-relationship-with-other-components)
    - [9. FAQ](#9-faq)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **Spring Expression Language (SpEL)**

   + The `MethodResolver` interface is usually used in SpEL, so you need to know the syntax and basic usage of SpEL. This includes how to use SpEL expressions in XML or annotation configuration, and how to parse SpEL expressions dynamically in code.

2. **Reflection**

   + `MethodResolver` often uses reflection to find and call the methods of an object at runtime. So you need to know Java reflection. This includes how to use classes such as `Class`, `Method` and `Field`, and how to call methods of an object through reflection.

### 3. Description

The `MethodResolver` interface is one of the key parts of the Spring Framework expression language (SpEL) module. It finds and runs methods of objects at runtime. It defines a method `resolve`. The method takes a method name and a list of argument types, and returns a `MethodExecutor` object for the method it found. An implementation of `MethodResolver` decides which method to call from the given method name and argument types. It creates a matching `MethodExecutor` instance to run the method while the expression is evaluated.

### 4. Main Features

1. **Method resolving**

   + The `MethodResolver` interface resolves method calls in expressions. It decides which method to call from the method name and the list of argument types.

2. **Dynamic method calls**

   + `MethodResolver` lets SpEL call the methods of an object at runtime. SpEL can call different methods of an object flexibly, as the expression needs.

3. **Creating method executors**

   + `MethodResolver` returns a `MethodExecutor` object for the method it found. `MethodExecutor` runs the method and returns the result.

4. **Extensibility**

   + The `MethodResolver` interface is extensible. You can write your own `MethodResolver` for specific method resolving logic. This makes SpEL more flexible and customizable when it handles different types of objects and method calls.

### 5. Interface Source

The `MethodResolver` interface defines a method resolver. In a given context, it uses the object and the method name to find a suitable method that can handle the given arguments. It returns an executor that can call the method. The interface lets you find and call methods of objects at runtime. This gives flexibility and extensibility to the expression language.

```java
/**
 * A method resolver attempts to locate a method and returns a command executor that can be
 * used to invoke that method. The command executor will be cached, but if it 'goes stale'
 * the resolvers will be called again.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface MethodResolver {

    /**
     * Within the supplied context, determine a suitable method on the supplied object that
     * can handle the specified arguments. Return a {@link MethodExecutor} that can be used
     * to invoke that method, or {@code null} if no method could be found.
     * @param context the current evaluation context
     * @param targetObject the object upon which the method is being called
     * @param name the name of the method
     * @param argumentTypes the arguments that the constructor must be able to handle
     * @return a MethodExecutor that can invoke the method, or null if the method cannot be found
     * @throws AccessException if there is a problem accessing the method
     */
    @Nullable
    MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
                           List<TypeDescriptor> argumentTypes) throws AccessException;
}
```

`ReflectiveMethodResolver` is a method resolver based on reflection. In a given context, it tries to find a suitable method on the target object and returns a method executor to call it. It uses reflection to check the methods of the target object and matches them by method name and argument types. While matching, it looks at the argument types of the method and whether type conversion is needed. It also supports registering method filters to filter the methods of a given type. When it resolves a method, it sorts the methods, handles bridge methods, and can use distance calculation to find a more exact match. If it finds a matching method, it returns a method executor. Otherwise, it returns null.

```java
/**
 * Reflection-based {@link MethodResolver} used by default in
 * {@link StandardEvaluationContext} unless explicit method resolvers have been specified.
 * 
 * Authors: Andy Clement, Juergen Hoeller, Chris Beams
 * Since version 3.0
 * @see StandardEvaluationContext#addMethodResolver(MethodResolver)
 */
public class ReflectiveMethodResolver implements MethodResolver {

	// Using distance will ensure a more accurate match is discovered,
	// more closely following the Java rules.
	private final boolean useDistance;

	@Nullable
	private Map<Class<?>, MethodFilter> filters;

	public ReflectiveMethodResolver() {
		this.useDistance = true;
	}

	/**
	 * This constructor allows the ReflectiveMethodResolver to be configured such that it
	 * will use a distance computation to check which is the better of two close matches
	 * (when there are multiple matches). Using the distance computation is intended to
	 * ensure matches are more closely representative of what a Java compiler would do
	 * when taking into account boxing/unboxing and whether the method candidates are
	 * declared to handle a supertype of the type (of the argument) being passed in.
	 * @param useDistance {@code true} if distance computation should be used when
	 * calculating matches; {@code false} otherwise
	 */
	public ReflectiveMethodResolver(boolean useDistance) {
		this.useDistance = useDistance;
	}

	/**
	 * Register a filter for methods on the given type.
	 * @param type the type to filter on
	 * @param filter a corresponding {@code MethodFilter},
	 *               or {@code null} to clear any filter for the given type
	 */
	public void registerMethodFilter(Class<?> type, @Nullable MethodFilter filter) {
		if (this.filters == null) {
			this.filters = new HashMap<>();
		}
		if (filter != null) {
			this.filters.put(type, filter);
		}
		else {
			this.filters.remove(type);
		}
	}

	/**
     * In the given context, try to find a suitable method on the target object and return
     * a method executor to call it. The method executor will be cached, but if its state
     * goes stale, the resolver will be called again.
     * <p>
     * In the given context, this method determines the suitable method that can handle the
     * specified arguments, and returns a {@link MethodExecutor} object for the method it
     * found. It returns {@code null} if no method can be found.
     * @param context the current evaluation context
     * @param targetObject the object upon which the method is being called
     * @param name the name of the method
     * @param argumentTypes the list of argument types of the method
     * @return a MethodExecutor that can invoke the method, or null if the method cannot be found
     * @throws AccessException if there is a problem accessing the method
     */
    @Override
    @Nullable
    public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
                                  List<TypeDescriptor> argumentTypes) throws AccessException {
        try {
            // Get the type converter
            TypeConverter typeConverter = context.getTypeConverter();
            // Get the class of the target object
            Class<?> type = (targetObject instanceof Class ? (Class<?>) targetObject : targetObject.getClass());
            // Get all methods of the target object
            ArrayList<Method> methods = new ArrayList<>(getMethods(type, targetObject));

            // If a filter is registered for this type, call it
            MethodFilter filter = (this.filters != null ? this.filters.get(type) : null);
            if (filter != null) {
                List<Method> filtered = filter.filter(methods);
                methods = (filtered instanceof ArrayList ? (ArrayList<Method>) filtered : new ArrayList<>(filtered));
            }

            // Sort the methods into a sensible order
            if (methods.size() > 1) {
                methods.sort((m1, m2) -> {
                    int m1pl = m1.getParameterCount();
                    int m2pl = m2.getParameterCount();
                    // Put varargs methods last
                    if (m1pl == m2pl) {
                        if (!m1.isVarArgs() && m2.isVarArgs()) {
                            return -1;
                        } else if (m1.isVarArgs() && !m2.isVarArgs()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                    return Integer.compare(m1pl, m2pl);
                });
            }

            // Resolve any bridge methods
            for (int i = 0; i < methods.size(); i++) {
                methods.set(i, BridgeMethodResolver.findBridgedMethod(methods.get(i)));
            }

            // Remove duplicate methods (possible due to resolved bridge methods)
            Set<Method> methodsToIterate = new LinkedHashSet<>(methods);

            Method closeMatch = null;
            int closeMatchDistance = Integer.MAX_VALUE;
            Method matchRequiringConversion = null;
            boolean multipleOptions = false;

            // Loop over the methods to find a suitable one
            for (Method method : methodsToIterate) {
                if (method.getName().equals(name)) {
                    int paramCount = method.getParameterCount();
                    List<TypeDescriptor> paramDescriptors = new ArrayList<>(paramCount);
                    // Build the list of parameter type descriptors
                    for (int i = 0; i < paramCount; i++) {
                        paramDescriptors.add(new TypeDescriptor(new MethodParameter(method, i)));
                    }
                    ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                    if (method.isVarArgs() && argumentTypes.size() >= (paramCount - 1)) {
                        // Handle varargs
                        matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                    } else if (paramCount == argumentTypes.size()) {
                        // Check whether the arguments match
                        matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                    }
                    if (matchInfo != null) {
                        if (matchInfo.isExactMatch()) {
                            return new ReflectiveMethodExecutor(method);
                        } else if (matchInfo.isCloseMatch()) {
                            if (this.useDistance) {
                                // Compute the distance of the match
                                int matchDistance = ReflectionHelper.getTypeDifferenceWeight(paramDescriptors, argumentTypes);
                                if (closeMatch == null || matchDistance < closeMatchDistance) {
                                    // Keep the better match
                                    closeMatch = method;
                                    closeMatchDistance = matchDistance;
                                }
                            } else {
                                // If there is no better match, treat it as a close match
                                if (closeMatch == null) {
                                    closeMatch = method;
                                }
                            }
                        } else if (matchInfo.isMatchRequiringConversion()) {
                            if (matchRequiringConversion != null) {
                                multipleOptions = true;
                            }
                            matchRequiringConversion = method;
                        }
                    }
                }
            }
            // Return the method executor that was found
            if (closeMatch != null) {
                return new ReflectiveMethodExecutor(closeMatch);
            } else if (matchRequiringConversion != null) {
                if (multipleOptions) {
                    // If there are several matches, throw an exception
                    throw new SpelEvaluationException(SpelMessage.MULTIPLE_POSSIBLE_METHODS, name);
                }
                return new ReflectiveMethodExecutor(matchRequiringConversion);
            } else {
                return null;
            }
        } catch (EvaluationException ex) {
            // An exception happened while resolving the method
            throw new AccessException("Failed to resolve method", ex);
        }
    }


	private Set<Method> getMethods(Class<?> type, Object targetObject) {
		if (targetObject instanceof Class) {
			Set<Method> result = new LinkedHashSet<>();
			// Add these so that static methods are invocable on the type: e.g. Float.valueOf(..)
			Method[] methods = getMethods(type);
			for (Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())) {
					result.add(method);
				}
			}
			// Also expose methods from java.lang.Class itself
			Collections.addAll(result, getMethods(Class.class));
			return result;
		}
		else if (Proxy.isProxyClass(type)) {
			Set<Method> result = new LinkedHashSet<>();
			// Expose interface methods (not proxy-declared overrides) for proper vararg introspection
			for (Class<?> ifc : type.getInterfaces()) {
				Method[] methods = getMethods(ifc);
				for (Method method : methods) {
					if (isCandidateForInvocation(method, type)) {
						result.add(method);
					}
				}
			}
			return result;
		}
		else {
			Set<Method> result = new LinkedHashSet<>();
			Method[] methods = getMethods(type);
			for (Method method : methods) {
				if (isCandidateForInvocation(method, type)) {
					result.add(method);
				}
			}
			return result;
		}
	}

	/**
	 * Return the set of methods for this type. The default implementation returns the
	 * result of {@link Class#getMethods()} for the given type, but subclasses may override
	 * in order to alter the results, e.g. specifying static methods declared elsewhere.
	 * @param type the class for which to return the methods
	 * @since 3.1.1
	 */
	protected Method[] getMethods(Class<?> type) {
		return type.getMethods();
	}

	/**
	 * Determine whether the given {@code Method} is a candidate for method resolution
	 * on an instance of the given target class.
	 * <p>The default implementation considers any method as a candidate, even for
	 * static methods and non-user-declared methods on the {@link Object} base class.
	 * @param method the Method to evaluate
	 * @param targetClass the concrete target class that is being introspected
	 * @since 4.3.15
	 */
	protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
		return true;
	}

}
```

### 6. Main Implementations

1. **DataBindingMethodResolver** 

   + A method resolver for data binding. It resolves methods by data binding rules, such as the rules of the Spring data binding framework. It may use more advanced techniques such as Spring DataBinding, and find the suitable method from the data binding configuration and metadata.

2. **ReflectiveMethodResolver** 

   + A general method resolver based on reflection. It uses reflection to check the methods of the target object and matches them by method name and argument types. It fits most Java objects and methods. It needs no special configuration or rules. It only resolves methods with Java reflection.

### 7. Best Practices

This example uses the Spring Expression Language (SpEL) to call a method of a custom object (`MyBean`). First, it creates a SpEL expression parser and a context object. Then it creates a `MyBean` object and sets it as a variable in the context. Next, it creates a SpEL expression that calls the `add` method of the `MyBean` object with two arguments. Finally, it evaluates the expression with the context. This calls the method of the `MyBean` object and gets the result.

```java
public class MethodResolverDemo {

    public static void main(String[] args) {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();

        // The methods defined in MyBean are called by the SpEL expression
        MyBean myBean = new MyBean();
        context.setVariable("myBean", myBean);

        // Create a SpEL expression that calls a method of MyBean
        SpelExpression expression = (SpelExpression) parser.parseExpression("#myBean.add(10, 5)");

        // Set the context for the expression and compute the result
        int result = (int) expression.getValue(context);

        // Print the result
        System.out.println("result = " + result);
    }
}
```

 The `MyBean` class defines a simple method `add`. It takes two integer arguments and returns their sum.

```java
public class MyBean {
    public int add(int a, int b) {
        return a + b;
    }
}
```

Output: `MethodResolverDemo` calls the `add` method of the `MyBean` object with two arguments (10 and 5). So the result is 10 + 5 = 15.

```java
result = 15
```

### 8. Relationship with Other Components

1. **StandardEvaluationContext**

   + The `MethodResolver` interface is usually used with the `StandardEvaluationContext` class. `StandardEvaluationContext` gives the runtime context of a SpEL expression. It can store variables, functions and other information. You can also set custom method resolvers on it, including implementations of the `MethodResolver` interface.

2. **ExpressionParser**

   + The `ExpressionParser` interface parses SpEL expressions. In SpEL, you can use an implementation of `ExpressionParser`, such as `SpelExpressionParser`, to parse expressions. When you evaluate a SpEL expression, you usually give an `EvaluationContext`. It may contain one or more `MethodResolver` objects to resolve method calls in the expression.

3. **MethodExecutor**

   + The `resolve` method of the `MethodResolver` interface returns a `MethodExecutor` object, which runs the method it found. `MethodExecutor` is an interface. It defines the `execute` method to run the method. The implementation class provides the real logic.

4. **ReflectiveMethodResolver** & **DataBindingMethodResolver**

   + These two classes implement the `MethodResolver` interface. They resolve methods based on reflection and in a data binding environment. They implement `MethodResolver` and give the real method resolving logic. When you use SpEL expressions, you can use one of these two classes, or write your own method resolver to resolve method calls.

### 9. FAQ

1. **How do I write a custom method resolver?**

   - Implement the `MethodResolver` interface and override its `resolve` method with your own method resolving logic. Then register the custom method resolver in `StandardEvaluationContext`.

2. **How is the priority of method resolvers decided?**

   - In `StandardEvaluationContext`, you can register several method resolvers. They are called in the order they were registered. The resolvers are called one by one until a matching method is found.

3. **How are overloaded methods handled?**

   - When a method is overloaded, `MethodResolver` uses the list of argument types to find the best method. When it resolves a method call, it tries to match all possible methods and picks the best match by how well the argument types match.

4. **How are varargs methods handled?**

   - Varargs methods (for example parameters defined with `...`) need special handling when methods are resolved. The method resolver must check that the number and types of arguments match the method definition, and handle the varargs case correctly.

5. **How is type conversion of method arguments handled?**

   - When the type of a method parameter does not exactly match the type of the given argument, type conversion may be needed. The method resolver can use the registered type converter to convert the argument types.

6. **How are bridge methods handled?**

   - Bridge methods are a special case of Java generics. They can cause overloading or matching errors. The method resolver must handle bridge methods correctly to find the right method to call.

7. **How are static method calls handled?**

   - When a static method is called, the method resolver must take static methods into account. It must handle static method calls correctly to find and call the right static method.
