(ns defi-clj.utils.types
  (:import [java.math BigInteger]))


(defn big-integer?
  [o]
  (= (type o) java.math.BigInteger))
