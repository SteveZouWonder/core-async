package core.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    @Test
    void supply() {
        SingleAsyncActor<String> singleAsyncActor = testStringSingleAsyncActor();
        String supplied = singleAsyncActor.getSupplied();
        Assertions.assertEquals("test", supplied);
    }

    @Test
    void map() {
        Integer supplied = testStringSingleAsyncActor().map(s -> 1).getSupplied();
        Assertions.assertEquals(1, supplied);
    }

    @Test
    void onSupplierError() {
    }

    @Test
    void onSupplierComplete() {
    }

    @Test
    void onMapperComplete() {
    }

    @Test
    void onMapperError() {
    }

    @Test
    void getSupplied() {
    }

    @Test
    void getMapped() {
    }
}