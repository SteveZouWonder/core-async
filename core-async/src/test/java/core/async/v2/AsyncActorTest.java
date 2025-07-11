package core.async.v2;

import core.framework.util.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

class AsyncActorTest {
    private static <T> AsyncActor<T> testAsyncActor(T returnVal, T fallbackVal) {
        Supplier<T> stringSupplier = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return returnVal;
        };
        return new AsyncActor<>(stringSupplier, e -> fallbackVal);
    }

    private static AsyncActor<String> testAsyncActorWithException(String fallbackStr) {
        Supplier<String> stringSupplier = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("test throw exception");
        };
        return new AsyncActor<>(stringSupplier, e -> fallbackStr);
    }

    @Test
    void combine() {
        AsyncActor<String> asyncActor1 = testAsyncActor("test", "test1");
        AsyncActor<Integer> asyncActor2 = testAsyncActor(11, 22);

        AsyncActor<String> combined = asyncActor1.combine(asyncActor2, (String a, Integer b) -> a + b);
        StopWatch stopWatch = new StopWatch();
        String supplied = combined.getSupplied();
        long elapsed = stopWatch.elapsed();
        Assertions.assertEquals("test11", supplied);
        Assertions.assertTrue(elapsed < 1030000000); // elapsed should be 1000, allow 3% deviation
    }

    @Test
    void map() {
        AsyncActor<String> asyncActor1 = testAsyncActor("test", "test1");
        AsyncActor<Integer> asyncActor2 = asyncActor1.map(s -> 11);
        AsyncActor<Integer> asyncActor3 = asyncActor2.map(v -> 22);
        StopWatch stopWatch = new StopWatch();
        Integer supplied = asyncActor3.getSupplied();
        long elapsed = stopWatch.elapsed();
        Assertions.assertEquals(22, supplied);
        Assertions.assertTrue(elapsed < 1030000000); // elapsed should be 1000, allow 3% deviation
    }

    @Test
    void onComplete() {
        AsyncActor<String> asyncActor = testAsyncActor("test", "test1");
        asyncActor.onComplete(s -> {
            throw new RuntimeException("test on complete");
        });
        Assertions.assertThrows(RuntimeException.class, asyncActor::getSupplied, "test on complete");
    }

    @Test
    void testOnCompleteWithFallback() {
        AsyncActor<String> asyncActor = testAsyncActor("test", "test1");
        asyncActor.onComplete(
                s -> {
                    throw new RuntimeException("test on complete");
                },
                e -> {
                    throw new RuntimeException(e.getMessage());
                });
        Assertions.assertThrows(RuntimeException.class, asyncActor::getSupplied, "test throw exception");
    }

    @Test
    void getSupplied() {
        AsyncActor<String> singleAsyncActor = testAsyncActor("test", "test1");
        StopWatch stopWatch = new StopWatch();
        String supplied = singleAsyncActor.getSupplied();
        long elapsed = stopWatch.elapsed();
        Assertions.assertEquals("test", supplied);
        Assertions.assertTrue(elapsed < 1030000000); // elapsed should be 1000, allow 3% deviation
    }

    @Test
    void testGetSuppliedWithFallback() {
        AsyncActor<String> singleAsyncActor = testAsyncActorWithException("test1");
        StopWatch stopWatch = new StopWatch();
        String supplied = singleAsyncActor.getSupplied();
        long elapsed = stopWatch.elapsed();
        Assertions.assertEquals("test1", supplied);
        Assertions.assertTrue(elapsed < 1030000000); // elapsed should be 1000, allow 3% deviation
    }
}