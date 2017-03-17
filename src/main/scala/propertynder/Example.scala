package propertynder

import java.io.File

import propertynder.ml.{Classifier, LogisticRegression}

object Example {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      sys.error("expected the training set file as argument")
      sys.exit(1)
    }

    val file = new File(args.head)

    val classifier = Classifier.quadratic(file, LogisticRegression.Options.default)

    // example for a 2-dimensions training set
    val prediction = classifier.predict(Seq(50.5, 3.1))

    println(prediction)
  }
}
