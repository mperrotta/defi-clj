(ns defi-clj.crypto.ecc
  (:require [clojure.math :as math])
  (:import [java.security KeyFactory]))

(def B256 (math/pow 2 256))
(def P (- B256  0x1000003d1))
(def N (- B256 0x14551231950b75fc4402da1732fc9bebf))
(def Gx 0x79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798)
(def Gy 0x483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8)

(def ecc-key-factory (memoize (fn [] (KeyFactory/getInstance "ECDSA" "BC"))))


(defn get-public-key
  [private-key]

  (let []))
