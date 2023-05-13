(ns concrete-optics.algebra.axioms
  (:require [concrete-optics.algebra.equality :refer [typed-eq]]))

;; Semigroup and monoid axioms
(defn associativity-axiom
  "Checks the associativity axiom of the given semigroup. Meant to be used in tests."
  [semigroup x y z & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)
        op (.binary-op semigroup)]
    (equiv (op x (op y z)) (op (op x y) z))))

(defn left-unit-axiom
  "Checks the left unit axiom of the given monoid. Meant to be used in tests."
  [monoid x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x ((.binary-op monoid) (.unit monoid) x))))

(defn right-unit-axiom
  "Checks the right unit axiom of the given monoid. Meant to be used in tests."
  [monoid x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x ((.binary-op monoid) x (.unit monoid)))))

;; Applicative axioms
(defn id-functor-axiom
  "Checks the identity axiom for fmap of a given applicative. Meant to be used in tests."
  [applicative x & [comparison-function]]
  (let [equiv (or comparison-function typed-eq)]
    (equiv x ((.fmap applicative) identity x))))

(defn compose-functor-axiom
  "Checks the composition axiom for fmap of a given applicative. Meant to be used in tests."
  [applicative f g x & [comparison-function]]
  (let [fmap (.fmap applicative)
        equiv (or comparison-function typed-eq)]
    (equiv (fmap f (fmap g x)) (fmap (comp f g) x))))

(defn lifted-associativity-axiom
  "Checks the lifted assocaitivity axiom. Meant to be used in tests."
  [applicative fx fy fz]
  (let [associator (fn [left-associated]
                     [(first (first left-associated))
                      [(second (first left-associated))
                       (second left-associated)]])
        lifted-pair ((.binary-lift applicative) (fn [x y] [x y]))]
    (typed-eq ((.fmap applicative) associator (lifted-pair (lifted-pair fx fy) fz))
              (lifted-pair fx (lifted-pair fy fz)))))

(defn lifted-left-unit-axiom
  "Checks the lifted left identity axiom. Meant to be used in tests."
  [applicative fx]
  (let [lifted-pair ((.binary-lift applicative) (fn [x y] [x y]))]
    (typed-eq ((.fmap applicative) first (lifted-pair fx ((.unit applicative) :throw-away)))
              fx)))

(defn lifted-right-unit-axiom
  "Checks the lifted right identity axiom. Meant to be used in tests."
  [applicative fx]
  (let [lifted-pair ((.binary-lift applicative) (fn [x y] [x y]))]
    (typed-eq ((.fmap applicative) second (lifted-pair ((.unit applicative) :throw-away) fx))
              fx)))
