(ns defi-clj.utils.math
  (:import [java.util BitSet]))


(def byte-array-type (Class/forName "[B"))


;;TODO: make this support returning a transducer
(defn group-byte-array-by-bits
  [bytes group-bit-size]

  (let [total-bits (* (count bytes) 8)
        bytes (if (not= (type bytes) byte-array-type) (byte-array bytes) bytes)]
    (loop [bit-idx            0
           result             []]
      (let [bits-left (- total-bits bit-idx 1)]
        (if (and (< bit-idx (- total-bits 1))
                (> bits-left group-bit-size))
          (let [new-bit-count (min group-bit-size bits-left)
                new-number (reduce
                            (fn [acc idx]
                              (let [byte-idx (quot idx 8)
                                    current-byte (nth bytes byte-idx)
                                    byte-bit-idx (- 7 (mod idx 8))
                                    new-number-bit-idx (mod idx group-bit-size)
                                    current-bit-set (> (bit-and current-byte (bit-shift-left 1 byte-bit-idx)) 0)
                                    shift-amount (- group-bit-size new-number-bit-idx 1)]

                                #_(println idx byte-bit-idx new-number-bit-idx byte-idx current-byte current-bit-set shift-amount acc)
                                (+ acc (if current-bit-set
                                         (bit-shift-left 1 shift-amount)
                                         0))))
                            0
                            (range bit-idx (+ bit-idx new-bit-count)))]

            (recur (+ bit-idx new-bit-count) (conj result new-number)))
          result)))))



(comment

  (time (group-byte-array-by-bits [0x06 0x36 0x79 0xca 0x1b 0x28 0xb5 0xcf 0xda 0x9c 0x18 0x6b 0x36 0x7e 0x27 0x1e] 11))

  )
