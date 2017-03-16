package propertynder

import java.io.File

import breeze.linalg._
import breeze.plot._
import propertynder.ml.LogisticRegression
import propertynder.ml.LogisticRegression.GradientDescentResult

object Example {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      sys.error("expected the training set file as argument")
      sys.exit(1)
    }

    val file = new File(args.head)
    val trainingSet = Loader.loadLinear(file)
    val m = trainingSet.labels.length
    val n = trainingSet.examples.cols

    val GradientDescentResult(theta, cost) =
      LogisticRegression.runGradientDescent(
        DenseVector.zeros[Double](n),
        trainingSet.examples,
        trainingSet.labels,
        maxIter = 10000,
        regularization = 0.1
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

    val f = Figure()
    val p = f.subplot(0)
    val x = linspace(0.0, 5.0)

    p += plot(x, 0.0 -:- (theta(0) +:+ theta(1) *:* x) /:/ theta(2))
    p += plot(pos(::, 0), pos(::, 1), '+', colorcode = "green")
    p += plot(neg(::, 0), neg(::, 1), '+', colorcode = "red")
    f.saveas("test.png")


  }
}
