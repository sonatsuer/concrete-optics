(ns concrete-optics.iso.axioms
  "Axioms for isomorphisms."
  (:require [concrete-optics.base :refer [view review]]
            [concrete-optics.algebra.equality :refer [typed-eq]]))

(defn view-review-axiom
  "Checks the isomorphism axiom which states `view` composed
   with `review` is identity on the given optic and the input. If a
   comparison function is not provided then `typed-eq` is used."
  [optic x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x (->> x (view optic) (review optic)))))

(defn review-view-axiom
  "Checks the isomorphism axiom which states `review` composed
   with `view` is identity on the given optic and the input. If a
   comparison function is not provided then `typed-eq` is used."
  [optic x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x (->> x (review optic) (view optic)))))
