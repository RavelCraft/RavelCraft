package com.connexal.ravelcraft.shared.util;

import com.connexal.ravelcraft.shared.RavelInstance;

public class Lock {
    private boolean isLocked = false;

    public synchronized void lock() {
        try {
            while (isLocked) {
                wait();
            }
            isLocked = true;
        } catch (InterruptedException e) {
            RavelInstance.getLogger().error("Lock exception", e);
        }
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }
}

