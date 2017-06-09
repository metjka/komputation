package shape.konvolution.layers

import org.junit.jupiter.api.Test
import shape.konvolution.assertMatrixEquality
import shape.konvolution.createDenseMatrix

class ReluLayerTest {

    @Test
    fun testForward() {

        val reluLayer = ReluLayer()

        val input = createDenseMatrix(doubleArrayOf(1.0, -2.0), doubleArrayOf(-3.0, 4.0))

        val actual = reluLayer.forward(input)
        val expected = createDenseMatrix(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(0.0, 4.0)
        )

        assertMatrixEquality(expected, actual, 0.0001)

    }

    @Test
    fun testBackward() {

        val reluLayer = ReluLayer()

        val input = createDenseMatrix(doubleArrayOf(-1.0), doubleArrayOf(2.0))

        val actual = reluLayer.forward(input)
        val expected = createDenseMatrix(
            doubleArrayOf(0.0),
            doubleArrayOf(2.0)
        )

        assertMatrixEquality(expected, actual, 0.0001)

    }


}