(ns concrete-optics.iso.axioms
  (:require [concrete-optics.base :refer [view review]]
            [concrete-optics.algebra.equality :refer [typed-eq]]))

(defn view-review-axiom
  [optic x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x (->> x (view optic) (review optic)))))

(defn review-view-axiom
  [optic x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x (->> x (review optic) (view optic)))))
