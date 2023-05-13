(ns concrete-optics.traversal-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.traversal.structures :refer :all]
            [concrete-optics.traversal.axioms :refer :all]))

