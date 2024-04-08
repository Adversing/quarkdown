package eu.iamgio.quarkdown.function.expression.visitor

import eu.iamgio.quarkdown.function.FunctionCall
import eu.iamgio.quarkdown.function.expression.ComposedExpression
import eu.iamgio.quarkdown.function.expression.Expression
import eu.iamgio.quarkdown.function.expression.eval
import eu.iamgio.quarkdown.function.value.DynamicInputValue
import eu.iamgio.quarkdown.function.value.NumberValue
import eu.iamgio.quarkdown.function.value.StringValue

/**
 * An [ExpressionVisitor] that describes the way two expressions are joined together.
 *
 * For example, in the Quarkdown source:
 * `.somefunction {three plus two is .sum {3} {2}}`
 * The argument to `somefunction` is a [ComposedExpression] built by these sub-expressions:
 * - `StringValue(three plus two is )`
 * - `FunctionCall(sum, 3, 2)`
 * After the evaluation of the `sum` call (handled by [EvalExpressionVisitor]) has been executed,
 * the output values are:
 * - `StringValue(three plus two is )`
 * - `NumberValue(5)`
 * These two values are then joined together by this [AppendExpressionVisitor], producing:
 * `StringValue(three plus two is 5)`
 *
 * @param other expression to append to the visited expression
 * @see ComposedExpression
 */
class AppendExpressionVisitor(private val other: Expression) : ExpressionVisitor<Expression> {
    override fun visit(value: StringValue): Expression =
        StringValue(
            value.unwrappedValue +
                other.eval().toString(),
        )

    override fun visit(value: NumberValue): Expression =
        StringValue(
            value.unwrappedValue.toString() +
                other.eval().toString(),
        )

    /**
     * @throws UnsupportedOperationException `append` must be called after the input type has been defined.
     */
    override fun visit(value: DynamicInputValue): Expression {
        throw UnsupportedOperationException()
    }

    override fun visit(expression: FunctionCall<*>): Expression =
        StringValue(
            expression.eval().unwrappedValue.toString() +
                other.eval().unwrappedValue.toString(),
        )

    /**
     * @throws UnsupportedOperationException there is no way a composed expression
     *         could be appended to another expression
     */
    override fun visit(expression: ComposedExpression): Expression {
        throw UnsupportedOperationException()
    }
}
