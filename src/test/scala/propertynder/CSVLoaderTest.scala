package propertynder

import java.io.File

import breeze.linalg.{DenseMatrix, DenseVector}
import org.scalatest.{FreeSpec, Matchers}
import propertynder.ml.CSVLoader

class CSVLoaderTest extends FreeSpec with Matchers {
  "Loader" - {
    "loads CSV files into a quadratic training set" in {
      val file = new File(getClass.getResource("/training-set.csv").getPath)

      val trainingSet = CSVLoader.loadQuadratic(file)

      trainingSet.labels should ===(DenseVector[Double](1, 0))

      trainingSet.examples should ===(
        DenseMatrix(
          (1.0, 4.0, 6.0, 9.0, 2.0, 3.0),
          (1.0, 16.0, 20.0, 25.0, 4.0, 5.0)
        )
      )
    }
  }
}
