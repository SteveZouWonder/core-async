package core.async;

import java.util.function.Supplier;

/**
 * @author stevezou
 */
public class MultiAsyncActor<T> extends AbstractAsyncActor<T> implements ActorInterface<T> {
    private MultiAsyncActor() {

    }

    public static class Builder<T> {
        private final MultiAsyncActor<T> asyncActor;

        public Builder(Class<T> resultClass) {
            this.asyncActor = new MultiAsyncActor<T>();
        }

        public Builder<T> suppliers(Supplier<?> a, Supplier<?> b, Supplier<?>... suppliers) {
            // todo impl
            return this;
        }

        public Builder<T> reducer(MultiReducer<T> reducer) {
            // todo impl
            return this;
        }

        public MultiAsyncActor<T> build() {
            return asyncActor;
        }
    }
}
