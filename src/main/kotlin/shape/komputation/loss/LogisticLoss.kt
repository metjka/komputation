package shape.komputation.loss

import shape.komputation.matrix.DoubleMatrix

class LogisticLoss : LossFunction {

    // -log(probability of the correct target)
    override fun forward(predictions: DoubleMatrix, targets : DoubleMatrix): Double {

        val predictionEntries = predictions.entries
        val targetEntries = targets.entries

        var loss = 0.0

        for (index in 0..predictionEntries.size-1) {

            val target = targetEntries[index]

            if (target == 1.0) {

                loss -= Math.log(predictionEntries[index])

            }

        }

        return loss

    }

    // -1/probability of the correct target summed over each column
    override fun backward(predictions: DoubleMatrix, targets : DoubleMatrix) : DoubleMatrix {

        val predictionEntries = predictions.entries
        val targetEntries = targets.entries

        val derivatives = DoubleArray(predictionEntries.size) { index ->

            val target = targetEntries[index]

            if (target == 1.0) {

                -1.0.div(predictionEntries[index])

            }
            else {

                0.0

            }

        }

        return DoubleMatrix(predictions.numberRows, predictions.numberColumns, derivatives)

    }

}