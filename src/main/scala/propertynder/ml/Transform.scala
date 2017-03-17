package propertynder.ml

import breeze.linalg._

import scala.collection.mutable.ArrayBuffer

object Transform {
  def addBias(m: DenseMatrix[Double]): DenseMatrix[Double] = {
    val bias = DenseMatrix.ones[Double](m.rows, 1)
    DenseMatrix.horzcat(bias, m)
  }

  def toQuadraticParameters(v: DenseVector[Double]): DenseVector[Double] = {
    val n = v.length
    val data = v.t

    // bias
    var newResultVec = ArrayBuffer[Double](1)

    // quadratic terms
    for {
      i <- 0 until n
      j <- i until n
    } yield {
      newResultVec += data(i) * data(j)
    }

    // linear terms
    (0 until n).foreach(i => newResultVec += data(i))

    DenseVector(newResultVec.toArray)
  }
}
