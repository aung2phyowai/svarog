package org.signalml.codec;

public class CodecException extends Exception {

	/**
	 * 
	 */
	public CodecException() {
	}

	/**
	 * @param message
	 */
	public CodecException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CodecException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}

}
