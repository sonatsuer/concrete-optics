(ns concrete-optics.lens.axioms
  (:require [concrete-optics.lens.structures :refer :all]
            [concrete-optics.core :refer [put view]]
            [concrete-optics.algebra.equality :refer [typed-eq]]))

(defn get-put-axiom
  [optic whole part & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv part 
           (->> whole (put optic part) (view optic)))))

(defn put-get-axiom
  [optic whole & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv whole
           (put optic (view optic whole) whole))))

(defn put-put-axiom 
  [optic whole part_1 part_2 & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv (put optic part_2 whole)
           (->> whole (put optic part_1) (put optic part_2)))))
