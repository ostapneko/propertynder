package propertynder

import java.io.File

import breeze.linalg._
import propertynder.ml.LogisticRegression
import propertynder.ml.LogisticRegression.GradientDescentResult

object Example {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      sys.error("expected the training set file as argument")
      sys.exit(1)
    }

    val file = new File(args.head)
    val testSize = 100
    val trainingSet = Loader.loadQuadratic(file, skip = Some(testSize))
    val m = trainingSet.labels.length
    val n = trainingSet.examples.cols

    val GradientDescentResult(theta, cost) =
      LogisticRegression.runGradientDescent(
        DenseVector.zeros[Double](n),
        trainingSet.examples,
        trainingSet.labels,
        maxIter = 10000,
        regularization = 0.1,
        deltaCostThreshold = 1.0E-8
      )

    var pos = DenseMatrix.zeros[Double](0, n - 1)
    var neg = DenseMatrix.zeros[Double](0, n - 1)

    (0 until m).foreach { i =>
      if (trainingSet.labels(i) > 0.5)
        pos = DenseMatrix.vertcat(pos, trainingSet.examples(i to i, 1 until n))
      else
        neg = DenseMatrix.vertcat(neg, trainingSet.examples(i to i, 1 until n))
    }

    println(s"Theta: $theta")
    println(s"Cost: $cost")

    val tests = csvread(file, ',')(0 to testSize, ::)
    var successes = 0

    (0 until testSize).foreach { i =>
      val input = tests(i, 0 to -2).t
      val isTrue = tests(i, -1) > 0.5
      val prediction = LogisticRegression.predictQuadratic(input, theta)
      if (prediction == isTrue) {
        successes += 1
      }
    }

    println(s"predictions: $successes/$testSize")
  }
}
