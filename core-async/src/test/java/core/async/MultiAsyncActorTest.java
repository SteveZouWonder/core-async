package core.async;

import org.junit.jupiter.api.Test;

/**
 * @author stevezou
 */
class MultiAsyncActorTest {
    @Test
    void test() {
        MultiAsyncActor.Builder<String> builder = new MultiAsyncActor.Builder<>(String.class);
    }
}
