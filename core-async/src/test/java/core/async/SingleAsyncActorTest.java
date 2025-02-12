package core.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author stevezou
 */
class SingleAsyncActorTest {
    private static SingleAsyncActor<String> testStringSingleAsyncActor() {
        return AsyncActors.single(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "test";
        });
    }

    private final Logger logger = LoggerFactory.getLogger(SingleAsyncActorTest.class);

    @Test
    void supply() {
        SingleAsyncActor<String> singleAsyncActor = testStringSingleAsyncActor();
        String supplied = singleAsyncActor.getSupplied();
        Assertions.assertEquals("test", supplied);
    }

    @Test
    void supplyWithFallback() {
        String message = "Test on complete";
        SingleAsyncActor<String> singleAsyncActor = AsyncActors.single(() -> {
            try {
                Thread.sleep(1000);
                throw new RuntimeException(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        String supplied = singleAsyncActor.getSupplied(e -> {
            if (e instanceof RuntimeException) {
                return "test";
            } else {
                throw new RuntimeException(message);
            }
        });

        Assertions.assertEquals("test", supplied);
    }

    @Test
    void map() {
        Integer supplied = testStringSingleAsyncActor().map(s -> 1).getSupplied();
        Assertions.assertEquals(1, supplied);
    }

    @Test
    void onComplete() {
        SingleAsyncActor<String> singleAsyncActor = testStringSingleAsyncActor();
        singleAsyncActor.onComplete(result -> {
            throw new RuntimeException("Test on complete");
        });
        Assertions.assertThrowsExactly(RuntimeException.class, singleAsyncActor::getSupplied, "Test on complete");
    }

    @Test
    void onCompleteWithErrorHandler() {
        String message = "Test on complete";
        SingleAsyncActor<String> singleAsyncActor = AsyncActors.single(() -> {
            try {
                Thread.sleep(1000);
                throw new RuntimeException(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        singleAsyncActor.onComplete(result -> {
            logger.info("OnComplete executed");
        }, exception -> {
            if (message.equals(exception.getMessage())) {
                throw new RuntimeException(message + "1");
            }
        });
        Assertions.assertThrowsExactly(RuntimeException.class, singleAsyncActor::getSupplied, message + "1");
    }
}