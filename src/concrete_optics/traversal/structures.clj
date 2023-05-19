(ns concrete-optics.traversal.structures
  "Traversal constructors an examples."
  (:require [concrete-optics.algebra.structures 
             :refer [const-applicative
                     vector-monoid
                     identity-applicative]]))

(defn mk-traversal
  "Constructs a traversal from a traverse function. A traverse
   function expects an applicative and a function which takes a
   value and produces another value in the given applicative. The 
   result returned by the traverse function again should be in
   the applicative."
  [traverse]
  {:traverse traverse
   :to-list (fn [s] ((traverse (const-applicative vector-monoid) (fn [a] [a])) s))
   :over (fn [a-to-b] (traverse identity-applicative (fn [a] (a-to-b a))))})

(def vector-traversal
  "This is the standard traversal structure on vectors."
  (mk-traversal
   (fn [app-f a-to-fb]
     (fn [s]
       (reduce (fn [fy x] (((:binary-lift app-f) conj) fy (a-to-fb x)))
               ((:pure app-f) [])
               s)))))

(defn ix
  "This tarversal focuses on a field which may not be present. So
   the `to-list` function on an `ix` tarversal produces a vector
   with at most one element."
  [k]
  (mk-traversal
   (fn [app-f a-to-fb]
     (fn [m]
       (if (contains? m k)
         ((:fmap app-f) (fn [x] (assoc-in m [k] x)) (a-to-fb (get-in m [k])))
         ((:pure app-f) m))))))


(defn- traverse-tree 
  [node? disassemble reassemble app-f a-to-fb t]
  (if (node? t)
    (a-to-fb t)
    (let [gather-applicatives (fn [l] (reduce ((:binary-lift app-f) conj) 
                                              ((:pure app-f) '()) 
                                              l) )
          descend (partial traverse-tree node? disassemble reassemble app-f a-to-fb)]
      ((:fmap app-f) reassemble (gather-applicatives (map descend (disassemble t)))))))

(defn tree-traversal
  "Standard tree tarversal. The function `disassemble` should turn the
   tree into a list of nodes and subrees and `reassemble` should be able to put it back
   preserving the shape of th tree without touching the contents of the tree"
  [node? disassemble reassemble]
  (mk-traversal
   (fn [app-f a-to-fb]
     (fn [t]
       (traverse-tree node? disassemble reassemble app-f a-to-fb t)))))
