# Introduction to concrete-optics

TLDR; Optics are general and composable constructions that allow bidirectional access to immutable data.

Here is an example of an optic --a traversal to be more precise-- which focuses on positive elements with key `:a` inside a map inside a vector.

```clojure
(def some-data
  [{:a 1 :b 2} {:c 3} {:a -5} {:a 7 :z 22}])

(def each-positive-a
  (optic-compose vector-traversal (ix :a) (predicate-prism "positive" (fn [x] (> x 0)))))

(def list-positive-as
  ((:to-list each-positive-a) some-data))
;; => [1 7]

(def increase-positive-as
  (((:over each-positive-a) inc) some-data))
;; => [{:a 2, :b 2} {:c 3} {:a -5} {:a 8, :z 22}]
```

## The Big Picture

There is a very general definition of optics which relies on a nontrivial amount of mathematical background. We will not be working with that here. Instead, we will take a more *concrete* approach and work with certain capabilities an optic can posses. Each optic will be determined by the capabilities it has.

As a simple mental model, we can think of an optic connecting a *big* whole to a *small* part. Sometimes we say that the optic focuses on the small part. Then the capabilities these optic capabilities have the following intuitive meanings.

* **view**: Gives you the small part of the whole. Typically field accessors are views.
* **to-list**: Very much like **view**, but instead of a single element it focuses on many --possibly zero-- elements.
* **review**: Given an element of the small part, constructs a big whole. Typically sum type constructors are reviews.
* **over**: Turns a transformation over the small part into a transformation of the whole.
* **traverse**: Allows you to perform a "structured for loop" over the focus.

There are also formal laws we expect these capabilities to satisfy. You can find their precise formulations in the test modules.

Here is a table which classifies optics in terms of their capabilities.
|               |  view | to-list| review | over | traverse |
| ------------- |:-----:|:------:|:------:|:----:|:--------:|
| isomorphism   |   ✓   |    ✓   |   ✓    |  ✓   |    ✓     |
| lens          |   ✓   |    ✓   |        |  ✓   |    ✓     |
| prism         |       |    ✓   |   ✓    |  ✓   |    ✓     |
| traversal     |       |    ✓   |        |  ✓   |    ✓     |
| setter        |       |        |        |  ✓   |          |
| getter        |   ✓   |    ✓   |        |      |          |
| fold          |       |    ✓   |        |      |          |

When we compose two optics, we compose their capabilities. If a capability is not a common capability of the optics then it is lost. For instance composition of two optics of the same kind is another of the same kind. An optic and its composition of an optic with an isomorphism are of the same kind since isomorphisms have all the capabilities. But the composition of a prism and a lens is a traversal.

## Isomorphisms

## Lenses

## Prisms

## Traversal

## Setter, Getters and Folds
