package org.cocos2d.types;

public class NoninvertibleTransformException extends java.lang.Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 6137225240503990466L;

    /**
     * Instantiates a new non-invertible transform exception.
     *
     * @param s the error message.
     */
    public NoninvertibleTransformException(String s) {
        super(s);
    }

}
