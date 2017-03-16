package propertynder

import breeze.linalg._
import breeze.plot._
import propertynder.ml.LogisticRegression
import propertynder.ml.LogisticRegression.GradientDescentResult

object Example extends App {
  val X = DenseMatrix.zeros[Double](10, 3)
  val y = DenseVector.zeros[Double](10)

  val pos = DenseMatrix.zeros[Double](5, 2)
  val neg = DenseMatrix.zeros[Double](5, 2)

  (0 to 9).zip(Seq(
    // -- positive examples
    (1.0, 1.0, 1.0),
    (2.9, 0.0, 1.0),
    (0.0, 2.9, 1.0),
    (0.5, 2.3, 1.0),
    (2.3, 0.5, 1.0),
    // -- negative examples
    (3.1, 0.1, 0.0),
    (2.0, 2.0, 0.0),
    (0.1, 3.1, 0.0),
    (1.0, 2.1, 0.0),
    (2.1, 1.0, 0.0)
  )).foreach { case (i, (x1, x2, label)) =>
    X(i, ::) := DenseVector(1, x1, x2).t
    y.update(i, label)
    if (label > 0.5)
      pos(i, ::) := DenseVector(x1, x2).t
    else
      neg(i - 5, ::) := DenseVector(x1, x2).t
  }

  val GradientDescentResult(theta, cost) =
    LogisticRegression.runGradientDescent(DenseVector.zeros[Double](3), X, y, maxIter = 10000, regularization = 0.1)

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
