package com.github.kmizu.nson

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{CharSequenceReader, Position, Reader}
import com.github.kmizu.nson.NValue._
import java.lang.{Long => JLong}

/**
  * @author Kota Mizushima
  */
object NValueParser extends RegexParsers {
  override def skipWhitespace = false
  private def not[T](p: => Parser[T], msg: String): Parser[Unit] = {
    not(p) | failure(msg)
  }
  private def and[T](p: => Parser[T], msg: String): Parser[Unit] = {
    not(not(p)) | failure(msg)
  }
  lazy val EOF: Parser[String] = not(elem(".", (ch: Char) => ch != CharSequenceReader.EofCh), "EOF Expected") ^^ {_.toString}
  lazy val LINEFEED : Parser[String] = ("\r\n" | "\r" | "\n")
  lazy val SEMICOLON: Parser[String] = ";"
  lazy val ANY: Parser[String] = elem(".", (ch: Char) => ch != CharSequenceReader.EofCh) ^^ {_.toString}

  lazy val SPACING: Parser[String] = (COMMENT | "\r\n" | "\r" | "\n" | " " | "\t" | "\b" | "\f").* ^^ {_.mkString}
  lazy val SPACING_WITHOUT_LF: Parser[String] = (COMMENT | "\t" | " " | "\b" | "\f").* ^^ {_.mkString}
  lazy val TERMINATOR: Parser[String] = (LINEFEED | SEMICOLON | EOF) <~ SPACING
  lazy val SEPARATOR: Parser[String] = (LINEFEED | COMMA | EOF | SPACING_WITHOUT_LF) <~ SPACING

  lazy val BLOCK_COMMENT: Parser[Any] = (
    "/*" ~ (not("*/") ~ (BLOCK_COMMENT | ANY)).* ~ "*/"
    )
  lazy val LINE_COMMENT: Parser[Any] = (
    "//" ~ (not(LINEFEED) ~ ANY).* ~ LINEFEED
    )
  lazy val COMMENT: Parser[Any] = BLOCK_COMMENT | LINE_COMMENT

  def CL[T](parser: Parser[T]): Parser[T] = parser <~ SPACING
  def token(parser: Parser[String]): Parser[String] = parser <~ SPACING_WITHOUT_LF
  def unescape(input: String): String = {
    val builder = new java.lang.StringBuilder
    val length = input.length
    var i = 0
    while(i < length - 1) {
      (input.charAt(i), input.charAt(i + 1)) match {
        case ('\\', 'r') => builder.append('\r'); i += 2
        case ('\\', 'n') => builder.append('\n'); i += 2
        case ('\\', 'b') => builder.append('\b'); i += 2
        case ('\\', 'f') => builder.append('\f'); i += 2
        case ('\\', 't') => builder.append('\t'); i += 2
        case ('\\', '\\') => builder.append('\\'); i += 2
        case (ch, _) => builder.append(ch); i += 1
      }
    }
    if(i == length - 1) {
      builder.append(input.charAt(i))
    }
    new String(builder)
  }

  lazy val LT      : Parser[String] = token("<")
  lazy val GT      : Parser[String] = token(">")
  lazy val LTE     : Parser[String] = token("<=")
  lazy val GTE     : Parser[String] = token(">=")
  lazy val PLUS    : Parser[String] = token("+")
  lazy val MINUS   : Parser[String] = token("-")
  lazy val ASTER   : Parser[String] = token("*")
  lazy val SLASH   : Parser[String] = token("/")
  lazy val LPAREN  : Parser[String] = token("(")
  lazy val RPAREN  : Parser[String] = token(")")
  lazy val LBRACE  : Parser[String] = token("{")
  lazy val RBRACE  : Parser[String] = token("}")
  lazy val LBRACKET: Parser[String] = token("[")
  lazy val RBRACKET: Parser[String] = token("]")
  lazy val IF      : Parser[String] = token("if")
  lazy val ELSE    : Parser[String] = token("else")
  lazy val WHILE   : Parser[String] = token("while")
  lazy val FOREACH : Parser[String] = token("foreach")
  lazy val IMPORT  : Parser[String] = token("import")
  lazy val TRUE    : Parser[String] = token("true")
  lazy val FALSE   : Parser[String] = token("false")
  lazy val IN      : Parser[String] = token("in")
  lazy val COMMA   : Parser[String] = token(",")
  lazy val DOT     : Parser[String] = token(".")
  lazy val CLASS   : Parser[String] = token("class")
  lazy val DEF     : Parser[String] = token("def")
  lazy val MUTABLE : Parser[String] = token("mutable")
  lazy val CLEANUP : Parser[String] = token("cleanup")
  lazy val VAL     : Parser[String] = token("val")
  lazy val EQ      : Parser[String] = token("=")
  lazy val EQEQ    : Parser[String] = token("==")
  lazy val ARROW   : Parser[String] = token("=>")
  lazy val COLON   : Parser[String] = token(":")
  lazy val NEW     : Parser[String] = token("new")
  lazy val QUES    : Parser[String] = token("?")
  lazy val AMP2    : Parser[String] = token("&&")
  lazy val BAR2    : Parser[String] = token("||")

  def expression: Parser[NValue] = SPACING ~> nvalue <~ SPACING

  //nvalue::= doubleLiteral | integerLiteral  | stringLiteral | objectLiteral | arrayLiteral | booleanLiteral | ident | "(" expression ")" | "{" lines "}"
  private lazy val nvalue: Parser[NValue] = doubleLiteral | integerLiteral | stringLiteral | objectLiteral | arrayLiteral | booleanLiteral | ident | CL(LPAREN) ~>expression<~ RPAREN

  //intLiteral ::= ["1"-"9"] {"0"-"9"}
  private lazy val integerLiteral : Parser[NLong] = (opt("+" | "-") ~
    ( ("0x" ~> """[0-9a-fA-F][0-9a-fA-F]*|0""".r  ^^ { case value =>
        JLong.parseLong(value, 16)
      })
    | ("""[1-9][0-9]*|0""".r ^^ { case value =>
        JLong.parseLong(value)
      })
    ) ^^ { case sign ~ value =>
      val result = sign match {
        case Some("+") => value
        case Some("-") => -value
        case _ => value
      }
      NLong(result)
    }) <~ SPACING_WITHOUT_LF

  private lazy val doubleLiteral: Parser[NDouble]= (opt("+" | "-") ~ "([1-9][0-9]*|0)\\.[0-9]*".r ^^ {
    case sign ~ value =>
      val parsed = value.toDouble
      val result = sign match {
        case Some("+") => parsed
        case Some("-") => -parsed
        case _ => parsed
      }
      NDouble(result)
  }) <~ SPACING_WITHOUT_LF

  private lazy val booleanLiteral: Parser[NBoolean] = (TRUE ^^ {_ => true }| FALSE ^^ {_ => false}) ^^ {
    case true => NBoolean(true)
    case false => NBoolean(false)
  }

  //stringLiteral ::= "\"" ((?!")(\[rntfb"'\\]|[^\\]))* "\""
  private lazy val stringLiteral : Parser[NString] =
  (("\"" ~> ( """((?!("|#\{))(\\[rntfb"'\\]|[^\\]))+""".r ).* <~ "\"") ^^ { values =>
    NString(values.mkString)
  }) <~ SPACING_WITHOUT_LF

  private lazy val arrayLiteral: Parser[NArray] = ((CL(LBRACKET) ~> (repsep(CL(expression), SEPARATOR) <~ opt(SEPARATOR)) <~ RBRACKET) ^^ {
    case contents => NArray(contents)
  }) <~ SPACING_WITHOUT_LF

  private lazy val objectLiteral: Parser[NObject] =  ((CL(LBRACE) ~> (repsep(CL((stringLiteral | ident) ~ COLON ~ expression), SEPARATOR) <~ opt(SEPARATOR)) <~ RBRACE) ^^ {
    case contents => NObject(contents.map { case k ~ _ ~ v  => k.value -> v}.toMap)
  }) <~ SPACING_WITHOUT_LF

  private lazy val ident :Parser[NString] = ( """[A-Za-z_][a-zA-Z0-9]*""".r ^^ {
    case value  => NString(value)
  }) <~ SPACING_WITHOUT_LF

  def parse(str:String): NValue = parseAll(expression, str) match {
    case Success(value, _) => value
    case Failure(msg, next) => throw ParseException(next.pos + msg)
    case Error(msg, next) => throw ParseException(next.pos + msg)
  }
}
