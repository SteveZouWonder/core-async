package core.async;

/**
 * @author stevezou
 */

@FunctionalInterface
public interface MultiReducer<R> {
    R reduce(Object... args);
}
