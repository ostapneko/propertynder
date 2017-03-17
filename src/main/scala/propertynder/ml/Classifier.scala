package propertynder.ml

import java.io.File

import breeze.linalg.DenseVector
import propertynder.ml.LogisticRegression.Options

object Classifier {
  /**
    *
    * @param f a CSV file containing the training set
    *          Each row is an example, the last column being the result (1.0 or 0.0)
    *          It shouldn't contain the bias column
    */
  def linear(f: File, options: Options = Options.default): LinearClassifier = {
    val trainingSet = CSVLoader.loadLinear(f)
    val Θ = LogisticRegression.trainClassifier(trainingSet, options)

    LinearClassifier(Θ)
  }

  /**
    *
    * @param f a CSV file containing the training set
    *          Each row is an example, the last column being the result (1.0 or 0.0)
    *          It shouldn't contain the bias column
    */
  def quadratic(f: File, options: Options = Options.default): QuadraticClassifier = {
    val trainingSet = CSVLoader.loadQuadratic(f)
    val Θ = LogisticRegression.trainClassifier(trainingSet, options)

    QuadraticClassifier(Θ)
  }

}

trait Classifier {
  def predict(example: Seq[Double]): Boolean
}

/**
  * A logistic regression classifier that uses a linear model, that is,
  * will try to find the best Θ to predict the result of a given example [x1, ..., xn] via
  * y = Θ0 + Θ1 * x1 + ... + Θn * xn
  */
case class LinearClassifier(Θ: DenseVector[Double]) extends Classifier {
  override def predict(example: Seq[Double]): Boolean =
    LogisticRegression.predictLinear(DenseVector(example.toArray), Θ)
}

/**
  * A logistic regression classifier that uses a quadratic model, that is,
  * will try to find the best Θ to predict the result of a given example [x1, ..., xn] via
  * y = Θ0 + Θ11 * x1 ** 2 + Θ12 * x1 * x2 + ... + Θ1n * x1 * xn + ... + Θnn * xn ** 2 +
  * Θ1 * x1 + ... + Θn * xn
  */
case class QuadraticClassifier(Θ: DenseVector[Double]) extends Classifier {
  override def predict(example: Seq[Double]): Boolean =
    LogisticRegression.predictQuadratic(DenseVector(example.toArray), Θ)
}
