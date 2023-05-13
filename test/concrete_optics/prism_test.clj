(ns concrete-optics.prism-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.core :refer [review]]
            [concrete-optics.prism.structures :refer :all]
            [concrete-optics.prism.axioms :refer :all]))

(def nonnegative-prism
  (predicate-prism #(>= % 0) "positive"))

(defspec predicate-review-preview-test 100
  (prop/for-all [part gen/nat]
                (review-preview-axiom nonnegative-prism part)))

(defspec predicate-preview-review-test 100
  (prop/for-all [whole gen/small-integer]
                (preview-review-axiom nonnegative-prism whole)))

(defspec predicate-review-throws 100
  (prop/for-all [negative (gen/fmap #(- -1 %) gen/nat)]
                (try ((constantly false) (review nonnegative-prism negative))
                     (catch Exception _exception true))))

(defspec cons-prism-review-preview-test 100
  (prop/for-all [head gen/any-equatable
                 tail (gen/vector gen/any-equatable)]
                (review-preview-axiom cons-prism {:head head :tail tail})))

(defspec cons-prism-preview-review-test 100
  (prop/for-all [vec (gen/vector gen/any-equatable)]
                (preview-review-axiom cons-prism vec)))
