(ns concrete-optics.algebra-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.algebra.structures :refer :all]
            [concrete-optics.algebra.axioms :refer :all]))

;; First semigroup
(defspec first-semigroup-assoc 100
  (prop/for-all [x gen/int 
                 y gen/int 
                 z gen/int]
                (associativity-axiom first-semigroup x y z)))

;; Additive monoid
(defspec additive-monoid-assoc 100
  (prop/for-all [x gen/int 
                 y gen/int 
                 z gen/int]
                (associativity-axiom (monoid->semigroup additive-monoid) x y z)))

(defspec additive-monoid-left-unit 100
  (prop/for-all [x gen/int]
                (left-unit-axiom additive-monoid x)))

(defspec additive-monoid-right-unit 100
  (prop/for-all [x gen/int]
                (right-unit-axiom additive-monoid x)))

;; Vector monoid
(defspec vector-monoid-assoc 100
  (prop/for-all [x (gen/vector gen/any-equatable) 
                 y (gen/vector gen/any-equatable) 
                 z (gen/vector gen/any-equatable)]
                (associativity-axiom (monoid->semigroup vector-monoid) x y z)))

(defspec vector-monoid-left-unit 100
  (prop/for-all [x (gen/vector gen/any-equatable)]
                (left-unit-axiom vector-monoid x)))

(defspec vector-monoid-right-unit 100
  (prop/for-all [x (gen/vector gen/any-equatable)]
                (right-unit-axiom vector-monoid x)))

;; Auxiliary function
(defn- check-compose-with-wrapper-functions
  [applicative x]
  (let [f (fn [u] {:f u})
        g (fn [u] {:g u})]
    (compose-functor-axiom applicative f g x)))

;; Constant applicative (with vector monoid)
(def const-vector-app 
  (const-applicative vector-monoid))

(defspec const-applicative-id 100
  (prop/for-all [x gen/any-equatable]
                (id-functor-axiom const-vector-app x)))

(defspec const-applicative-compose 100
  (prop/for-all [x gen/any-equatable]
                (check-compose-with-wrapper-functions const-vector-app x)))

(defspec const-applicative-assoc 100
  (prop/for-all [fx (gen/vector gen/any-equatable)
                 fy (gen/vector gen/any-equatable)
                 fz (gen/vector gen/any-equatable)]
                (lifted-associativity-axiom const-vector-app fx fy fz)))

(defspec const-applicative-left-unit 100
  (prop/for-all [fx (gen/vector gen/any-equatable)]
                (lifted-left-unit-axiom const-vector-app fx)))

(defspec const-applicative-right-unit 100
  (prop/for-all [fx (gen/vector gen/any-equatable)]
                (lifted-right-unit-axiom const-vector-app fx)))

;; Identity applicative s
(defspec identity-applicative-id 100
  (prop/for-all [x gen/any-equatable]
                (id-functor-axiom identity-applicative x)))

(defspec identity-applicative-compose 100
  (prop/for-all [x gen/any-equatable]
                (check-compose-with-wrapper-functions identity-applicative x)))

(defspec identity-applicative-assoc 100 
  (prop/for-all [fx gen/any-equatable
                 fy gen/any-equatable
                 fz gen/any-equatable]
                (lifted-associativity-axiom identity-applicative fx fy fz)))

(defspec identity-applicative-left-unit 100
  (prop/for-all [fx gen/any-equatable]
                (lifted-left-unit-axiom identity-applicative fx)))

(defspec identity-applicative-right-unit 100
  (prop/for-all [fx gen/any-equatable]
                (lifted-right-unit-axiom identity-applicative fx)))

;; Fail-Fast applicative
(def gen-fail-fast 
  (gen/one-of [(gen/fmap (fn [x] {:failure x}) gen/any-equatable) gen/any-equatable]))

(defspec fail-fast-applicative-id 100
  (prop/for-all [x gen/any-equatable]
                (id-functor-axiom identity-applicative x)))

(defspec fail-fast-applicative-compose 100
  (prop/for-all [x gen/any-equatable]
                (check-compose-with-wrapper-functions identity-applicative x)))

(defspec fail-fast-applicative-assoc 100
  (prop/for-all [fx gen-fail-fast
                 fy gen-fail-fast
                 fz gen-fail-fast]
                (lifted-associativity-axiom identity-applicative fx fy fz)))

(defspec fail-fast-applicative-left-unit 100
  (prop/for-all [fx gen-fail-fast]
                (lifted-left-unit-axiom identity-applicative fx)))

(defspec fail-fast-applicative-right-unit 100
  (prop/for-all [fx gen-fail-fast]
                (lifted-right-unit-axiom identity-applicative fx)))

;; Collect-Errors applicative
(def gen-collect-errors
  (gen/one-of [(gen/fmap (fn [x] {:failure x}) (gen/vector gen/any-equatable)) gen/any-equatable]))

(defspec collect-errors-applicative-id 100
  (prop/for-all [x gen/any-equatable]
                (id-functor-axiom identity-applicative x)))

(defspec collect-errors-applicative-compose 100
  (prop/for-all [x gen/any-equatable]
                (check-compose-with-wrapper-functions identity-applicative x)))

(defspec collect-errors-applicative-assoc 100
  (prop/for-all [fx gen-collect-errors
                 fy gen-collect-errors
                 fz gen-collect-errors]
                (lifted-associativity-axiom identity-applicative fx fy fz)))

(defspec collect-errors-applicative-left-unit 100
  (prop/for-all [fx gen-collect-errors]
                (lifted-left-unit-axiom identity-applicative fx)))

(defspec collect-errors-applicative-right-unit 100
  (prop/for-all [fx gen-collect-errors]
                (lifted-right-unit-axiom identity-applicative fx)))
