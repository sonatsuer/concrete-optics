(ns concrete-optics.prism.axioms
  "Prism axioms."
  (:require [concrete-optics.prism.structures :refer [no-match?]]
            [concrete-optics.core :refer [review preview]]
            [concrete-optics.algebra.equality :refer [typed-eq]]))

(defn review-preview-axiom
   "Checks the prism axiom which states `review` composed
    with `preview` is identity. If a comparison function is not
    provided then `typed-eq` is used."
  [optic part & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv part
           (->> part (review optic) (preview optic)))))

(defn preview-review-axiom
  "Checks the prism axiom which states `preview` composed
   with `review` is identity, provided that preview does not
   produce a `:no-match`. If a comparison function is not
   provided then `typed-eq` is used."
  [optic whole & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)
        previewed (preview optic whole)]
    (if (no-match? previewed) true (equiv (review optic previewed) whole))))
