package propertynder

import java.io.File

import breeze.linalg._
import propertynder.ml.Transform._

import scala.collection.mutable.ArrayBuffer

object Loader {
  case class TrainingSet(examples: DenseMatrix[Double], labels: DenseVector[Double])

  /**
    * Load a CSV file representing the training set,
    * with the last column denoting labels.
    * It also adds the bias column at the beginning
    */
  def loadLinear(f: File, skip: Option[Int] = None): TrainingSet = {
    val mat = skip.fold(csvread(f, ','))(l => csvread(f, ',', skipLines = l))
    val labels = mat(::, -1)
    val noLabels = mat(::, 0 to -2)
    val examples: DenseMatrix[Double] = addBias(noLabels)
    TrainingSet(examples, labels)
  }

  /**
    * Load a CSV file representing the training set,
    * with the last column denoting labels.
    * If the training set has the parameter x1, x2, ..., xn, this will create
    * a training set with the shape:
    * 1, x1 ** 2, x1 * x2, ..., x1 * xn, x2 ** 2, ..., x2 * xn, ..., xn ** 2, x1, x2, ..., xn
    * (note the bias at the first column)
    * So a a training set with n parameters will generate another training set with 1 + n * (n + 1) / 2 + n parameters
    */
  def loadQuadratic(f: File, skip: Option[Int] = None): TrainingSet = {
    val mat = skip.fold(csvread(f, ','))(l => csvread(f, ',', skipLines = l))
    val labels = mat(::, -1)
    val m = labels.length

    val noLabels = mat(::, 0 to -2)
    val n = noLabels.cols

    // 1 bias + n * (n + 1) / 2 quadratic terms + n linear terms
    val empty = DenseMatrix.zeros[Double](0, 1 + n * (n + 1) / 2 + n)

    val examples = (0 until m).foldLeft(empty) { case (accRes, i) =>
      val row = noLabels(i, ::)

      val newResultRow = toQuadraticParameters(row.t).toDenseMatrix
      DenseMatrix.vertcat(accRes, newResultRow)
    }

    TrainingSet(examples, labels)
  }
}
