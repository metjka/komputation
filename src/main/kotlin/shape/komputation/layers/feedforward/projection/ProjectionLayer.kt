package shape.komputation.layers.feedforward.projection

import shape.komputation.functions.backwardProjectionWrtBias
import shape.komputation.functions.backwardProjectionWrtInput
import shape.komputation.functions.backwardProjectionWrtWeights
import shape.komputation.functions.project
import shape.komputation.initialization.InitializationStrategy
import shape.komputation.initialization.initializeMatrix
import shape.komputation.initialization.initializeRowVector
import shape.komputation.layers.FeedForwardLayer
import shape.komputation.layers.OptimizableLayer
import shape.komputation.matrix.DoubleMatrix
import shape.komputation.optimization.DenseAccumulator
import shape.komputation.optimization.OptimizationStrategy
import shape.komputation.optimization.UpdateRule
import shape.komputation.optimization.updateDensely

class ProjectionLayer(
    name : String? = null,
    private val weights : DoubleArray,
    private val numberWeightRows: Int,
    private val numberWeightColumns: Int,
    private val bias : DoubleArray? = null,
    private val weightUpdateRule: UpdateRule? = null,
    private val biasUpdateRule: UpdateRule? = null) : FeedForwardLayer(name), OptimizableLayer {

    private var forwardResult : DoubleMatrix? = null
    private var input : DoubleMatrix? = null

    private val numberWeightEntries = numberWeightRows * numberWeightColumns

    private var weightAccumulator = if(weightUpdateRule != null) DenseAccumulator(numberWeightRows * numberWeightColumns) else null
    private var biasAccumulator = if(bias != null && biasUpdateRule != null) DenseAccumulator(bias.size) else null

    override fun forward(input: DoubleMatrix) : DoubleMatrix {

        this.input = input

        val projection = project(input.entries, input.numberRows, input.numberColumns, weights, numberWeightRows, numberWeightColumns, bias)

        this.forwardResult = DoubleMatrix(numberWeightRows, input.numberColumns, projection)

        return this.forwardResult!!

    }

    override fun backward(chain : DoubleMatrix) : DoubleMatrix {

        val chainEntries = chain.entries
        val numberChainRows = chain.numberRows
        val numberChainColumns = chain.numberColumns

        val input = this.input!!
        val numberInputRows = input.numberRows
        val numberInputColumns = input.numberColumns
        val numberInputEntries = numberInputRows * numberInputColumns

        val gradient = backwardProjectionWrtInput(
            numberInputRows,
            numberInputColumns,
            numberInputEntries,
            weights,
            numberWeightRows,
            chainEntries,
            numberChainRows)

        if (weightUpdateRule != null) {

            val backwardWrtWeights = backwardProjectionWrtWeights(
                numberWeightEntries,
                numberWeightRows,
                numberWeightColumns,
                input.entries,
                numberInputRows,
                chainEntries,
                numberChainRows,
                numberChainColumns)

            this.weightAccumulator!!.accumulate(backwardWrtWeights)

        }

        if (bias != null && biasUpdateRule != null) {

            val backwardWrtBias = backwardProjectionWrtBias(bias.size, chainEntries, numberChainRows, numberChainColumns)

            this.biasAccumulator!!.accumulate(backwardWrtBias)


        }

        return DoubleMatrix(numberInputRows, numberInputColumns, gradient)

    }

    override fun optimize() {

        if (this.weightUpdateRule != null) {

            val weightAccumulator = this.weightAccumulator!!

            updateDensely(this.weights, weightAccumulator.getAccumulation(), weightAccumulator.getCount(), weightUpdateRule)

            weightAccumulator.reset()

        }

        if (this.bias != null && this.biasUpdateRule != null) {

            val biasAccumulator = this.biasAccumulator!!

            updateDensely(this.bias, biasAccumulator.getAccumulation(), biasAccumulator.getCount(), biasUpdateRule)

            biasAccumulator.reset()

        }

    }

}

fun createProjectionLayer(
    numberInputRows: Int,
    numberResultRows: Int,
    withBias : Boolean,
    initializationStrategy : InitializationStrategy,
    optimizationStrategy : OptimizationStrategy? = null) =

    createProjectionLayer(null, numberInputRows, numberResultRows, withBias, initializationStrategy, optimizationStrategy)

fun createProjectionLayer(
    name : String?,
    numberInputRows: Int,
    numberResultRows: Int,
    withBias : Boolean,
    initializationStrategy : InitializationStrategy,
    optimizationStrategy : OptimizationStrategy? = null): ProjectionLayer {

    val numberWeightRows = numberResultRows
    val numberWeightColumns = numberInputRows

    val weights = initializeMatrix(initializationStrategy, numberWeightRows, numberWeightColumns)

    val weightUpdateRule : UpdateRule?

    if (optimizationStrategy != null) {

        weightUpdateRule = optimizationStrategy(numberWeightRows, numberWeightColumns)

    }
    else {

        weightUpdateRule = null

    }

    val bias : DoubleArray?
    val biasUpdateRule: UpdateRule?

    if (withBias) {

        bias = initializeRowVector(initializationStrategy, numberWeightRows)

        if (optimizationStrategy != null) {

            biasUpdateRule = optimizationStrategy(bias.size, 1)

        }
        else {

            biasUpdateRule = null

        }

    }
    else {

        bias = null
        biasUpdateRule = null

    }

    return ProjectionLayer(name, weights, numberWeightRows, numberWeightColumns, bias, weightUpdateRule, biasUpdateRule)

}