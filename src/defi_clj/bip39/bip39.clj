(ns defi-clj.bip39
  (:require [defi-clj.utils.random :as random]
            [defi-clj.crypto.hashing :as hashing]
            [defi-clj.utils.bits :as bits]
            [defi-clj.bip39.english :as english]
            [clojure.string :as str])
  (:import [com.google.common.primitives Bytes]))


(defn generate-mnemonic
  "Takes a map with the following key:
      :wordlist (required) - the list of words from which to create the mnemonic. Language wordlists can be found in the defi-clj.bip39 namespace
      :strength (default 128) - the strength of the mnemonic. Must be greater than or equal to 128, less or equal to 256 and a multiple of 8
      :random-gen-fn (optional) - optional custom random byte generation fn. Takes one parameter: length of bytes"

  [{:keys [wordlist strength random-gen-fn]}]

  (when (or
         (not= 0 (mod strength 8))
         (> strength 256)
         (< strength 128))
    (throw (Exception. "strength must be greater than or equal to 128, less or equal to 256 and a multiple of 8")))
  (let [random-bytes (if random-gen-fn
                       (random-gen-fn (/ strength 8))
                       (random/generate-random-bytes (/ strength 8)))

        checksum (hashing/first-sha256-checksum-byte random-bytes (/ strength 32))
        entropy (byte-array (conj (vec random-bytes) checksum))
        bit-groups (bits/group-byte-array-by-bits entropy 11)]

    {:mnemonic (->> (map (fn [idx] (english/wordlist idx)) bit-groups)
                    (str/join " "))
     :entropy entropy }))


(comment


  (generate-mnemonic {:wordlist english/wordlist
                      :strength 128
                      :random-gen-fn (fn [_] (byte-array [0x06 0x36 0x79 0xca 0x1b 0x28 0xb5 0xcf 0xda 0x9c 0x18 0x6b 0x36 0x7e 0x27 0x1e]))})

  )
