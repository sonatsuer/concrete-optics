(ns concrete-optics.iso-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.iso.structures :refer :all]
            [concrete-optics.iso.axioms :refer :all]))

(defspec eq-view-review-test 100
  (prop/for-all [x gen/any-equatable]
                (view-review-axiom eq x)))

(defspec eq-review-view-test 100
  (prop/for-all [x gen/any-equatable]
                (review-view-axiom eq x)))
