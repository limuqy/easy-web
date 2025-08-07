package io.github.limuqy.easyweb.core.function;

@FunctionalInterface
public interface Func2<A, B, R> {
    R apply(A a, B b);
}
