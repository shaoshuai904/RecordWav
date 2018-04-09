package com.maple.recorder;

/**
 * A {@code ThreadAction} is an action which going to be executed on the implementer thread.
 *
 * @author Kailash Dabhi
 * @date 25-07-2016
 */
interface ThreadAction {
    /**
     * Execute {@code runnable} action on implementer {@code Thread}
     */
    void execute(Runnable action);
}

