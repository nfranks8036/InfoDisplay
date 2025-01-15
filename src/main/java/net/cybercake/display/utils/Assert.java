package net.cybercake.display.utils;

import java.util.concurrent.Callable;

public class Assert {

    public static <T> T throwsException(Callable<T> executable, Class<? extends Throwable> exceptionType, T defaultValue) {
        try {
            return executable.call();
        } catch (Throwable throwable) {
            if(throwable.getClass().equals(exceptionType)) return defaultValue;
            Log.line("Executable (" + Callable.class.getCanonicalName() + ") threw an exception (" + Throwable.class.getCanonicalName() + ":" + throwable +")");
        }
        return defaultValue;
    }

}
