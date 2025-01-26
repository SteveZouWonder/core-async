package core.async;

import core.framework.web.exception.ConflictException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
// todo unit test: including: 1. methods in SingleAsyncActorInterface, 2. waitMills in AbstractAsyncActor
public final class SingleAsyncActor<T, R> extends AbstractAsyncActor implements SingleAsyncActorInterface<T, R> {
    private CompletableFuture<T> supplierFuture;
    private CompletableFuture<R> mappedFuture;

    public SingleAsyncActor() {
    }

    public SingleAsyncActor(long waitMills) {
        super(waitMills);
    }

    @Override
    public void supply(Supplier<T> supplier) {
        if (Objects.nonNull(supplierFuture)) {
            throw new ConflictException("Redundant supplier, please only supply supplier once.", ErrorCodes.REDUNDANT_SUPPLIER);
        }
        supplierFuture = CompletableFuture.supplyAsync(supplier);
    }

    @Override
    public AsyncSupplier<R> map(Function<T, R> function) {
        if (Objects.nonNull(mappedFuture)) {
            throw new ConflictException("Redundant mapper, please only supply mapper once.", ErrorCodes.REDUNDANT_MAPPER);
        }
        if (Objects.isNull(supplierFuture)) {
            throw new ConflictException("Supplier is null, please use `supply(Supplier<T> supplier)` to provide the supplier first.", ErrorCodes.SUPPLIER_REQUIRED);
        }
        mappedFuture = supplierFuture.thenApply(function);
        return new AsyncSupplierImpl<>(mappedFuture);
    }

    @Override
    public void onSupplierError(ErrorHandler<T> errorHandler) {
        if (Objects.isNull(supplierFuture)) {
            throw new ConflictException("Supplier is null, please use `supply(Supplier<T> supplier)` to provide the supplier first.", ErrorCodes.SUPPLIER_REQUIRED);
        }
        supplierFuture = supplierFuture.exceptionallyAsync(errorHandler::handle);
    }

    @Override
    public void onSupplierComplete(Consumer<T> consumer) {
        if (Objects.isNull(supplierFuture)) {
            throw new ConflictException("Supplier is null, please use `supply(Supplier<T> supplier)` to provide the supplier first.", ErrorCodes.SUPPLIER_REQUIRED);
        }
        supplierFuture.thenAcceptAsync(consumer);
    }

    @Override
    public void onMapperComplete(Consumer<R> consumer) {
        if (Objects.isNull(mappedFuture)) {
            throw new ConflictException("Mapper is null, please use `map(Function<T, R> function)` to provide the mapper first.", ErrorCodes.MAPPER_REQUIRED);
        }
        mappedFuture.thenAcceptAsync(consumer);
    }

    @Override
    public void onMapperError(ErrorHandler<R> errorHandler) {
        if (Objects.isNull(mappedFuture)) {
            throw new ConflictException("Mapper is null, please use `map(Function<T, R> function)` to provide the mapper first.", ErrorCodes.MAPPER_REQUIRED);
        }
        mappedFuture = mappedFuture.exceptionallyAsync(errorHandler::handle);
    }

    @Override
    public T getT() {
        if (Objects.isNull(super.waitMills)) {
            return CoreAsyncHelper.tryGetFuture(supplierFuture);
        } else {
            return CoreAsyncHelper.tryGetFuture(super.waitMills, supplierFuture);
        }
    }

    @Override
    public R getMapped() {
        if (Objects.isNull(super.waitMills)) {
            return CoreAsyncHelper.tryGetFuture(mappedFuture);
        } else {
            return CoreAsyncHelper.tryGetFuture(super.waitMills, mappedFuture);
        }
    }
}
