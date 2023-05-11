(ns concrete-optics.showcase
  (:require [clojure.test :refer [deftest testing is]]
            [concrete-optics.common :refer [typed-eq]]
            [concrete-optics.core :as opt]
            [concrete-optics.algebra.structures :as alg]))

;; This test file also serves as documentation.

;; Isomorphisms
;; ------------

;; Isomorphisms allow us to change the representation of data
;; without loosing infromation.

;; Lenses
;; Nested data, composition with iso, weight record

;; Prisms
;; Filtering, decomposing

;; Traversals
;; ----------

;; The combinator `traverse` generalizes `reduce` with an arbitrary
;; monoid using the `const-applicative`. For instance here is a
;; very indirect implementation of `count` for vectors.

(defn vector-length
  [vec]
  (opt/traverse opt/vector-traversal 
                (alg/const-applicative alg/additive-monoid) 
                (constantly 1) 
                vec))

(deftest vector-length-test
  (testing "vector-length indeed computes the length"
    (is (typed-eq (vector-length [1 2 3 4 5]) 5)))
  (testing "testing for empty vector"
    (is (typed-eq (vector-length []) 0))))

;; In combination with other optics, traverse can do more interesting things
;; like accessing or modifying groups of nested data.

(def some-data
  [{:a 1 :b 2} {:c 3} {:a -5} {:a 7 :z 22}])

(def each-positive-a
  (opt/optic-compose opt/vector-traversal 
                     (opt/ix :a) 
                     (opt/predicate-prism "positive" #(> % 0))))

(deftest list-positive-as-test
  (testing "listing elements with a filtering condition"
    (is (typed-eq (opt/to-list each-positive-a some-data)
           [1 7]))))

(deftest modify-positive-as-test
  (testing "modifying only the values fitting a filtering condition"
    (is (typed-eq (opt/over each-positive-a inc some-data)
           [{:a 2, :b 2} {:c 3} {:a -5} {:a 8, :z 22}]))))

;; There are a lot of useful applicative structures that can be used with
;; `traverse`. Here are a few examples for validating data.

(def some-numbers
  [1 -4 6 7 -9 7 -3])

(def some-nonnegative-numbers
  [1 16 25 9 1 4])

(defn fancy-sqrt
  [x]
  (if (< x 0) 
    (alg/fail-with (str "Cannot take the square root of " x))
    (Math/sqrt x)))

(defn fail-fast-validation
  [numbers]
  (opt/traverse opt/vector-traversal 
                alg/fail-fast-applicative 
                fancy-sqrt 
                numbers))

(deftest fail-fast-validation-test
  (testing "if there are errors, only the first one is kept"
    (is (typed-eq (fail-fast-validation some-numbers) 
           {:failure "Cannot take the square root of -4"})))
  (testing "if there are no errors the result is returned"
    (is (typed-eq (fail-fast-validation some-nonnegative-numbers) 
           [1.0 4.0 5.0 3.0 1.0 2.0]))))

(defn collect-errors-validation
  [numbers]
  (opt/traverse opt/vector-traversal 
                alg/collect-errors-applicative 
                (alg/map-failure (fn [x] [x]) fancy-sqrt)
                numbers))

(deftest collect-errors-validation-test
  (testing "if there are errors, all of them are returned in a vector"
    (is (typed-eq (collect-errors-validation some-numbers)
           {:failure ["Cannot take the square root of -4"
                      "Cannot take the square root of -9"
                      "Cannot take the square root of -3"]})))
  (testing "if there are no errors the result is returned"
    (is (typed-eq (collect-errors-validation some-nonnegative-numbers)
           [1.0 4.0 5.0 3.0 1.0 2.0]))))

;; We can even cook up an applicative which, say, counts  errors or determines
;; the maximum severity of errors. Here is an implementation for error counting.
;; We leave the severity example as an exercise.
(defn count-errors-validation
  [numbers]
  (opt/traverse opt/vector-traversal 
                (alg/validation-applicative alg/additive-monoid)
                (alg/map-failure (constantly 1) fancy-sqrt)
                numbers))

(deftest count-errors-validation-test
  (testing "if there are errors, the error count is returned"
    (is (typed-eq (count-errors-validation some-numbers)
           {:failure 3})))
  (testing "if there are no errors the result is returned"
    (is (typed-eq (count-errors-validation some-nonnegative-numbers)
           [1.0 4.0 5.0 3.0 1.0 2.0]))))

;; Setters, Getters and Folds
;; mapped, getter as a convenience
