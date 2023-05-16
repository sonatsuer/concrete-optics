(ns concrete-optics.iso.structures
  (:require [concrete-optics.common :refer [classify]]))

(defn mk-iso
  "Constructs an isomorphism from a pair of functions which are
   inverse to each other."
  [f g]
  {:view f
   :to-list (fn [s] [(f s)])
   :over (fn [a-to-b] (fn [s] (g (a-to-b (f s)))))
   :review g
   :traverse (fn [app-f a-to-fb] (fn [s] ((:fmap app-f) g (a-to-fb (f s)))))})

(defn invert-iso
  [optic]
  (if (= :iso (classify optic))
    (mk-iso (:review optic) (:view optic))
    (throw (Exception. "Only isomorphisms can be inverted."))))

(def eq
  (mk-iso identity identity))

(def curried
  (let [curry (fn [f] (fn [x] (fn [y] (f x y))))
        uncurry (fn [f] (fn [x y] ((f x) y)))]
    (mk-iso curry uncurry)))
