(ns concrete-optics.prism.structures)

(defn- nothing?
  [x]
  (= x :nothing))

(defn- left?
  [x]
  (and (map? x)(= (keys x) '(:left))))

(defn mk-prism
  "match : s -> Either t a, rev : b -> t"
  [match rev]
  {:to-list (fn [s]
              (let [matched (match s)]
                (if (left? matched) [] [matched])))
   :review rev
   :over (fn [a-to-b]
           (fn [s]
             (let [matched (match s)]
               (if (left? matched) (:left matched) (a-to-b matched)))))
   :traverse (fn [app_f a-to-fb]
               (fn [s]
                 (let [matched (match s)]
                   (if (left? matched) ((:unit app_f) (:left matched)) ((:fmap app_f) rev (a-to-fb matched))))))})

(defn mk-simple-prism
  "preview s -> Maybe a, rev : a -> s"
  [prev rev]
  (mk-prism (fn [s]
              (let [previewed (prev s)]
                (if (nothing? previewed) {:left s} previewed)))
            rev))

(defn
  predicate-prism
  [predicate & [name]]
  (let [msg (or name "")]
    (mk-simple-prism
     (fn [x] (if (predicate x) x :nothing))
     (fn [x] (if (predicate x) x (throw (Exception. (str x " vioates the predicate " msg))))))))
