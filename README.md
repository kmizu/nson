# nson

NSON is a object notation that is not a JSON but alike JSON.

NSON is a new object notation like JSON and unlike JSON. NSON is strict superset of JSON and differ from JSON in that NObject doesn't require any comma as a separator character.

For example, the following example, including several properties, doesn't have any comma:

```js
{"name": "Kota Mizushima" "age": 33
  "like" : ["Scala" "Haskell" "Nemerle" "Rust"]}
```

This repository will provide a parser of NSON.
