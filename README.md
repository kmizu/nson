# nson

NSON is a object notation that is not a JSON but alike JSON.

NSON is a new object notation like JSON and unlike JSON. NSON is strict superset of JSON and differ from JSON in that NObject doesn't require any comma as a separator character.

For example, the following example, including several properties, doesn't have any comma:

```js
{"name": "Kota Mizushima" "age": 33
  "like" : ["Scala" "Haskell" "Nemerle" "Rust"]}
```

This repository provides a parser of NSON.

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
