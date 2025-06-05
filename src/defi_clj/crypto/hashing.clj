(ns defi-clj.crypto.hashing
  (:import [java.security MessageDigest]))


(def get-sha256-instance (memoize (fn []
                                    (MessageDigest/getInstance "SHA-256"))))

(defn sha256
  [bytes]
  (.digest (get-sha256-instance) bytes))

(defn first-sha256-checksum-byte
  [bytes nbits]
  (when (> nbits 8) (throw (Exception. "nbits must be 8 or less")))
  (let [sha (sha256 bytes)
        the-byte (first sha)]
    (-> the-byte
        (bit-shift-right (- 8 nbits))
        (bit-shift-left (- 8 nbits)))))
