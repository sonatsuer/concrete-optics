(ns concrete-optics.base
  "Core and derived optic capabilites and composition."
  (:require [concrete-optics.iso.structures :refer [eq]]
            [concrete-optics.common :refer [get-capability]]))

;; Capability combinators
(defn preview
  "Tries to focus on a sinlge element, produces `:no-match` if it fails.
   Note that the failure to find the element does not result in `nil` because
   that would break `(predicate-prism nil?)`. If the optic does not have
   the `to-list` capability then it throws an exception because `preview`
   is defined in terms of `to-list`."
  [optic whole]
  (let [lst ((get-capability :to-list optic) whole)]
    (or (first lst) :no-match)))

(defn put
  "Replaces the element in the whole by the new focus. Traditionally
   called set but it clashes with the data structure set. If the optic
   does not have the `over` capability then it throws an exception because
   `put` is defined in terms of `over`."
  [optic focus whole]
  (((get-capability :over optic) (constantly focus)) whole))

(defn view
  "Given an optic and a whole, returns the focus. If the optic does not have
   the `view` capability then it throws an exception."
  [optic whole]
  ((get-capability :view optic) whole))

(defn to-list
  "Given an optic and a whole, returns the focus list. If the optic does not have
   the `to-list` capability then it throws an exception."
  [optic whole]
  ((get-capability :to-list optic) whole))

(defn review
  "Generates a whole from a given part. If the optic does not have
   the `review` capability then it throws an exception."
  [optic part]
  ((get-capability :review optic) part))

(defn over
  "Lifts a transformation over the part to a transformation on the whole. If
   the optic does not have the `over` capability then it throws an exception."
  [optic transformation whole]
  (((get-capability :over optic) transformation) whole))

(defn traverse
  "Reduces a collection inside an applicative. If the optic does not have
   the `traverse` capability then it throes an exception."
  [optic applicative part-processor whole]
  (((get-capability :traverse optic) applicative part-processor) whole))

;; Optic compostition
(defn- fail-on-nil
  [f]
  (fn [& params] (if (every? some? params) (apply f params) nil)))

(def ^:private forward-compose
  (fail-on-nil comp))

(def ^:private backward-compose
  (fail-on-nil (fn [& functions] (apply comp (reverse functions)))))

(def ^:private vector-kleisli
  (fail-on-nil
   (fn [a-to-vec-b b-to-vec-c]
     (fn [a] (into [] (mapcat b-to-vec-c (a-to-vec-b a)))))))

(defn- compose-binary
  [optic1 optic2]
  (let
   [compose-capability (fn [field-name binary-op] (binary-op (field-name optic1) (field-name optic2)))
    candidates {:view (compose-capability :view backward-compose)
                :to-list (compose-capability :to-list vector-kleisli)
                :over (compose-capability :over forward-compose)
                :review (compose-capability :review forward-compose)
                :traverse (compose-capability :traverse forward-compose)}]
    (into {} (filter (comp some? val) candidates))))

(defn compose
  "General optic composition. Accepts an arbitrary list of 
   optics. It produces the `eq`, the unit on optic composition, 
   for the empty list."
  ([& optics] (reduce compose-binary eq optics)))
