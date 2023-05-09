(ns concrete-optics.setter.structures)

(defn mk-setter
  "over : (a -> b) -> (s -> t)"
  [over]
  {:over over})
