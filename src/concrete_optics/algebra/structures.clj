(ns concrete-optics.algebra.structures
  "Constructors and examples of semigroups, monoids, apply
   and applicatives." )

(defn mk-semigroup
  "Wraps a binary associative operator as `{:binary-op binary-op}`.
   If in addition there is a unit then the result is the map
   `{:binary-op binary-op :unit unit}`. If there is a unit, the
   semigroup is indeed a monoid. A unit should not be encoded by a `nil.`"
  ([binary-op] {:binary-op binary-op})
  ([binary-op unit] {:binary-op binary-op :unit unit}))

(def additive-monoid
  "The semigroup on numbers whose opearion is addition. Since
   addition also has a unit, namely `0`, this actually defines
   a monoid."
  (mk-semigroup + 0))

(def first-semigroup
  "The semigroup whose operation picks the first argumet and 
   ignores the second. There is no unit for this operation."
  (mk-semigroup (fn [x _y] x)))

(def vector-monoid
  "The semigroup on vectors whose operation is concatenation. Since
   concatenation has a unit, namely `[]`, this is actually a monoid."
  (mk-semigroup (fn [x y] (into [] (concat x y))) []))

(defn mk-apply
  "Wraps a mapping operation and a binary operation lifter as
   `{:fmap fmap :binary-lift binary-lift}`. We assume that the 
   wrapped functions satisfy the 'semimonoidal functor' laws.
   If there is also a pure operation which injects a regular
   value into the context then we assume that the wrapped values
   form an 'applicative functor'."
  ([fmap binary-lift] {:fmap fmap :binary-lift binary-lift})
  ([fmap binary-lift pure] {:fmap fmap :binary-lift binary-lift :pure pure})
  )

(def identity-applicative
  "This is the trivial applicative where there is no actual lifting."
  (mk-apply
   (fn [f x] (f x))
   (fn [binary-op] (fn [x y] (binary-op x y)))
   identity))

(defn const-applicative
  "Promotes a semigroup into an apply. If the semigroup is a
   a monoid then the result is an applicative."
  [semigroup]
  (mk-apply
   (fn [_f const] const) 
   (fn [_binary-op] (fn [x y] ((:binary-op semigroup) x y)))
   (fn [_x] (:unit semigroup))))

;; Validation related structures
(defn failure?
  "Checks if the input represents a failure, that it, if it is a map
   of the form `{:failure <report>}`."
  [x]
  (and (map? x) (= (keys x) '(:failure))))

(defn fail-with
  "Wraps a value as a failure so that `failure?` can detect it."
  [x]
  {:failure x})

(defn validation-applicative
  "Constructs an applicative where failures are accumulated using the semigroup."
  [semigroup]
  (mk-apply
   (fn [f x] (if (failure? x) x (f x)))
   (fn [binary-op]
     (fn [x y]
       (case [(failure? x) (failure? y)]
         [true true] (fail-with ((:binary-op semigroup) (:failure x) (:failure y)))
         [true false] x
         [false true] y
         [false false] (binary-op x y))))
   identity))

(defn map-failure
  "Changes the failure type for a validation function."
  [to-new-failure f]
  (fn [x] (let [result (f x)]
            (if (failure? result)
              (fail-with (to-new-failure (:failure result)))
              result))))

(def fail-fast-applicative
  "A failure applicative where only the first failure is kept."
  (validation-applicative first-semigroup))

(def collect-errors-applicative
  "A failure where all failures are collected in a vector."
  (validation-applicative vector-monoid))
