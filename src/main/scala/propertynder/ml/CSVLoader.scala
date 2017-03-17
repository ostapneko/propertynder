package propertynder.ml

import java.io.File

import breeze.linalg._
import propertynder.ml.Transform._

object CSVLoader {
  case class TrainingSet(examples: DenseMatrix[Double], labels: DenseVector[Double])

  private def mkTrainingSet(f: File, skip: Option[Int])(exampleTransformation: DenseMatrix[Double] => DenseMatrix[Double]) = {
    val mat = skip.fold(csvread(f, ','))(l => csvread(f, ',', skipLines = l))
    val labels = mat(::, -1)
    val noLabels = mat(::, 0 to -2)
    val examples: DenseMatrix[Double] = exampleTransformation(noLabels)
    TrainingSet(examples, labels)
  }

  /**
    * Load a CSV file representing the training set,
    * with the last column denoting labels.
    * It also adds the bias column at the beginning
    */
  def loadLinear(f: File, skip: Option[Int] = None): TrainingSet =
    mkTrainingSet(f, skip)(addBias)

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
    mkTrainingSet(f, skip) { noLabels =>
      val n = noLabels.cols
      val m = noLabels.rows
      // 1 bias + n * (n + 1) / 2 quadratic terms + n linear terms
      val empty = DenseMatrix.zeros[Double](0, 1 + n * (n + 1) / 2 + n)

      (0 until m).foldLeft(empty) { case (accRes, i) =>
        val row = noLabels(i, ::)

        val newResultRow = toQuadraticParameters(row.t).toDenseMatrix
        DenseMatrix.vertcat(accRes, newResultRow)
      }
    }
  }
}
