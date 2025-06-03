(ns defi-clj.utils.random
  (:import [java.security SecureRandom]))


(defn generate-random-bytes
  [num-bytes]
  (let [random (SecureRandom.)
        buffer (byte-array num-bytes)]
    (.nextBytes random buffer)
    (vec buffer)))
