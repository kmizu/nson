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
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.github.kmizu" %% "nson" % "0.0.1-SNAPSHOT"
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
