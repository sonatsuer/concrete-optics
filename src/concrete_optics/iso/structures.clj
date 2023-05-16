(ns concrete-optics.iso.structures
  "Isomorphism constructors and examples."
  (:require [concrete-optics.common :refer [classify]]))

(defn mk-iso
  "Constructs an isomorphism from a pair of functions which are
   inverse to each other."
  [view review]
  {:view view
   :to-list (fn [s] [(view s)])
   :over (fn [a-to-b] (fn [s] (review (a-to-b (view s)))))
   :review review
   :traverse (fn [app-f a-to-fb] (fn [s] ((:fmap app-f) review (a-to-fb (view s)))))})

(defn invert-iso
  "Swaps `view` and `review` for an isomorphism. If the input is
   not an isomorphism then it throws an exception."
  [optic]
  (if (= :iso (classify optic))
    (mk-iso (:review optic) (:view optic))
    (throw (Exception. "Only isomorphisms can be inverted."))))

(def eq
  "The isomorphism whose `view` and `review` are both `identity`.
   If the optic is not an isomorphism then it throws an exception."
  (mk-iso identity identity))

(def curried
  "The currying isomorphism between functions of *n* variables
   and *n+1* variables."
  (let [curry (fn [f] (fn [x] (fn [& rest] (apply f (concat (list x) rest)))))
        uncurry (fn [f] (fn [x & rest] (apply (f x) rest)))]
    (mk-iso curry uncurry)))
