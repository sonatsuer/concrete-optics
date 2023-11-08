# concrete-optics

A simple minded implementation of optics in Clojure. Optics are general and composable constructions that allow bidirectional access to immutable data. For a general introduction to optics see the documentation of [optics](https://hackage.haskell.org/package/optics-0.4.2/docs/Optics.html) library in Haskell.

This library is experimental/educational and it is not intended to be used in production code. If you
need to manipulate nested data you should probably use [Specter](https://github.com/redplanetlabs/specter).

## Documents

[The docs](http://sonatsuer.github.io/concrete-optics) contain a showcase, a high level description of the design and the API reference. You can generate documents by running `./mk-docs.sh` and view
the docs locally under `docs/index.html`.

## License

Distributed under the Eclipse Public License version 1.0.
