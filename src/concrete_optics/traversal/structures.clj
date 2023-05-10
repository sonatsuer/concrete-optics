(ns concrete-optics.traversal.structures
  (:require [concrete-optics.algebra.structures 
             :refer [const-applicative
                     vector-monoid
                     identity-applicative]]))

(defn mk-traversal
  "traverse : applicative_f -> (a -> f b) -> (s -> f t)"
  [traverse]
  {:traverse traverse
   :to-list (fn [s] ((traverse (const-applicative vector-monoid) (fn [a] [a])) s))
   :over (fn [a-to-b] (traverse identity-applicative (fn [a] (a-to-b a))))})

(def vector-traversal
  (mk-traversal
   (fn [applicative_f a-to-fb]
     (fn [s]
       (reduce (fn [fy x] (((.binary-lift applicative_f) conj) fy (a-to-fb x)))
               ((.unit applicative_f) [])
               s)))))

(defn ix
  [k]
  (mk-traversal
   (fn [applicative_f a-to-fb]
     (fn [m]
       (if (contains? m k)
         ((:fmap applicative_f) (fn [x] (assoc-in m [k] x)) (a-to-fb (get-in m [k])))
         ((:unit applicative_f) m))))))
