(ns concrete-optics.iso-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.algebra.equality :refer [typed-eq compare-functions]]
            [concrete-optics.iso.structures :refer :all]
            [concrete-optics.iso.axioms :refer :all]))

(defspec eq-view-review-test 100
  (prop/for-all [x gen/any-equatable]
                (view-review-axiom eq x)))

(defspec eq-review-view-test 100
  (prop/for-all [x gen/any-equatable]
                (review-view-axiom eq x)))

(defspec curried-view-review-test 1
  (let [f (fn [x y] {:x x :y y})
        sample-size 500
        generator (gen/vector gen/any-equatable sample-size)]
    (prop/for-all [xs generator
                   ys generator]
                  (view-review-axiom curried f (compare-functions typed-eq xs ys)))))

(defspec curried-review-view-axiom-test 1
  (let [f (fn [x] (fn [y] {:x x :y y}))
        sample-size 500
        generator (gen/vector gen/any-equatable sample-size)]
    (prop/for-all [fs (gen/fmap (fn [vec] (into [] (map f vec))) generator)
                   xs generator]
                  (review-view-axiom curried f (compare-functions (compare-functions typed-eq xs) fs)))))
