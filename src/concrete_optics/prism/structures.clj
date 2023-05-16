(ns concrete-optics.prism.structures
  "Prism constructors and examples.")

(defn no-match?
  "Checks if a value is equal to `:no-match`, a value used
   in prisms when the pattern encoded by the prism does not
   match the given value."
  [x]
  (= x :no-match))

(defn no-match-alternative?
  "Checks if a value is equal to a map with `:no-match-alternative`
   as it is unique key. Such maps are used when the pattern encoded
   by a prism is not does not match the given value but an alternative
   can be supplied."
  [x]
  (and (map? x) (= (keys x) '(:no-match-alternative))))

(defn mk-prism
  "Constructs a prism from a pattern matching function and a review.
   It is the programmers responsibilty to guarantee that a value not
   matching the pattern is not fed into `review`"
  [match review]
  {:to-list (fn [s]
              (let [matched (match s)]
                (if (no-match-alternative? matched) [] [matched])))
   :review review
   :over (fn [a-to-b]
           (fn [s]
             (let [matched (match s)]
               (if (no-match-alternative? matched) (:no-match-alternative matched) (a-to-b matched)))))
   :traverse (fn [app-f a-to-fb]
               (fn [s]
                 (let [matched (match s)]
                   (if (no-match-alternative? matched) ((:pure app-f) (:no-match-alternative matched)) ((:fmap app-f) review (a-to-fb matched))))))})

(defn mk-simple-prism
  "Constructs a prism from a preview-review pair."
  [preview review]
  (mk-prism (fn [s]
              (let [previewed (preview s)]
                (if (no-match? previewed) {:no-match-alternative s} previewed)))
            review))

(defn predicate-prism
  "Turns a predicate into prism. In the resulting prism, if the
   input given to the `review` does not satisfy the predicate 
   then an exception is thrown. The optional name parameter is used
   in the exception message." 
  [predicate & [name]]
  (let [msg (or name "")]
    (mk-simple-prism
     (fn [x] (if (predicate x) x :no-match))
     (fn [x] (if (predicate x) x (throw (Exception. (str x " vioates the predicate " msg))))))))

(def cons-prism
  "A prism of vectors which encodes the nonempty pattern. If a 
   vector is nonempty, the the review decomposes it as a map `{:head <head> :tail <tail>}`."
  (mk-simple-prism
   (fn [vec] (if (empty? vec) :no-match {:head (first vec) :tail (into [] (rest vec))}))
   (fn [decomposition] (into [] (concat [(:head decomposition)] (:tail decomposition))))))
