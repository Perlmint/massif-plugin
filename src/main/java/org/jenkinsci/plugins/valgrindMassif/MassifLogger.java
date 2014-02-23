package org.jenkinsci.plugins.valgrindMassif;

import hudson.model.BuildListener;

import java.io.PrintStream;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifLogger {
    private static PrintStream LOGGER;

    public static void setLogger(BuildListener listener) {
        LOGGER = listener.getLogger();
    }

    public static void log(BuildListener listener, final String message)
    {
        listener.getLogger().println("[Massif] " + message);
    }

    public static void logFine(final String message)
    {
        if (getLOGGER() != null) {
            getLOGGER().println("[Massif] " + message);
        }
    }

    public static void logWarn(final String message)
    {
        if (getLOGGER() != null) {
            getLOGGER().println("[Massif] " + message);
        }
    }

    public static PrintStream getLOGGER() {
        return LOGGER;
    }
}
