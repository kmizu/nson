package com.github.kmizu.nson

import scala.language.dynamics

object NValue {
  final case class NLong(override val value: Long) extends NValue
  final case class NDouble(override val value: Double) extends NValue
  final case class NBoolean(override val value: Boolean) extends NValue
  final case class NString(override val value: String) extends NValue
  case object NNull extends NValue {
    override val value: Null = null
  }
  final case class NArray(val content: Seq[NValue]) extends NValue {
    override val value: Seq[Any] = content.map{_.value}

    override def apply(index: Int): NValue = content(index)
  }
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
  def value: Any
  def apply(index: Int): NValue = throw new UnsupportedOperationException("NValue#apply(Int)")
  def apply(name: String): NValue = throw new UnsupportedOperationException("NValue#apply(String)")
  def selectDynamic(name: String): NValue = throw new UnsupportedOperationException("NValue#selectDynamic(String)")
}
