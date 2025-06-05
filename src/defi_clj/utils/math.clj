(ns defi-clj.utils.math
  (:import [java.util BitSet]))


(defn group-byte-array-by-bits
  [bytes group-bit-size]


  (let [total-bits (* (count bytes) 8)]
    (loop [bit-idx            0
           result             []]
      (let [bits-left (- total-bits bit-idx 1)]
        (if (< bit-idx (- total-bits 1))
          (let [new-bit-count (min group-bit-size bits-left)
                new-number (reduce
                            (fn [acc idx]
                              (let [byte-idx (quot idx 8)
                                    current-byte (bytes byte-idx)
                                    byte-bit-idx (- 7 (mod idx 8))
                                    new-number-bit-idx (mod idx 11)
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
