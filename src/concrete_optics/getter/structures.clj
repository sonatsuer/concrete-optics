(ns concrete-optics.getter.structures)

(defn mk-getter
  "view : s -> a"
  [view]
  {:view view
   :to-list (fn [s] [(view s)])})
