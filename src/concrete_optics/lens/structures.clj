(ns concrete-optics.lens.structures
  "Lens constructors and examples.")

(defn mk-lens
  "Constructs a lens from a getter and putter."
  [get put]
  {:view get
   :over (fn [a-to-b] (fn [s] (put (a-to-b (get s)) s)))
   :to-list (fn [s] [get s])
   :traverse (fn [app-f a-to-fb]
               (fn [s] ((:binary-lift app-f) put (a-to-fb (get s)) ((:pure app-f) s))))})

(defn field
  "This captures the get and set properties provided by the language as a lens.
   Assumes that the fields already exist. The input is a vector of keys."
  [ks]
  (mk-lens (fn [m] (get-in m ks))
           (fn [b m] (assoc-in m ks b))))
