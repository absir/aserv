package com.absir.orm.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Transactional {

	/**
	 * The TxType element of the Transactional annotation indicates whether a
	 * bean method is to be executed within a transaction context.
	 */
	TxType value() default TxType.REQUIRED;

	/**
	 * The TxType element of the annotation indicates whether a bean method is
	 * to be executed within a transaction context where the values provide the
	 * following corresponding behavior.
	 */
	public enum TxType {
		/**
		 * <p>
		 * If called outside a transaction context, the interceptor must begin a
		 * new JTA transaction, the managed bean method execution must then
		 * continue inside this transaction context, and the transaction must be
		 * completed by the interceptor.
		 * </p>
		 * <p>
		 * If called inside a transaction context, the managed bean method
		 * execution must then continue inside this transaction context.
		 * </p>
		 */
		REQUIRED,

		/**
		 * <p>
		 * If called outside a transaction context, the interceptor must begin a
		 * new JTA transaction, the managed bean method execution must then
		 * continue inside this transaction context, and the transaction must be
		 * completed by the interceptor.
		 * </p>
		 * <p>
		 * If called inside a transaction context, the current transaction
		 * context must be suspended, a new JTA transaction will begin, the
		 * managed bean method execution must then continue inside this
		 * transaction context, the transaction must be completed, and the
		 * previously suspended transaction must be resumed.
		 * </p>
		 */
		REQUIRES_NEW,

		/**
		 * <p>
		 * If called outside a transaction context, a TransactionalException
		 * with a nested TransactionRequiredException must be thrown.
		 * </p>
		 * <p>
		 * If called inside a transaction context, managed bean method execution
		 * will then continue under that context.
		 * </p>
		 */
		MANDATORY,

		/**
		 * <p>
		 * If called outside a transaction context, managed bean method
		 * execution must then continue outside a transaction context.
		 * </p>
		 * <p>
		 * If called inside a transaction context, the managed bean method
		 * execution must then continue inside this transaction context.
		 * </p>
		 */
		SUPPORTS,

		/**
		 * <p>
		 * If called outside a transaction context, managed bean method
		 * execution must then continue outside a transaction context.
		 * </p>
		 * <p>
		 * If called inside a transaction context, the current transaction
		 * context must be suspended, the managed bean method execution must
		 * then continue outside a transaction context, and the previously
		 * suspended transaction must be resumed by the interceptor that
		 * suspended it after the method execution has completed.
		 * </p>
		 */
		NOT_SUPPORTED,

		/**
		 * <p>
		 * If called outside a transaction context, managed bean method
		 * execution must then continue outside a transaction context.
		 * </p>
		 * <p>
		 * If called inside a transaction context, a TransactionalException with
		 * a nested InvalidTransactionException must be thrown.
		 * </p>
		 */
		NEVER
	}

	/**
	 * The rollbackOn element can be set to indicate exceptions that must cause
	 * the interceptor to mark the transaction for rollback. Conversely, the
	 * dontRollbackOn element can be set to indicate exceptions that must not
	 * cause the interceptor to mark the transaction for rollback. When a class
	 * is specified for either of these elements, the designated behavior
	 * applies to subclasses of that class as well. If both elements are
	 * specified, dontRollbackOn takes precedence.
	 * 
	 * @return Class[] of Exceptions
	 */
	Class<?>[]rollbackOn() default {};

	/**
	 * The dontRollbackOn element can be set to indicate exceptions that must
	 * not cause the interceptor to mark the transaction for rollback.
	 * Conversely, the rollbackOn element can be set to indicate exceptions that
	 * must cause the interceptor to mark the transaction for rollback. When a
	 * class is specified for either of these elements, the designated behavior
	 * applies to subclasses of that class as well. If both elements are
	 * specified, dontRollbackOn takes precedence.
	 * 
	 * @return Class[] of Exceptions
	 */
	Class<?>[]dontRollbackOn() default {};

}
