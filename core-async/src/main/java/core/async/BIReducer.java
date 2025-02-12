package core.async;

/**
 * @author stevezou
 */

@FunctionalInterface
public interface BIReducer<A, B, R> {
    R reduce(A a, B b);
}
