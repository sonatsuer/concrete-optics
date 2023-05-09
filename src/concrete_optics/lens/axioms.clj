(ns concrete-optics.lens.axioms)

(defn mk-lens
  "Constructs a lens from a getter and a putter (or setter)"
  [get put]
  {:view get
   :over (fn [a-to-b] (fn [s] (put (a-to-b (get s)) s)))
   :to-list (fn [s] [get s])
   :traverse (fn [app_f a-to-fb] (fn [s] ((:binary-lift app_f) put (a-to-fb (get s)) ((:unit app_f) s))))})

(defn field
  "This captures the get and set properties provided by the language as a lens.
   Assumes that the fields already exist."
  [ks]
  (mk-lens (fn [m] (get-in m ks)) (fn [b m] (assoc-in m ks b))))
