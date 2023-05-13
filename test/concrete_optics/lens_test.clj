(ns concrete-optics.lens-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.lens.structures :refer :all]
            [concrete-optics.lens.axioms :refer :all]))

(def gen-map-with-keyword
  "Generates a keyword and a map which has the keyword."
  (gen/let [keyword gen/keyword
            value gen/any-equatable
            dummy-map (gen/map gen/keyword gen/any-equatable)]
    {:keyword keyword
     :map (assoc dummy-map keyword value)}))

(defspec field-get-put-test 100
  (prop/for-all [map-with-keyword gen-map-with-keyword
                 part gen/any-equatable]
                (get-put-axiom (field [(:keyword map-with-keyword)])
                               (:map map-with-keyword)
                               part)))

(defspec field-put-get-test 100
  (prop/for-all [map-with-keyword gen-map-with-keyword]
                (put-get-axiom (field [(:keyword map-with-keyword)])
                               (:map map-with-keyword))))

(defspec field-put-put-axiom 100
  (prop/for-all [map-with-keyword gen-map-with-keyword
                 part_1 gen/any-equatable
                 part_2 gen/any-equatable]
                (put-put-axiom (field [(:keyword map-with-keyword)])
                               (:map map-with-keyword)
                               part_1
                               part_2)))
