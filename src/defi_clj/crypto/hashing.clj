(ns defi-clj.crypto.hashing

  (:import [java.security MessageDigest]
           [javax.crypto Mac]
           [javax.crypto.spec SecretKeySpec]))


(def get-sha256-instance (memoize (fn []
                                    (MessageDigest/getInstance "SHA-256"))))

(def get-hmac-sha512-instance (memoize (fn []
                                         (Mac/getInstance "HmacSHA512"))))

(def hmac-algo-map
  {:sha-512 (get-hmac-sha512-instance)})

(defn sha256
  [bytes]
  (.digest (get-sha256-instance) bytes))

(defn first-sha256-checksum-byte
  "Returns the first nbits of the hashed bytes. Returns as a single byte with rest of bits zerod out."
  [bytes nbits]
  (when (> nbits 8) (throw (Exception. "nbits must be 8 or less")))
  (let [sha (sha256 bytes)
        the-byte (first sha)]
    (-> the-byte
        (bit-shift-right (- 8 nbits))
        (bit-shift-left (- 8 nbits)))))


(defn get-hmac
  "hash-algo can be anyone of the following keywords [:sha-512]"
  [hash-algo seed data]
  (let [hmac (if-let [instance (hmac-algo-map hash-algo)] instance (throw (Exception. (str "hash-algo " hash-algo " not found"))))
        key (SecretKeySpec. seed (.getAlgorithm hmac))]
    (.init hmac key)
    (.doFinal hmac data))

  )


(comment

  (get-hmac :sha-512 (byte-array [1 2 3]) (byte-array [6]))

  )
