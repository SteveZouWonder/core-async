package core.async;

/**
 * @author stevezou
 */
public abstract class AbstractAsyncActor {
    protected Long waitMills;

    public AbstractAsyncActor() {
    }

    public AbstractAsyncActor(long waitMills) {
        this.waitMills = waitMills;
    }
}
