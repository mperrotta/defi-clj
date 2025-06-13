(ns defi-clj.bip39
  (:require [defi-clj.utils.random :as random]
            [defi-clj.crypto.hashing :as hashing]
            [defi-clj.crypto.pbkdf :as pbkdf]
            [defi-clj.utils.bits :as bits]
            [defi-clj.bip39.english :as english]
            [clojure.string :as str])
  (:import [com.google.common.primitives Bytes]
           [java.text Normalizer Normalizer$Form]))

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

(defn make-bip39-salt
  [salt]
  (Normalizer/normalize (str "mnemonic" salt) Normalizer$Form/NFKD))

(defn mnemonic-to-seed
  [mnemonic pass-phrase]
  (let [normalized-mnemonic (Normalizer/normalize mnemonic Normalizer$Form/NFKD)
        bip39ified-salt (make-bip39-salt pass-phrase)]
    (pbkdf/pbkdf2 normalized-mnemonic bip39ified-salt)))

(comment


  (generate-mnemonic {:wordlist english/wordlist
                      :strength 128
                      :random-gen-fn (fn [_] (byte-array [0x06 0x36 0x79 0xca 0x1b 0x28 0xb5 0xcf 0xda 0x9c 0x18 0x6b 0x36 0x7e 0x27 0x1e]))})


  (do
    (def m "melody task hungry cancel column broom equip error spy toss pioneer swing trip cruel stereo over eye trend fox economy aim near genuine real")
    (def seed (mnemonic-to-seed m ""))

    (assert (= seed [
                     153, 230, 119,  62, 159, 241,  59,  18,  76,  39, 128,
                     141, 219,  20, 137,   6, 208, 162,  49, 144, 216,  49,
                     206, 240, 225,  33,  34,  52, 166,  43, 218,  50, 238,
                     85, 165,  95, 185,  79, 197, 117,  69,  18,  48, 186,
                     165, 189,  41, 213, 225,  25,   5, 187,   8, 159, 175,
                     135, 156, 229, 132, 123,  73,   3,  24, 250
                     ]))
    )
  )
