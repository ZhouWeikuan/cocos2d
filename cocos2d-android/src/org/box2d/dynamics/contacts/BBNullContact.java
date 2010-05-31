package org.box2d.dynamics.contacts;

import org.box2d.common.BBSweep;

public class BBNullContact extends BBContact {
    public BBNullContact() {
    }

    public void evaluate() {
    }

    public float ComputeTOI(final BBSweep sweepA, final BBSweep sweepB) {
        return 1.0f;
    }
}
