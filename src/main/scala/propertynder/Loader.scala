package propertynder

import java.io.File

import breeze.linalg._

object Loader {
  case class TrainingSet(examples: DenseMatrix[Double], labels: DenseVector[Double])

  /**
    * Load a CSV file representing the training set,
    * with the last column denoting labels.
    * It also adds the bias column at the beginning
    */
  def loadLinear(f: File): TrainingSet = {
    val mat = csvread(f, ',')
    val labels = mat(::, -1)
    val noLabels = mat(::, 0 to -2)
    val bias = DenseMatrix.ones[Double](labels.length, 1)
    val examples: DenseMatrix[Double] = DenseMatrix.horzcat(bias, noLabels)
    TrainingSet(examples, labels)
  }
}
