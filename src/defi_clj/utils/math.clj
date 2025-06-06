(ns defi-clj.utils.math
  (:import [java.util BitSet]))


(defn mod-inverse
  [a modulo]

  (loop [y 1
         last-y 0
         a' (if (>= a 0) a (mod a modulo))
         m modulo]
    (let [q (quot m a')]
      (if (> a' 1)
        (recur (- last-y (* q y)) y (mod m a') a')
        (mod y modulo)))))




(comment

  (assert (= 7 (mod-inverse 4 9)) "")

  (assert (= 10 (mod-inverse 54 11)) "")

  )
