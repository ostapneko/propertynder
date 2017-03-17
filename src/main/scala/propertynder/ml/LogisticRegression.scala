package propertynder.ml

import breeze.linalg._
import breeze.numerics._
import CSVLoader.TrainingSet

object LogisticRegression {
  case class CostAndGrad(cost: Double, grad: DenseVector[Double])
  case class GradientDescentResult(theta: DenseVector[Double], cost: Double)

  case class Options(
    maxIter: Int,
    α: Double, // gradient descent step
    λ: Double // weight of the regularization parameter
  )

  object Options {
    def default = Options(
      maxIter = 1000,
      α = 0.01,
      λ = 1
    )
  }

  def costAndGrad(
    Θ: DenseVector[Double],
    examples: DenseMatrix[Double],
    labels: DenseVector[Double],
    λ: Double
  ): CostAndGrad = {
    val m = examples.size.toDouble
    val hs = sigmoid(examples * Θ)
    val ΘnoBias = Θ.copy
    ΘnoBias.update(0, 0.0)
    val costReg = sum(ΘnoBias ^:^ 2.0) * λ / 2 / m
    val x1 = labels.t * log(hs +:+ 1.0E-4) // avoid NaN
    val x2 = (1.0 -:- labels).t
    val x3 = log((1.0 + 1.0E-4) -:- hs) // avoid NaN
    val cost = - (x1 + x2 * x3) / m + costReg
    val gradReg = (λ / m) *:* ΘnoBias
    val grad = examples.t * (hs - labels) /:/ m + gradReg

    CostAndGrad(cost, grad)
  }

  private def runGradientDescent(
    Θ0: DenseVector[Double],
    examples: DenseMatrix[Double],
    labels: DenseVector[Double],
    opts: Options
  ): GradientDescentResult = {
    var deltaCost: Option[Double] = None
    var cost: Option[Double] = None
    val theta = Θ0

    (1 to opts.maxIter).foreach { _ =>
      val CostAndGrad(newCost, grad) =
        LogisticRegression.costAndGrad(theta, examples, labels, opts.λ)
      deltaCost = cost.map(c => (c - newCost) / c)
      cost = Some(newCost)
      theta :-= (opts.α *:* grad)
    }

    GradientDescentResult(theta, cost.get)
  }

  /**
    *
    * @return Θ
    */
  def trainClassifier(trainingSet: TrainingSet, options: Options): DenseVector[Double] = {
    val initTheta = DenseVector.zeros[Double](trainingSet.examples.cols)

    val GradientDescentResult(theta, _) =
      LogisticRegression.runGradientDescent(
        initTheta,
        trainingSet.examples,
        trainingSet.labels,
        options
      )

    theta
  }

  def predictLinear(x: DenseVector[Double], Θ: DenseVector[Double]): Boolean = {
    val biased = DenseVector.vertcat(DenseVector.ones[Double](1), x)
    biased.t * Θ > 0.5
  }

  def predictQuadratic(v: DenseVector[Double], Θ: DenseVector[Double]): Boolean = {
    val transformed = Transform.toQuadraticParameters(v)
    transformed.t * Θ > 0.5
  }
}