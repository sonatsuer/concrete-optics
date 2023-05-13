(ns concrete-optics.algebra.structures)

;; A semigroup is a set together with an associative binary operation.
(defrecord Semigroup [binary-op])

(def additive-semigroup
  (Semigroup. +))

(def first-semigroup
  (Semigroup. (fn [x _y] x)))

(def vector-semigroup
  (Semigroup. (fn [x y] (into [] (concat x y)))))

;; A monoid is a semigroup with a unit element.
(defrecord Monoid [binary-op unit])

(def additive-monoid
  (Monoid. (.binary-op additive-semigroup) 0))

(def vector-monoid
  (Monoid. (.binary-op vector-semigroup) []))

(defn monoid->semigroup
  "Demotes a monoid to a semigroup by forgetting its identity element."
  [monoid]
  (Semigroup. (.binary-op monoid)))

;; An applicative is a type constructor wich allows lifting binary operations
(defrecord Applicative [fmap unit binary-lift])

(def identity-applicative
  (Applicative.
   (fn [f x] (f x))
   identity
   (fn [binary-op] (fn [x y] (binary-op x y)))))

(defn const-applicative
  "Promotes a monoid into an applicative."
  [monoid]
  (Applicative.
   (fn [_f const] const)
   (fn [_x] (.unit monoid))
   (fn [_binary-op] (fn [x y] ((.binary-op monoid) x y)))))

;; Validation related structures
(defn failure?
  "Checks if the input represents a failure."
  [x]
  (and (map? x) (= (keys x) '(:failure))))

(defn fail-with
  [x]
  {:failure x})

(defn validation-applicative
  "Constructs an applicative where failures are accumulated using the semigroup."
  [semigroup]
  (Applicative.
   (fn [f x] (if (failure? x) x (f x)))
   identity
   (fn [binary-op]
     (fn [x y]
       (case [(failure? x) (failure? y)]
         [true true] (fail-with ((.binary-op semigroup) (:failure x) (:failure y)))
         [true false] x
         [false true] y
         [false false] (binary-op x y))))))

(defn map-failure
  [to-new-failure f]
  (fn [x] (let [result (f x)]
            (if (failure? result)
              (fail-with (to-new-failure (:failure result)))
              result))))

(def fail-fast-applicative
  (validation-applicative first-semigroup))

(def collect-errors-applicative
  (validation-applicative vector-semigroup))
