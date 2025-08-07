package com.lingmu.easyweb.core.function;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface QSupplier<T> extends Supplier<T>, Serializable {
}
