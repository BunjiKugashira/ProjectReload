/**
 *
 */
package modularity.events;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Alexander
 *
 */
public class ThrowingEvent extends Event {
	/**
	 * @author Alexander
	 *
	 */
	public class ExceptionArray extends Exception implements
			Iterable<Exception> {

		/**
		 *
		 */
		private static final long serialVersionUID = 3653263253309276736L;
		private final HashSet<Exception> _exceptions;

		/**
		 * @param pExceptions
		 */
		public ExceptionArray(final HashSet<Exception> pExceptions) {
			_exceptions = pExceptions;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<Exception> iterator() {
			return _exceptions.iterator();
		}

		/**
		 * @return adsf
		 */
		public HashSet<Exception> toHashSet() {
			return _exceptions;
		}

	}

	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @throws ExceptionArray
	 */
	public ThrowingEvent() throws ExceptionArray {
		super(container);
		if (!getExceptions().isEmpty()) {
			throw new ExceptionArray(getExceptions());
		}
	}

	/**
	 * @param pEv
	 * @throws ExceptionArray
	 */
	public ThrowingEvent(final EventContainer pEv) throws ExceptionArray {
		super(pEv);
		if (!getExceptions().isEmpty()) {
			throw new ExceptionArray(getExceptions());
		}
	}
}
