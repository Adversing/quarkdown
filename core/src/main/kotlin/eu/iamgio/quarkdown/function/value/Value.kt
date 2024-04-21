package eu.iamgio.quarkdown.function.value

import eu.iamgio.quarkdown.function.expression.Expression
import eu.iamgio.quarkdown.function.value.output.OutputValueVisitor

/**
 * An immutable value wrapper.
 */
sealed interface Value<T> {
    /**
     * The wrapped value.
     */
    val unwrappedValue: T
}

/**
 * An immutable value wrapper that is used in function parameters and function call arguments.
 * When used as an [Expression], its evaluated value is the same as its static wrapped value
 */
sealed interface InputValue<T> : Value<T>, Expression

/**
 * An immutable value wrapper that is used in function outputs.
 */
sealed interface OutputValue<T> : Value<T> {
    fun <O> accept(visitor: OutputValueVisitor<O>): O
}
