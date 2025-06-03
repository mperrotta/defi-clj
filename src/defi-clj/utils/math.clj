(ns defi-clj.utils.math)



(defn group-byte-array-by-bits
  [bytes group-bit-size]


  (loop [[current-byte & rst] bytes
         current-bits       0
         current-bit-count  0
         remaining-bits     0
         remaining-bit-count 0
         result             []]
    (if current-byte
      (let [needed-bits (- group-bit-size current-bit-count remaining-bit-count)
            new-bit-count (+ current-bit-count (min 8 needed-bits))
            current-bits (bit-or (bit-shift-right remaining-bits (- 8 remaining-bit-count)) (bit-shift-left current-bits remaining-bit-count))
            bits-to-shift (- 8 (min 8 needed-bits))
            new-bits (-> (bit-shift-right current-byte bits-to-shift)
                         (bit-or (bit-shift-left current-bits (- 8 bits-to-shift)) ))
            reamining-bit-count (- 8 bits-to-shift)
            remaining-bits (bit-shift-left current-byte (- 8 remaining-bit-count))
            new-result (if (< new-bit-count group-bit-size) result (conj result new-bits))]

        (println {:current-byte current-byte
                  :current-bit-count current-bit-count
                  :remaining-bits remaining-bits
                  :remaining-bit-count remaining-bit-count
                  :needed-bits needed-bits
                  :new-bit-count new-bit-count
                  :bits-to-shift bits-to-shift
                  :new-bits new-bits
                  :new-result new-result})
        (recur (vec rst) new-bits new-bit-count remaining-bits remaining-bit-count result))
      (if (> current-bit-count 0)
        (conj result current-bits)
        result))))
