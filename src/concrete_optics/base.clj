(ns concrete-optics.base
  (:require [concrete-optics.iso.structures :refer [eq]]
            [concrete-optics.common :refer [get-capability]]))

;; Capability combinators
(defn preview
  "Tries to focus on a sinlge element, produces :nothing if fails."
  [optic whole]
  (let [lst ((get-capability :to-list optic) whole)]
    (if (empty? lst) :nothing (first lst))))

(defn put
  "Replaces the element in the whole by the new focus. Traditionally
   calles set but it clasehs witht the data structure set."
  [optic focus whole]
  ((get-capability :over optic) (constantly focus)) whole)

(defn view 
  "Given an optic and a whole, returns the focus."
  [optic whole]
  ((get-capability :view optic) whole))

(defn to-list
  "Given an optic and a whole, returns the focus list."
  [optic whole]
  ((get-capability :to-list optic) whole))

(defn review
  "Generates a whole from a given part."
  [optic part]
  ((get-capability :review optic) part))

(defn over 
  "Lifts a transformation over the part to a transformation on the whole."
  [optic transformation whole]
  (((get-capability :over optic) transformation) whole))

(defn traverse
  "Reduces a collection inside an applicative."
  [optic applicative part-processor whole]
  (((get-capability :traverse optic) applicative part-processor) whole))

;; Optic compostition
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

(defn- compose-binary
  [optic1 optic2]
  (let
   [compose-capability (fn [field-name binary-op] (apply-binary-nonnil binary-op (field-name optic1) (field-name optic2)))
    candidates {:view (compose-capability :view rev-comp)
                :to-list (compose-capability :to-list vector-fish)
                :over (compose-capability :over comp)
                :review (compose-capability :review comp)
                :traverse (compose-capability :traverse comp)}]
    (into {} (filter (comp some? val) candidates))))

(defn compose
  ([& optics] (reduce compose-binary eq optics)))
