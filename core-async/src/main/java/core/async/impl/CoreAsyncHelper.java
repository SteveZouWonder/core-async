package core.async.impl;

import core.async.ErrorCodes;
import core.framework.log.ErrorCode;
import core.framework.web.exception.ConflictException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author stevezou
 */
public final class CoreAsyncHelper {
    public static <R> R tryGetFuture(long mills, CompletableFuture<R> mappedFuture) {
        try {
            return mappedFuture.get(mills, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new ConflictException(e.getMessage(), ErrorCodes.TIMEOUT, e);
        } catch (Exception e) {
            if (hasErrorCode(e)) {
                throw new ConflictException(e.getMessage(), ((ErrorCode) e).errorCode(), e);
            } else {
                throw new ConflictException(e.getMessage(), ErrorCodes.INTERNAL_ERROR, e);
            }
        }
    }

    public static <R> R tryGetFuture(CompletableFuture<R> mappedFuture) {
        try {
            return mappedFuture.get();
        } catch (Exception e) {
            if (hasErrorCode(e)) {
                throw new ConflictException(e.getMessage(), ((ErrorCode) e).errorCode(), e);
            } else {
                throw new ConflictException(e.getMessage(), ErrorCodes.INTERNAL_ERROR, e);
            }
        }
    }

    private static boolean hasErrorCode(Exception e) {
        return e instanceof ErrorCode;
    }
}
