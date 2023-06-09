# Introduction to concrete-optics

## TL;DR

Optics are general and composable constructions that allow bidirectional access to immutable data.

Here is an example of an optic --a traversal to be more precise-- which focuses on positive elements with key `:a` inside a map inside a vector.

```clojure
(def nested-data
  [{:a 1 :b 2} {:c 3} {:a -5} {:a 7 :z 22}])

(def each-positive-a
  (opt/compose opt/vector-traversal
               (opt/ix :a)
               (opt/predicate-prism #(> % 0))))

(deftest list-positive-as-test
  (testing "listing elements with a filtering condition"
    (is (= (opt/to-list each-positive-a nested-data)
           [1 7]))))

(deftest modify-positive-as-test
  (testing "modifying only the values fitting a filtering condition"
    (is (= (opt/over each-positive-a inc nested-data)
           [{:a 2, :b 2} {:c 3} {:a -5} {:a 8, :z 22}]))))
```

## Implementation Idea

There is a very general [definition](https://ncatlab.org/nlab/show/optic+%28in+computer+science%29) of optics which relies on a nontrivial amount of mathematical background. We will not be working with that here. Instead, we will take a more *concrete* approach and work with certain capabilities an optic can have. Behind the curtains, each optic will be a map of capabilities.

As a simple mental model, we can think of an optic connecting a *big* whole to a *small* part. Sometimes we say that the optic focuses on the small part. Here is a list of capabilities with their intuitive meanings:

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

When we compose two optics, we compose their corresponding capabilities. If a capability is not a common capability of the optics then it is lost. For instance composition of two optics of the same kind is another of the same kind. An optic and its composition with an isomorphism are of the same kind since isomorphisms have all the capabilities. But the composition of a prism and a lens is a traversal.

## What is Missing

This framework can easily support the optics `getter` (which has `view` and `to-list`), `setter` (which has `over`) and `fold` (which has `to-list`). I omitted them since they are not as useful.

There is a general notion duality in optics where you change the roles of the whole and the piece. For instance the function `invert-iso` is an implementation of this idea. However, in this capability only formalism, it is not possible to invert, say, a prism twice and get the original prism. The system can certainly be expanded to support this, though.

Exotic optics like grates or achromatic lenses are not implemented because, well, they are exotic and implementing them  would require a change in the current design which is dead simple.
