/**
 * 
 */
package util.meta.ThreadSafeMethod;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

import util.meta.DeadlockException;

/**
 * This class replaces java's normal methods. You can make the object public,
 * private, protected, static... it works just like a normal method. Except that
 * all fields that are specified in the constructor will be reserved during the
 * execution of this method, so there are no inconsistencies because of multiple
 * threads working on the same fields.
 * 
 * @author Alexander Otto
 * @param <Return>
 *            The class of the object that the start() and run() methods should
 *            return.
 * @param <Arg>
 *            The class of the argument that the start() method should accept.
 *            If start() should accept more than one argument, use a container
 *            class.
 */
public abstract class RetArgs<Return, Arg> extends ThreadSafeMethod {
	/**
	 * Objects of this class are used to represent fields.
	 * 
	 * @author Alexander Otto
	 */
	public static final class Field extends ThreadSafeMethod.Field {
		/**
		 * Constructor of the class Field. The field's identity consists of the
		 * object that's holding it and it's name.
		 * 
		 * @param pOwner
		 *            The object that's holding this field. If the field is
		 *            static use "this.class", else use "this".
		 * @param pName
		 *            The field's name. Please use the exact same string that's
		 *            representing the field in the class.
		 */
		public Field(Object pOwner, String pName) {
			super(pOwner, pName);
		}
		
		/**
		 * Constructor of the class Field. The field's identity consists of the
		 * object that's holding it and it's name.
		 * 
		 * @param pOwner
		 *            The object that's holding this field. If the field is
		 *            static use "this.class", else use "this".
		 * @param pName
		 *            The field's name. Please use the exact same string that's
		 *            representing the field in the class.
		 * @param pReadOnly
		 *            If true then the field will be reserved for reading only.
		 *            Multiple threads may read a field simultaneously as long
		 *            as it's not being written on.
		 */
		public Field(Object pOwner, String pName, boolean pReadOnly) {
			super(pOwner, pName, pReadOnly);
		}
	}
	
	/**
	 * Constructor of this class. When calling the constructor all fields that
	 * need to be reserved must be in pVars.
	 * 
	 * @param pSub
	 *            A list of sub-methods that this method will call. This is
	 *            optional, but helps preventing deadlocks.
	 * @param pVars
	 *            The fields that this method needs to reserve before running.
	 */
	protected RetArgs(ThreadSafeMethod[] pSub, Field... pVars) {
		super(pSub, pVars);
	}
	
	/**
	 * Constructor of this class. When calling the constructor all fields that
	 * need to be reserved must be in pVars.
	 * 
	 * @param pVars
	 *            The fields that this method needs to reserve before running.
	 */
	protected RetArgs(Field... pVars) {
		super(pVars);
	}
	
	/**
	 * The body of this method. Use this as if you were writing a normal method.
	 * 
	 * @param pArg
	 *            The parameter your method should accept.
	 * @return The return object of your method.
	 */
	protected abstract Return run(Arg pArg);
	
	/**
	 * The method used to execute this tread safe method. It will automatically
	 * reserve all fields, call run() and release the fields again. This method
	 * will pause thread execution if it needs to wait for one or more fields to
	 * be released.
	 * 
	 * @param pTimeout
	 *            The maximum amount of time in milliseconds to wait before
	 *            throwing a TimeoutException. Use -1 to wait infinitely. The
	 *            method can only time out while waiting for it's fields. Once
	 *            execution of run() has started it can no longer time out.
	 * @param pArg
	 *            The parameter that will be given to run(pArg)
	 * @return The object that run() returned.
	 * @throws DeadlockException
	 *             Throws a DeadlockException of this thread runs into a
	 *             deadlock with a higher or equally priorized thread. If this
	 *             exception occurs you need to return the control flow to the
	 *             beginning where no fields are reserved before trying again.
	 *             Catch and retry will crash the whole program.
	 * @throws TimeoutException
	 *             Throws a TimeoutException if the desired fields are not
	 *             available within the timeout period.
	 */
	public final Return start(int pTimeout, Arg pArg) throws DeadlockException,
	        TimeoutException {
		// Calculate the instant the wait will be considered timed out
		Instant inst;
		if (pTimeout > 0)
			inst = Instant.now().plusMillis(pTimeout);
		else
			inst = Instant.MAX;
		// Register the fields
		ThreadSafeMethod.FieldList registered = pre(inst);
		// Check if any exceptions have occurred
		DeadlockException dexc = registered.getDeadlockException();
		TimeoutException texc = registered.getTimeoutException();
		RuntimeException exc = null;
		// If no exceptions have occurred, do what the method is supposed to do
		// and catch anything that could possibly go wrong
		Return ret = null;
		if (texc == null && dexc == null) {
			try {
				ret = run(pArg);
			} catch (RuntimeException e) {
				exc = e;
			}
		}
		// Release all registered fields
		post(registered);
		// If any exceptions have occurred, throw them
		if (texc != null)
			throw texc;
		if (dexc != null)
			throw dexc;
		if (exc != null)
			throw exc;
		return ret;
	}
}
