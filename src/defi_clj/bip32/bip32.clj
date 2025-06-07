(ns defi-clj.bip32
  (:require [defi-clj.crypto.hashing :as hashing])
  (:import [java.nio.charset Charset]
           [javax.crypto Mac]
           [java.util Arrays]))

(def btc-versions {:private 0x0488ade4, :public 0x0488b21e })

(def btc-seed (.getBytes "Bitcoin seed" (Charset/forName "UTF-8")))



(defn gen-hd-root-key
  [seed-bytes]
  (let [hmac (hashing/get-hmac :sha-512 btc-seed seed-bytes)]
    {:versions btc-versions
     :chain-code (Arrays/copyOfRange hmac 32 (alength hmac))
     :private-key (Arrays/copyOfRange hmac 0 32)
     :public-key nil ;;TODO
     }) )


(defn gen-child-from-root
  [{:keys [public-key private-key chain-code] :as root-key-map} account-index]

  (let [new-data (conj (vec public-key) account-index)
        hmac (hashing/get-hmac :sha-512 chain-code new-data)]))

(comment

  (gen-hd-root-key (byte-array [1 2 3 4 ]))

  )
