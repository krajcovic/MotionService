package cz.krajcovic.motionservice;

import java.security.InvalidKeyException;

/**
 * Created by krajcovic on 1/5/18.
 */

public enum MotionCommands {
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3),

    LONG_CENTER(10);

    private int id;

    MotionCommands(int id) {
        this.id = id;
    }


    static MotionCommands valueOf(int id) throws InvalidKeyException {
        for(MotionCommands e : MotionCommands.values()) {
            if(e.id == id) {
                return e;
            }
        }

        throw new InvalidKeyException("Undefined MotionCommand");
    }

    public int getId() {
        return id;
    }
}
