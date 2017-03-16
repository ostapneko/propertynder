package propertynder.ml

import breeze.linalg._
import breeze.numerics._

object LogisticRegression {
  case class CostAndGrad(cost: Double, grad: DenseVector[Double])
  case class GradientDescentResult(theta: DenseVector[Double], cost: Double)

  def costAndGrad(theta: DenseVector[Double], examples: DenseMatrix[Double], labels: DenseVector[Double]): CostAndGrad = {
    val m = examples.size.toDouble
    val hs = sigmoid(examples * theta)
    val cost = - (labels.t * log(hs) + (1.0 -:- labels).t * log(1.0 -:- hs)) / m
    val grad = examples.t * (hs - labels) /:/ m
    CostAndGrad(cost, grad)
  }

  def runGradientDescent(
    initTheta: DenseVector[Double],
    examples: DenseMatrix[Double],
    labels: DenseVector[Double],
    maxIter: Int = 1000,
    deltaCostThreshold: Double = 1.0E-4,
    gradientStep: Double = 1.0,
    alpha: Double = 1
  ): GradientDescentResult = {
    var step = 1
    var deltaCost: Option[Double] = None
    var cost: Option[Double] = None
    val theta = initTheta

    while (step < maxIter && deltaCost.forall(_.abs > deltaCostThreshold)) {
      val CostAndGrad(newCost, grad) = LogisticRegression.costAndGrad(theta, examples, labels)
      deltaCost = cost.map(c => (c - newCost) / c)
      cost = Some(newCost)
      theta :-= (alpha *:* grad)
      step += 1
    }

    GradientDescentResult(theta, cost.get)
  }
}