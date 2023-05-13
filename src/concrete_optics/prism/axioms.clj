(ns concrete-optics.prism.axioms
  (:require [concrete-optics.prism.structures :refer [nothing?]]
            [concrete-optics.core :refer [review preview]]
            [concrete-optics.algebra.equality :refer [typed-eq]]))

(defn review-preview-axiom
  [optic part & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv part
           (->> part (review optic) (preview optic)))))

(defn preview-review-axiom
  [optic whole & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)
        previewed (preview optic whole)]
    (if (nothing? previewed) true (equiv (review optic previewed) whole))))
