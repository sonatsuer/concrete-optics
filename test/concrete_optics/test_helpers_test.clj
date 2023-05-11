(ns concrete-optics.test-helpers-test
  (:require [clojure.test :refer [deftest testing is]] 
            [concrete-optics.test-helpers :refer :all]))

(deftest typed-eq-positive-test
  (testing "typed-eq empty"
    (is (typed-eq [])))
  (testing "typed-eq single"
    (is (typed-eq 3)))
  (testing "typed-eq many"
    (is (typed-eq 3 3 3 3 3 3 3))))

(deftest typed-eq-negative-test
  (testing "float versus integer"
    (is (not (typed-eq 1 1 1.0 1))))
  (testing "list versus vector"
    (is (not (typed-eq [1 2] [1 2] '(1 2) [1 2])))))

(deftest compare-functions-positive-test 
  (testing "#(+ % %) vs (* 2 %)"
    (is ((compare-functions typed-eq [1 2 3 4 5 6 7 8 9 10]) #(+ % %) #(* 2 %))))
  (testing "(+ % %) vs (* 2.0 %)"
    (is ((compare-functions == [1 2 3 4 5 6 7 8 9 10]) #(+ % %) #(* 2.0 %)))))

(defn- dirac 
  [x]
  (fn [y] (if (= x y) 1 0)))

(deftest compare-functions-negative-test
  (testing "(dirac 3) vs (dirac 5)"
    (is (not ((compare-functions = [1 2 3 4 5 6 7 8 9 10]) (dirac 3) (dirac 5)))))
  (testing "(+ % %) vs (* 2.0 %)"
    (is (not ((compare-functions = [1 2 3 4 5 6 7 8 9 10]) #(+ % %) #(* 2.0 %))))))
