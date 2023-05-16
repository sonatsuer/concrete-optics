# concrete-optics

A simple minded implementation of optics in Clojure. Optics are general and composable constructions that allow bidirectional access to immutable data. For a general introduction to optics see the documentation of [optics](https://hackage.haskell.org/package/optics-0.4.2/docs/Optics.html) library in Haskell.

## Usage

See the [showcase](test/concrete_optics/showcase.clj) module under `test` for use cases. See [intro](doc/intro.md) under `doc` for a broad introduction.

## License

Distributed under the Eclipse Public License version 1.0.

## TODO

- [ ] Add tests for tarversals.
- [ ] Improve applicative tests: Make the lax-monoidal structure explicit, also test for naturality.
- [ ] Implement tree traversal.
