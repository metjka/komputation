package shape.komputation.demos.lines

import shape.komputation.initialization.createUniformInitializer
import shape.komputation.layers.feedforward.activation.ReluLayer
import shape.komputation.layers.feedforward.convolution.MaxPoolingLayer
import shape.komputation.layers.feedforward.convolution.createConvolutionalLayer
import shape.komputation.layers.entry.InputLayer
import shape.komputation.layers.feedforward.activation.SoftmaxLayer
import shape.komputation.layers.feedforward.projection.createProjectionLayer
import shape.komputation.loss.LogisticLoss
import shape.komputation.matrix.Matrix
import shape.komputation.matrix.doubleRowVector
import shape.komputation.matrix.doubleMatrixFromRows
import shape.komputation.matrix.doubleColumnVector
import shape.komputation.networks.Network
import shape.komputation.networks.printLoss
import shape.komputation.optimization.stochasticGradientDescent
import java.util.*

fun main(args: Array<String>) {

    val input = arrayOf<Matrix>(

        doubleMatrixFromRows(
            doubleRowVector(1.0, 1.0, 1.0),
            doubleRowVector(0.0, 0.0, 0.0),
            doubleRowVector(0.0, 0.0, 0.0)
        ),
        doubleMatrixFromRows(
            doubleRowVector(0.0, 0.0, 0.0),
            doubleRowVector(1.0, 1.0, 1.0),
            doubleRowVector(0.0, 0.0, 0.0)
        ),
        doubleMatrixFromRows(
            doubleRowVector(0.0, 0.0, 0.0),
            doubleRowVector(0.0, 0.0, 0.0),
            doubleRowVector(1.0, 1.0, 1.0)
        ),
        doubleMatrixFromRows(
            doubleRowVector(1.0, 0.0, 0.0),
            doubleRowVector(1.0, 0.0, 0.0),
            doubleRowVector(1.0, 0.0, 0.0)
        ),
        doubleMatrixFromRows(
            doubleRowVector(0.0, 1.0, 0.0),
            doubleRowVector(0.0, 1.0, 0.0),
            doubleRowVector(0.0, 1.0, 0.0)
        ),
        doubleMatrixFromRows(
            doubleRowVector(0.0, 0.0, 1.0),
            doubleRowVector(0.0, 0.0, 1.0),
            doubleRowVector(0.0, 0.0, 1.0)
        )
    )

    val targets = arrayOf(
        doubleColumnVector(0.0, 1.0),
        doubleColumnVector(0.0, 1.0),
        doubleColumnVector(0.0, 1.0),
        doubleColumnVector(1.0, 0.0),
        doubleColumnVector(1.0, 0.0),
        doubleColumnVector(1.0, 0.0)
    )

    val random = Random(1)
    val initialize = createUniformInitializer(random, -0.05, 0.05)

    val optimization = stochasticGradientDescent(0.01)

    val filterWidth = 3
    val filterHeight = 1
    val numberFilters = 6

    val network = Network(
        InputLayer(),
        createConvolutionalLayer(numberFilters, filterWidth, filterHeight, initialize, optimization),
        MaxPoolingLayer(),
        ReluLayer(),
        createProjectionLayer(numberFilters, 2, true, initialize, optimization),
        SoftmaxLayer()
    )

    network.train(input, targets, LogisticLoss(), 30_000, 1, printLoss)
}