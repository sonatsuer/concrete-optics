(ns concrete-optics.lens.structures)

(defn mk-lens
  "get : s -> a, oset : b -> s -> t"
  [get oset]
  {:view get
   :over (fn [a-to-b] (fn [s] (oset (a-to-b (get s)) s)))
   :to-list (fn [s] [get s])
   :traverse (fn [app_f a-to-fb] 
               (fn [s] ((:binary-lift app_f) oset (a-to-fb (get s)) ((:unit app_f) s))))})

(defn field
  "This captures the get and set properties provided by the language as a lens.
   Assumes that the fields already exist."
  [ks]
  (mk-lens (fn [m] (get-in m ks)) 
           (fn [b m] (assoc-in m ks b))))
