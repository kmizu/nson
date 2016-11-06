# nson

[![Gitter](https://badges.gitter.im/kmizu/nson.svg)](https://gitter.im/kmizu/nson?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build Status](https://travis-ci.org/kmizu/nson.png?branch=master)](https://travis-ci.org/kmizu/nson)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kmizu/nson_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.kmizu/nson_2.11)
[![Scaladoc](http://javadoc-badge.appspot.com/com.github.kmizu/nson_2.11.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.github.kmizu/nson_2.11/index.html#com.github.kmizu.nson.package)
[![Reference Status](https://www.versioneye.com/java/com.github.kmizu:nson_2.11/reference_badge.svg?style=flat)](https://www.versioneye.com/java/com.github.kmizu:nson_2.11/references)


NSON is a new object notation like JSON and unlike JSON. NSON is strict superset of JSON and differ from JSON in that NSON doesn't 
require any comma as a separator character.

For example, the following example, including several properties of an object, doesn't have any comma:

```js
{"name": "Kota Mizushima" "age": 33
  "like" : ["Scala" "Haskell" "Nemerle" "Rust"]}
```

## Usage

Add the following lines to your build.sbt file:

```scala
libraryDependencies += "com.github.kmizu" %% "nson" % "0.0.1"
```

Then, you can use NValueParser as the followings:

```scala
import com.github.kmizu.nson.NValueParser
val nvalue = NValuePaser.parse(
  """
  | {x: 1 y: 2}
  """.stripMargin
)
println(nvalue.x.value) // => 1
println(nvalue.y.value) // => 2
```

## Syntax

### Integer

64-bit signed integer.

```js
10
20
0x10 // 16
-20  // -20
```

### Floating Point Number

IEEE 754 double precision floating point number

```js
1.0
1.5
2.0
3.0
```

### Boolean

```js
true
false
```

### String

```js
""
"Hello, World"
"Foo"
"Bar"
```

### Array

```js
[]
[1, 2, 3]
[1 2 3] //comma is omitted
[1
 2
 3] // line feed is also separator
```

### Object

```js
{"x" : 1, "y": 2}
{ x : 1 y : 2} //comma is omitted
{"x" : 1
 "y" : 2} // line feed is also separator
{ x : 1, y : 2} // identifier
{ x : 1  y : 2}
```
