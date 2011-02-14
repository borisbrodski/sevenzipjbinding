package net.sf.sevenzipjbinding.junit;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runners.model.Statement;

public class FailAndStackDumpOnTimeout extends Statement {
    private Statement next;
    private final long timeout;

    public FailAndStackDumpOnTimeout(Statement next, long timeout) {
        this.next = next;
        this.timeout = timeout;
    }

    @Override
    public void evaluate() throws Throwable {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Callable<Object> callable = new Callable<Object>() {
            public Object call() throws Exception {
                try {
                    next.evaluate();
                } catch (Throwable e) {
                    throw new ExecutionException(e);
                }
                return null;
            }
        };
        Future<Object> result = service.submit(callable);
        service.shutdown();
        try {
            boolean terminated = service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            if (!terminated) {
                service.shutdownNow();
            }
            result.get(0, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
        } catch (TimeoutException e) {
            throw new Exception(String.format("test timed out after %d milliseconds", timeout));
        } catch (ExecutionException e) {
            throw unwrap(e);
        }
    }

    private Throwable unwrap(Throwable e) {
        if (e instanceof ExecutionException) {
            return unwrap(e.getCause());
        }
        return e;
    }
}