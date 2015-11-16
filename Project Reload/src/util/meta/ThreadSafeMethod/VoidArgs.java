/**
 * 
 */
package util.meta.ThreadSafeMethod;

import java.sql.Time;
import java.time.Instant;

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
	
	public final void start(int pTimeout, Arg pArg) throws DeadlockException {
		Instant inst = Instant.now().plusMillis(pTimeout);
		try {
			pre(inst);
		} catch (DeadlockException e) {
			throw e;
		}
		RuntimeException exc = null;
		try {
			run(pArg);
		} catch (RuntimeException e) {
			exc = e;
		}
		post();
		if (exc != null)
			throw exc;
	}
}
