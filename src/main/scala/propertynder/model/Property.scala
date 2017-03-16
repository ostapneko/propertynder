package propertynder.model

import propertynder.model.InferredType.InferredType

object InferredType extends Enumeration {
  type InferredType = Value
  val Haussmannien, Loft, Neuf, Autre = Value
}

case class Property(
  title: String,
  mainUrl: String,
  description: String,
  rooms: Option[Int],
  bedrooms: Option[Int],
  price: Option[Int],
  size: Option[Double],
  postcode: Option[Int],
  constructionDate: Option[Int] = None,
  inferredType: Option[InferredType] = None
)
