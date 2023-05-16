(ns concrete-optics.lens.axioms
  "Lens axioms."
  (:require [concrete-optics.core :refer [put view]]
            [concrete-optics.algebra.equality :refer [typed-eq]]))

(defn get-put-axiom
  "Checks the lens axiom which states `put` composed
   with `view` gives you the original part you put. If a
   comparison function is not provided then `typed-eq` is used."
  [optic whole part & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv part
           (->> whole (put optic part) (view optic)))))

(defn put-get-axiom
  "Checks the lens axiom which states `put` composed
   with `view` gives you the original part you put. If a
   comparison function is not provided then `typed-eq` is used."
  [optic whole & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv whole
           (put optic (view optic whole) whole))))

(defn put-put-axiom
  "Checks the lens axiom which states if you `put` two
   values in a whole then the second one overrides the first one.
   If a comparison function is not provided then `typed-eq` is used."
  [optic whole part_1 part_2 & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv (put optic part_2 whole)
           (->> whole (put optic part_1) (put optic part_2)))))
