/**
 * 
 */
package util.meta.ThreadSafeMethod;

import java.sql.Time;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import util.meta.DeadlockException;

/**
 * @author Alexander
 * @param <Args> 
 *
 */
public abstract class VoidArgs<Arg> extends ThreadSafeMethod {
	public static final class Field extends ThreadSafeMethod.Field {
		/**
		 * @param pOwner
		 * @param pName
		 */
		public Field(Object pOwner, String pName) {
			super(pOwner, pName);
		}
		public Field(Object pOwner, String pName, boolean pReadOnly) {
			super(pOwner, pName, pReadOnly);
		}
	}
	public static abstract class Arglist {
	}
	
	protected VoidArgs(ThreadSafeMethod[] pSub, Field... pVars) {
		super(pSub, pVars);
	}
	
	protected VoidArgs(Field... pVars) {
		super(pVars);
	}

	protected abstract void run(Arg pArg);
	
	public final void start(int pTimeout, Arg pArg) throws DeadlockException, TimeoutException {
		Instant inst;
		if (pTimeout > 0)
			inst = Instant.now().plusMillis(pTimeout);
		else
			inst = Instant.MAX;
		DeadlockException dexc = null;
		TimeoutException texc = null;
		try {
			pre(inst);
		} catch (DeadlockException e) {
			dexc = e;;
		} catch (TimeoutException e) {
			texc = e;
		}
		RuntimeException exc = null;
		if (texc == null && dexc == null)
			try {
				run(pArg);
			} catch (RuntimeException e) {
				exc = e;
			}
		post();
		if (texc != null)
			throw texc;
		if (dexc != null)
			throw dexc;
		if (exc != null)
			throw exc;
	}
}
