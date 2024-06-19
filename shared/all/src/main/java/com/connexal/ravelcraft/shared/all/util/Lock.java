package com.connexal.ravelcraft.shared.all.util;

import com.connexal.ravelcraft.shared.all.RavelMain;

public class Lock {
    private boolean isLocked = false;

    public synchronized void lock() {
        try {
            while (isLocked) {
                wait();
            }
            isLocked = true;
        } catch (InterruptedException e) {
            RavelMain.get().getRavelLogger().error("Lock exception", e);
        }
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }
}

