(ns defi-clj.bip39
  (:require [defi-clj.utils.random :as random]))


(defn generate-mnemonic
  "Takes a map with the following key:
      :word-list (required) - the list of words from which to create the mnemonic. Language wordlists can be found in the defi-clj.bip39 namespace
      :strength (default 128) - the strength of the mnemonic. Must be greater than or equal to 128 and a multiple of 8"
  [{:keys [word-list strength] }]

  (let [random-bytes (random/generate-random-bytes (/ strength 8))]
    ))
