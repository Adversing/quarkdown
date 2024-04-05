package eu.iamgio.quarkdown

import eu.iamgio.quarkdown.function.Function
import eu.iamgio.quarkdown.function.FunctionCall
import eu.iamgio.quarkdown.function.FunctionCallArgument
import eu.iamgio.quarkdown.function.FunctionParameter
import eu.iamgio.quarkdown.function.value.StringType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Function call tests.
 */
class FunctionTest {
    @Test
    fun `no arguments`() {
        val function =
            object : Function<String, StringType> {
                override val name: String = "greet"
                override val parameters: List<FunctionParameter<*>> = emptyList()
                override val invoke: (List<FunctionCallArgument<*>>) -> String = {
                    "Hello"
                }
            }

        val call = FunctionCall(function, arguments = emptyList())

        assertEquals("Hello", call.execute())
    }
}
