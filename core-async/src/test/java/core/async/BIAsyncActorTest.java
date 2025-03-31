package core.async;

import core.framework.util.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * @author stevezou
 */
class BIAsyncActorTest {
    private static ActorInterface<String> testBIAsyncActor() {
        Supplier<String> stringSupplier = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "test";
        };
        return AsyncActors.combine(stringSupplier, stringSupplier, (a, b) -> a + b);
    }

    @Test
    void supply() {
        ActorInterface<String> singleAsyncActor = testBIAsyncActor();
        StopWatch stopWatch = new StopWatch();
        String supplied = singleAsyncActor.getSupplied();
        long elapsed = stopWatch.elapsed();
        Assertions.assertEquals("testtest", supplied);
        Assertions.assertTrue(elapsed < 1030000000); // elapsed should be 1000, allow 3% deviation
    }
}