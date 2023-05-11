(ns concrete-optics.test-helpers)

(defn- typed-eq-binary
  "Binary typed equality, overrides truthiness with actual
   boolean values."
  [x y]
  (if (and (= (type x) (type y)) (= x y)) true false))

(defn- all-true
  [& args]
  (every? #(= true %) args))

(defn typed-eq
  "In normal equality you basically check 'content' equality. 
   So for instance [1 2 3] is equal to '(1 2 3). Typed eqaulity
   checks content _and_ type, so it is stricter. This is meant to
   be used in tests."
  ([] true)
  ([_single-value] true)
  ([value & rest] (apply all-true (map typed-eq-binary rest (concat (list value) rest)))))

(defn compare-functions
  "Checks semantic equality of functions. The usual equality for functions
   is hash based. It cannot check the equality of, say, #(+ % %) and
   #(* 2 %). The general problem is of course undecidable so we only
   test for given sample values. Meant to be used in tests."
  ([base-comparison initia-samples & remaining-samples]
   (fn [f g]
     (let [samples (concat (list initia-samples) remaining-samples)]
       (apply all-true (map base-comparison 
                            (apply map f samples) 
                            (apply map g samples)))))))
