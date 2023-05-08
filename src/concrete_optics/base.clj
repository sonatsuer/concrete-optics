(ns concrete-optics.base
  (:require [concrete-optics.iso.structures :refer [eq]]
            [concrete-optics.common :refer [get-capability]]))

(defn preview
  "Tries to focus on a snge element, produces :nothing if fails."
  [optic whole]
  (let [lst ((get-capability :to-list optic) whole)]
    (if (empty? lst) :nothing (first lst))))

(defn put
  "Replaces the element in the whole by the new focus. Traditionally
   calles set but it clasehs witht the data structure set."
  [optic focus whole]
  ((get-capability :over optic) (constantly focus)) whole)

(defn- apply-binary-nonnil 
  [binary-op x y]
  (if (or (nil? x) (nil? y)) nil (binary-op x y)))

(defn- vector-fish
  "Returns a function from a to [c]. Special case of (>=>)"
  [a-to-vec-b b-to-vec-c]
  (fn [a] (into [] (mapcat b-to-vec-c (a-to-vec-b a)))))

(defn- rev-comp
  "Function composition but the arguments are swapped."
  [f g]
  (fn [a] (g (f a))))

(defn- optic-compose-binary
  [optic1 optic2]
  (let
   [compose-capability (fn [field-name binary-op] (apply-binary-nonnil binary-op (field-name optic1) (field-name optic2)))
    candidates {:view (compose-capability :view rev-comp)
                :to-list (compose-capability :to-list vector-fish)
                :over (compose-capability :over comp)
                :review (compose-capability :review comp)
                :traverse (compose-capability :traverse comp)}]
    (into {} (filter (comp some? val) candidates))))

(defn optic-compose
  ([] eq)
  ([optic] optic)
  ([optic & rest] (optic-compose-binary optic (reduce optic-compose-binary eq rest))))

