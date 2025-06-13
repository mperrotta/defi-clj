(ns defi-clj.crypto.pbkdf
  (:require [defi-clj.crypto.hashing :as hashing])
  (:import [javax.crypto SecretKeyFactory]
           [javax.crypto.spec PBEKeySpec]))


(def pbkdf2-iteration-count 2048)
(def pbkdf2-key-length 64)

(def get-pbkdf2-key-factory (memoize (fn [] (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA512"))) )


(defn pbkdf2
  [mnemonic pass-phrase]
  (let [spec (PBEKeySpec. (char-array mnemonic) (.getBytes pass-phrase) pbkdf2-iteration-count pbkdf2-key-length)]
    (-> (get-pbkdf2-key-factory)
        (.generateSecret spec)
        (.getEncoded))))
