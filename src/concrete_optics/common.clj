(ns concrete-optics.common
  "Utility functions.")

(defn classify
  "Assumes that the input is a lawful optic and determines its kind
   looking at the capabilities it posseses."
  [optic]
  (case (set (keys optic))
    #{:to-list :over :traverse} :traverse
    #{:view :to-list :over :traverse} :lens
    #{:to-list :review :over :traverse} :prism
    #{:view :to-list :review :over :traverse} :iso
    :not-supported))

(def capability-set
  "Full set of available capabilities."
  #{:view :to-list :review :over :traverse})

(defn get-capability
  "Utility function to access a capabiity. If the capability
   is not present then the function throws an exception."
  [capability optic]
  (if (contains? capability-set capability)
    (let [candidate (capability optic)]
      (if (nil? candidate)
        (throw (Exception. (str "The optic " (classify optic) " does not support " capability)))
        candidate))
    (throw (Exception. (str "Unrecognized capability: " capability)))))
