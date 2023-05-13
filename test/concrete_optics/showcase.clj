(ns concrete-optics.showcase
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.test.check.clojure-test :refer [defspec]] 
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [concrete-optics.algebra.equality :refer [typed-eq]]
            [concrete-optics.core :as opt]
            [concrete-optics.algebra.structures :as alg]
            [concrete-optics.iso.axioms :refer [view-review-axiom review-view-axiom]]
            [concrete-optics.lens.axioms :refer [get-put-axiom put-get-axiom put-put-axiom]]))

;; This test file also serves as documentation.

;; Isomorphisms
;; ------------

;; Isomorphisms allow us to change the representation of data
;; without loosing infromation. Here is a simple example.

(def celcius<->fahrenheit 
  (let [celsius->fahrenheit (fn [c] {:fahrenheit (+ 32 (* (:celsius c) (/ 9 5)))})
        fahrenheit->celsius (fn [f] {:celsius (/ (- (:fahrenheit f) 32) (/ 9 5))})]
    (opt/mk-iso celsius->fahrenheit fahrenheit->celsius)))

;; It is a good idea to check that a custom optic constructed by one of the
;; `mk-<optic>` functions is lawful. The axiom modules provide 
;; the necessary functions. See also the test modules for more
;; examples. One subtle issue is choosing the right notion of 
;; equality. See the tests for the `curried` iso for an interesting case.

(defspec celcius<->fahrenheit-view-review-test 100
  (prop/for-all [x (gen/fmap (fn [t] {:celsius t}) gen/ratio)]
                (view-review-axiom celcius<->fahrenheit x)))

(defspec celcius<->fahrenheit-review-view-test 100
  (prop/for-all [x (gen/fmap (fn [t] {:fahrenheit t}) gen/ratio)]
                (review-view-axiom celcius<->fahrenheit x)))

;; Consider this function defined with Celsius in mind.

(defn celsius-freezing?
  [c]
  (< (:celsius c) 0))

;; By using `celcius<->fahrenheit` we can use this function
;; on Fharenheit values.

(deftest celsius-freezing-test
  (testing "celsius freezing"
    (is (celsius-freezing? {:celsius -10})))
  (testing "celsius not freezing"
    (is (not (celsius-freezing? {:celsius 23})))) 
  (testing "fahreheit freezing"
      (is (celsius-freezing? (opt/review celcius<->fahrenheit {:fahrenheit 30 }))))
  (testing "fahrenheit not freezing"
      (is (not (celsius-freezing? (opt/review celcius<->fahrenheit {:fahrenheit 35}))))))

;; At this point it is not very exciting because what we did
;; was just wrapping and unwrapping the function which converts
;; Fahrenheit to Celsius. Here is a more interesting example.

(defn increase-fahrenheit
  [diff]
  (fn [f] {:fahrenheit (+ (:fahrenheit f) diff)}))

(deftest increase-fahrenheit-test
  (testing "increase fahrenheit"
    (is (= ((increase-fahrenheit 1) {:fahrenheit 1}) 
           {:fahrenheit 2})))
  (testing "increase celsius"
    (is (= (opt/over celcius<->fahrenheit (increase-fahrenheit (/ 9 5)) {:celsius 1})
           {:celsius 2}))))

;; Note that we manipulated a Celsius value as if it was 
;; in Fahrenheit. We can also do it in the other direction
;; by inverting the isomorphism.

(defn increase-celsius
  [diff]
  (fn [f] {:celsius (+ (:celsius f) diff)}))

(def fahrenheit<->celsius
  (opt/invert-iso celcius<->fahrenheit))

(deftest increase-celsius-test
  (testing "increase celsius"
    (is (= ((increase-celsius 1) {:celsius 1})
           {:celsius 2})))
  (testing "increase fahrenheit"
    (is (= (opt/over fahrenheit<->celsius (increase-celsius (/ 5 9)) {:fahrenheit 1})
           {:fahrenheit 2}))))

;; It is also possible to work with more than two representations.
;; As an example, consider Kelvin.

(def celsisus<->kelvin
  (let [celsius->kelvin (fn [c] {:kelvin (+ (:celsius c) 273)})
        kelvin->celsius (fn [k] {:celsius (- (:kelvin k) 273)})]
    (opt/mk-iso celsius->kelvin kelvin->celsius)))

(defspec celcius<->kelvin-view-review-test 100
  (prop/for-all [x (gen/fmap (fn [t] {:celsius t}) gen/ratio)]
                (view-review-axiom celsisus<->kelvin x)))

(defspec celcius<->kelvin-review-view-test 100
  (prop/for-all [x (gen/fmap (fn [t] {:kelvin t}) gen/ratio)]
                (review-view-axiom celsisus<->kelvin x)))

(def kelvin<->fahrenheit
  (opt/compose (opt/invert-iso celsisus<->kelvin) celcius<->fahrenheit ))

(deftest mixed-iso-test
  (testing "inversion and composition"
    (is (= (opt/over kelvin<->fahrenheit (increase-fahrenheit (/ 9 5)) {:kelvin 1})
           {:kelvin 2}))))

;; Lenses
;; ------

;; Lenses are a generalization of fieled accessors/modifiers. Consider
;; the following example.

(def location 
  {:latitude 51.340199
   :longitude 12.360103})

(def weather-data
  {:temperature {:celsius 0}
   :date "2017-06-09"
   :location location})

(def weater-latitude-lens 
  (opt/field [:location :latitude]))

(deftest lens-compariosn
  (testing "lenses imitate field accessors"
    (is (= (get-in weather-data [:location :latitude])
           (opt/view weater-latitude-lens weather-data))))
  (testing "lenses imitate field modifiers"
    (is (= (assoc-in weather-data [:location :latitude] 0.0)
           (opt/put weater-latitude-lens 0.0 weather-data)))))

;; As one can see, lenses do not buy us anything in this case. However,
;; we can build 'virtual fields' as lenses by composing them with
;; isos. This is a general phenomenon: optics in isolation are often
;; mundane, they really shine when used with other optics.

(def weather-fahrenheit-lens
  (opt/compose (opt/field [:temperature]) celcius<->fahrenheit))

(deftest virtual-fahrenheit-test 
  (testing "get value from virtual field" 
    (is (= (opt/view weather-fahrenheit-lens weather-data) 
           {:fahrenheit 32}))) 
  (testing "manipulate value in virtual field" 
    (is (= (opt/over weather-fahrenheit-lens (increase-fahrenheit (/ 9 5)) weather-data) 
           {:temperature {:celsius 1} :date "2017-06-09" :location location})))
  (testing "put new value in the virtual field"
    (is (= (opt/put weather-fahrenheit-lens {:fahrenheit 212} weather-data)
           {:temperature {:celsius 100} :date "2017-06-09" :location location}))))

;; There are other ways of creating virtual fields. Consider the
;; following piece of data 

(def net-tare-weight
  {:net 100 :tare 15})

(def net-tare<->gross-tare
  (let [net-tare->gross-tare (fn [w] {:gross (+ (:net w) (:tare w)) :tare (:tare w)})
        gross-tare->net-tare (fn [w] {:net (- (:gross w) (:tare w)) :tare (:tare w)})]
    (opt/mk-iso net-tare->gross-tare gross-tare->net-tare)))

;; Now we can manipulate the virtual field `gross` by composing
;; `net-tare<->gross-tare` by the `gross-field` lens.

(def virtual-gross-field
  (opt/compose net-tare<->gross-tare (opt/field [:gross])))

(deftest virtual-gross-field-test
  (testing "get value from virtual field"
    (is (= (opt/view virtual-gross-field net-tare-weight)
            115))) 
  (testing "manipulate value in virtual field" 
    (is (= (opt/over virtual-gross-field #(+ % 10) net-tare-weight)
            {:net 110 :tare 15})))
  (testing "put new value in the virtual field"
    (is (= (opt/put virtual-gross-field 80 net-tare-weight)
           {:net 65 :tare 15})))) 

;; One can also inline the definitions and create the virtual
;; gross field by inlining the action of the isomorphism.

(def handmade-virtual-gross-field
  (opt/mk-lens (fn [w] (+ (:net w) (:tare w)))
               (fn [new-gross w] {:net (- new-gross (:tare w)) :tare (:tare w)})))

;; Again it is good practise to test `field-put-put-axiom` as
;; it is manually defined.
(def gen-weight 
  (gen/let [tare gen/small-integer
            net gen/small-integer]
    {:net net :tare tare}))

(defspec virtual-field-get-put-test 100
  (prop/for-all [weight gen-weight
                 gross gen/small-integer]
                (get-put-axiom handmade-virtual-gross-field
                               weight
                               gross)))

(defspec virtual-field-put-get-test 100
  (prop/for-all [weight gen-weight]
                (put-get-axiom handmade-virtual-gross-field 
                               weight)))

(defspec virtual-field-put-put-axiom 100
  (prop/for-all [weight gen-weight
                 gross_1 gen/small-integer
                 gross_2 gen/small-integer]
                (put-put-axiom handmade-virtual-gross-field
                               weight
                               gross_1
                               gross_2)))

;; We can also check that handmade version has the same behaviour as 
;; the one we ontain by composition.
(deftest virtual-gross-field-comparison-test
  (testing "get value from virtual field"
    (is (= (opt/view virtual-gross-field net-tare-weight)
           (opt/view handmade-virtual-gross-field net-tare-weight))))
  (testing "manipulate value in virtual field"
    (is (= (opt/over virtual-gross-field #(+ % 10) net-tare-weight)
           (opt/over handmade-virtual-gross-field #(+ % 10) net-tare-weight))))
  (testing "put new value in the virtual field"
    (is (= (opt/put virtual-gross-field 80 net-tare-weight)
           (opt/put handmade-virtual-gross-field 80 net-tare-weight)))))

;; Prisms
;; ------

;; Prisms are a way to implement a form pattern matching in which
;; you commit to one branch. The `cons-prism` prism is a textbook 
;; example.

;; TODO

;; One can even use a predicate as a pattern.

;; TODO

;; Composition of predicate prisms correspond to conjunction.

;; TODO 

;; On its own this again looks somewhat boring. For more
;; interesting examples look at the `each-positive-a` tarversal
;; from the next section.

;; Traversals
;; ----------

;; The combinator `traverse` generalizes `reduce` with an arbitrary
;; monoid using the `const-applicative`. For instance here is a
;; very indirect implementation of `count` for vectors.

(defn vector-length
  [vec]
  (opt/traverse opt/vector-traversal 
                (alg/const-applicative alg/additive-monoid) 
                (constantly 1) 
                vec))

(deftest vector-length-test
  (testing "vector-length indeed computes the length"
    (is (= (vector-length [1 2 3 4 5]) 5)))
  (testing "testing for empty vector"
    (is (= (vector-length []) 0))))

;; In combination with other optics, traverse can do more interesting things
;; like accessing or modifying groups of nested data.

(def nested-data
  [{:a 1 :b 2} {:c 3} {:a -5} {:a 7 :z 22}])

(def each-positive-a
  (opt/compose opt/vector-traversal 
                     (opt/ix :a) 
                     (opt/predicate-prism #(> % 0))))

(deftest list-positive-as-test
  (testing "listing elements with a filtering condition"
    (is (typed-eq (opt/to-list each-positive-a nested-data)
           [1 7]))))

(deftest modify-positive-as-test
  (testing "modifying only the values fitting a filtering condition"
    (is (typed-eq (opt/over each-positive-a inc nested-data)
           [{:a 2, :b 2} {:c 3} {:a -5} {:a 8, :z 22}]))))

;; There are a lot of useful applicative structures that can be used with
;; `traverse`. Here are a few examples for validating data.

(def some-numbers
  [1 -4 6 7 -9 7 -3])

(def some-nonnegative-numbers
  [1 16 25 9 1 4])

(defn fancy-sqrt
  [x]
  (if (< x 0) 
    (alg/fail-with (str "Cannot take the square root of " x))
    (Math/sqrt x)))

(defn fail-fast-validation
  [numbers]
  (opt/traverse opt/vector-traversal 
                alg/fail-fast-applicative 
                fancy-sqrt 
                numbers))

(deftest fail-fast-validation-test
  (testing "if there are errors, only the first one is kept"
    (is (typed-eq (fail-fast-validation some-numbers) 
           {:failure "Cannot take the square root of -4"})))
  (testing "if there are no errors the result is returned"
    (is (typed-eq (fail-fast-validation some-nonnegative-numbers) 
           [1.0 4.0 5.0 3.0 1.0 2.0]))))

(defn collect-errors-validation
  [numbers]
  (opt/traverse opt/vector-traversal 
                alg/collect-errors-applicative 
                (alg/map-failure (fn [x] [x]) fancy-sqrt)
                numbers))

(deftest collect-errors-validation-test
  (testing "if there are errors, all of them are returned in a vector"
    (is (typed-eq (collect-errors-validation some-numbers)
           {:failure ["Cannot take the square root of -4"
                      "Cannot take the square root of -9"
                      "Cannot take the square root of -3"]})))
  (testing "if there are no errors the result is returned"
    (is (typed-eq (collect-errors-validation some-nonnegative-numbers)
           [1.0 4.0 5.0 3.0 1.0 2.0]))))

;; We can even cook up an applicative which, say, counts  errors or determines
;; the maximum severity of errors. Here is an implementation for error counting.
;; We leave the severity example as an exercise.
(defn count-errors-validation
  [numbers]
  (opt/traverse opt/vector-traversal 
                (alg/validation-applicative alg/additive-monoid)
                (alg/map-failure (constantly 1) fancy-sqrt)
                numbers))

(deftest count-errors-validation-test
  (testing "if there are errors, the error count is returned"
    (is (typed-eq (count-errors-validation some-numbers)
           {:failure 3})))
  (testing "if there are no errors the result is returned"
    (is (typed-eq (count-errors-validation some-nonnegative-numbers)
           [1.0 4.0 5.0 3.0 1.0 2.0]))))


