package com.github.kmizu.nson

import scala.language.dynamics

object NValue {

  /**
    * Represent an integer value in NSON
    * @param value a Long value
    */
  final case class NLong(override val value: Long) extends NValue

  /**
    * Represent an floating point value in NSON
    * @param value a Double value
    */
  final case class NDouble(override val value: Double) extends NValue

  /**
    * Represent an floating point value in NSON
    * @param value a Boolean value
    */
  final case class NBoolean(override val value: Boolean) extends NValue

  /**
    * Represent an String value in NSON
    * @param value a String value
    */
  final case class NString(override val value: String) extends NValue

  /**
    * Represent null value in NSON
    */
  case object NNull extends NValue {
    override val value: Null = null
  }

  /**
    * Represent an array in NSON
    * @param content a Seq of NValue
    */
  final case class NArray(val content: Seq[NValue]) extends NValue {
    override val value: Seq[Any] = content.map{_.value}

    override def apply(index: Int): NValue = content(index)
  }

  /**
    * Represent an object in NSON
    * @param content a Map value which is key to value mappings
    */
  final case class NObject(val content: Map[String, NValue]) extends NValue {
    override val value: Map[String, Any] = content.map{ case (k, v) => (k, v.value)}

    override def apply(name: String): NValue = content(name)

    override def selectDynamic(name: String): NValue = content(name)
  }
  object NObject {
    def apply(): NObject = NObject(Map.empty[String, NValue])
    def apply(kvs: (String, NValue)*): NObject = {
      NObject(kvs.toMap)
    }
  }
}
sealed trait NValue extends Dynamic {
  /**
    * Extract a plain Scala value
    * @return a value corresponding to NValue
    */
  def value: Any


  /**
    * Fetch index-th element of NArray
    * @param index
    * @return index-th element of NArray
    * @throws UnsupportedOperationException if it's not a NArray
    */
  def apply(index: Int): NValue = throw new UnsupportedOperationException("NValue#apply(Int)")

  /**
    * Fetch a property of NObject
    * @param name
    * @return property name
    * @throws UnsupportedOperationException if it's not a NObject
    */
  def apply(name: String): NValue = throw new UnsupportedOperationException("NValue#apply(String)")

  /**
    * Fetch a property of NObject.
    * ```
    * obj.x // obj.apply("x")
    * ```
    * @param name
    * @return property name
    * @throws UnsupportedOperationException if it's not a NObject
    */
  def selectDynamic(name: String): NValue = throw new UnsupportedOperationException("NValue#selectDynamic(String)")
}
