(ns defi-clj.wallet
  (:require [defi-clj.bip39 :as bip39]
            [defi-clj.bip39.english :as english]))


(def wordlist-map
  {:english english/wordlist})



(defn generate-hd-root-key
  "Genereates Hierarchical Deterministic root key.
   - strength (default 128) - the strength of the mnemonic. Must be greater than or equal to 128, less or equal to 256 and a multiple of 8
   - language - can be anyone of the following keywords [:english]"
  [strength language]
  (let [{:keys [entropy mnemonic]} (bip39/generate-mnemonic (wordlist-map language) strength)]

    ))
