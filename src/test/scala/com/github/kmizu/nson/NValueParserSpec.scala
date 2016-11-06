package com.github.kmizu.nson

import org.scalatest.{DiagrammedAssertions, FunSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import com.github.kmizu.nson.NValue._

class NValueParserSpec extends FunSpec with DiagrammedAssertions with GeneratorDrivenPropertyChecks {
  import NValueParser.parse
  describe("Primitive") {
    it("NLong(decimal)") {
      assert(NLong(0) == parse("0"))
      assert(NLong(-1) == parse("-1"))
      assert(NLong(+1) == parse("+1"))
      assert(NLong(1) == parse("1"))
    }
    it("NLong(hex)") {
      assert(NLong(0x10) == parse("0x10"))
      assert(NLong(-0x10) == parse("-0x10"))
      assert(NLong(0xFF) == parse("0xFF"))
      assert(NLong(-0xFF) == parse("-0xFF"))
    }
    it("NDouble") {
      assert(NDouble(1.0) == parse("1.0"))
      assert(NDouble(-1.0) == parse("-1.0"))
      assert(NDouble(1.5) == parse("1.5"))
      assert(NDouble(-1.5) == parse("-1.5"))
      assert(NDouble(+1.5) == parse("+1.5"))
    }
    it("NString") {
      assert(NString("") == parse("\"\""))
      assert(NString("abc") == parse("\"abc\""))
    }
    it("NBoolean") {
      assert(NBoolean(true) == parse("true"))
      assert(NBoolean(false) == parse("false"))
    }
    it("identifier") {
      assert(NString("hello") == parse("hello"))
      assert(NString("world") == parse("world"))
    }
  }
  describe("NArray") {
    it("empty") {
      assert(NArray(Seq()) == parse("[]"))
    }
    it("one element") {
      assert(NArray(Seq(NLong(1))) == parse("[1]"))
      assert(NArray(Seq(NLong(0x10))) == parse("[0x10]"))
      assert(NArray(Seq(NDouble(1.5))) == parse("[1.5]"))
      assert(NArray(Seq(NString("hello"))) == parse("[\"hello\"]"))
      assert(NArray(Seq(NString("hello"))) == parse("[hello]"))
    }
    it("two element") {
      assert(NArray(Seq(NLong(1), NLong(2))) == parse("[1 2]"))
      assert(NArray(Seq(NLong(1), NDouble(1.5))) == parse("[1 1.5]"))
      assert(NArray(Seq(NBoolean(true), NBoolean(false))) == parse("[true false]"))
    }
    it("more than two element") {
      assert(NArray(Seq(NLong(1), NLong(2), NLong(3), NLong(4))) == parse("[1 2 3 4]"))
      assert(NArray(Seq(NBoolean(true), NBoolean(false), NBoolean(true), NBoolean(false))) == parse("[true false true false]"))
    }
  }
  describe("NObject") {
    it("empty") {
      assert(NObject() == parse("{}"))
      assert(NObject("name" -> NString("Kota Mizushima"), "age" -> NLong(33)) == parse("{name: \"Kota Mizushima\" age: 33}"))
    }
    it("one property") {
      assert(NObject("name" -> NString("Kota Mizushima")) == parse("{name: \"Kota Mizushima\"}"))
    }
    it("two properties") {
      assert(NObject("name" -> NString("Kota Mizushima"), "age" -> NLong(33)) == parse("{name: \"Kota Mizushima\" age: 33}"))
    }
    it("complex") {
      assert(
        NObject("name" -> NString("Kota Mizushima"), "age" -> NLong(33), "like" -> NArray(Seq(NString("Scala")))) ==
          parse("{name: \"Kota Mizushima\" age: 33 like: [\"Scala\"]}")
      )
      assert(
        NObject("name" -> NString("Kota Mizushima"), "age" -> NLong(33), "like" -> NArray(Seq(NString("Scala"), NString("Nemerle")))) ==
          parse(
            """
          | { name: "Kota Mizushima"
          |   age: 33
          |   like: ["Scala", "Nemerle"] }
          """.stripMargin
          )
      )
      val result = parse(
        """
          | { name: "Kota Mizushima"
          |   age: 33
          |   like: ["Scala", "Nemerle"] }
       """.stripMargin
      )
      assert("Kota Mizushima" == result.name.value)
      assert(33 == result.age.value)
      assert(Seq("Scala", "Nemerle") == result.like.value)
    }
  }
}