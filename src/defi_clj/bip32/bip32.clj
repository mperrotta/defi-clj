(ns defi-clj.bip32
  (:require [defi-clj.crypto.hashing :as hashing]
            [defi-clj.crypto.ecc-secp256k1 :as ecc])
  (:import [java.nio.charset Charset]
           [javax.crypto Mac]
           [java.util Arrays]))

(def btc-versions {:private 0x0488ade4, :public 0x0488b21e })

(def btc-seed (.getBytes "Bitcoin seed" (Charset/forName "UTF-8")))



(defn gen-hd-root-key
  [seed-bytes]
  (let [hmac (hashing/get-hmac :sha-512 btc-seed seed-bytes)
        private-key (Arrays/copyOfRange hmac 0 32)]
    {:versions btc-versions
     :chain-code (Arrays/copyOfRange hmac 32 (alength hmac))
     :private-key private-key
     :public-key (ecc/get-public-key private-key)
     }) )

;;TODO: support hardened derivation
(defn gen-child-from-root
  [{:keys [public-key private-key chain-code] :as root-key-map} account-index]

  (let [new-data (conj (vec public-key) account-index)
        hmac (hashing/get-hmac :sha-512 chain-code new-data)]))

(comment

  (def mnemonic "melody task hungry cancel column broom equip error spy toss pioneer swing trip cruel stereo over eye trend fox economy aim near genuine real")
  (def entropy  [
                 138, 155, 193, 189,  16, 146, 220,  57,
                 147,  10, 102, 211,  92, 182, 148, 238,
                 14, 140, 105,  53, 100, 239,  81,  61,
                 1, 113,  35,  16,  85,  39,  24,  85
                 ])

  (def expected-chain-code  [
                             184,  36, 154,  11, 143, 222, 118, 234,
                             62, 140, 165,  33, 222,  36, 131,  10,
                             48, 104,  60,   7, 117,  70,  13, 169,
                             113, 171, 171, 219,  68,  44,   3, 116
                             ])

  (def expected-priv-key 7577021973915723553539144247751242766384926117266777020138029630561209515855)
  (def expected-priv-key-bytes [
                                16, 192, 113,  69,  24, 109, 105,  72,
                                61, 102,  15, 136,   3, 209,  22, 135,
                                67,  96, 202, 220, 147, 228, 106, 111,
                                37, 161, 238, 230,  60, 203, 127,  79
                                ])

  (def expected-pub-key-bytes [
                            2, 217, 206, 190,  23, 187,  56, 130,
                            145, 202, 142, 220, 248, 214, 171, 242,
                            32,  22, 187,  57, 161, 233, 200, 131,
                            37, 163, 156, 113,  69, 215,  51,   2,
                            133
                            ])

  (def root (gen-hd-root-key (byte-array )))

  )
