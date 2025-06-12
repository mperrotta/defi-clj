;; secp256k1 ecc
(ns defi-clj.crypto.ecc-secp256k1
  (:require [clojure.math :as clj-math]
            [defi-clj.utils.math :as math]
            [defi-clj.utils.types :as types])
  (:import [java.security KeyFactory]
           [java.security.spec ECParameterSpec EllipticCurve ECPoint ECFieldFp]
           [java.util BitSet]))

;; if curve is defined as: y² = x³ + ax + b
(def A (.toBigInteger 0N))
(def B (.toBigInteger 7N))

(def B256 (-> (.toBigInteger 2N) (.pow  256)))
(def P (.toBigInteger (- B256  0x1000003d1))) ;; prime field
(def N (.toBigInteger (- B256 0x14551231950b75fc4402da1732fc9bebf))) ;; Number of points on the curve (order)
(def Gx 0x79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798) ;; generator point x
(def Gy 0x483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8) ;; generator point y


(def the-point (ECPoint. (.toBigInteger Gx), (.toBigInteger Gy)))
(def the-field (ECFieldFp. P))
(def the-curve (EllipticCurve. the-field A B))
(def the-spec  (ECParameterSpec. the-curve the-point N 1))
(def ecc-key-factory (memoize (fn [] (KeyFactory/getInstance "ECDSA" "BC"))))


;; TODO: move these fns into a generic ECC ns.

(defn double-point
  [point curve]
  (let [x (.getAffineX point)
        y (.getAffineY point)
        a (.getA curve)
        b (.getB curve)
        p (-> (.getField curve)
              (.getP))
        slope (-> (* (+ (* 3 (.pow x 2)) a)
                     (math/mod-inverse (* 2 y) p))
                  (.toBigInteger))
        new-x (mod (- (.pow slope 2)
                      (* 2 x)) p)
        new-y (mod (- (* slope (- x new-x))
                      y) p)]
    (ECPoint. (.toBigInteger new-x) (.toBigInteger new-y))))

(defn add-points
  [point1 point2 curve]
  (let [x1 (.getAffineX point1)
        y1 (.getAffineY point1)
        x2 (.getAffineX point2)
        y2 (.getAffineY point2)
        a (.getA curve)
        b (.getB curve)
        p (-> (.getField curve)
              (.getP))
        slope (-> (* (- y1 y2)
                     (math/mod-inverse (- x1 x2) p))
                  (mod p)
                  (.toBigInteger))
        new-x (mod (- (.pow slope 2) x1 x2)
                   p)
        new-y (mod (- (* slope (- x1 new-x)) y1)
                   p)]
    (ECPoint. (.toBigInteger new-x) (.toBigInteger new-y))))

(defn multiply-point
  [point curve k]
  (let [k (if (types/big-integer? k) k (.toBigInteger (bigint k)))
        bit-length (.bitLength k)]
    (reduce (fn [new-point bit-idx]
              (let [current-point (double-point new-point curve)]
                (if (.testBit k bit-idx)
                  (add-points current-point point curve)
                  current-point)))
            point
            (range (- bit-length 2) -1 -1 ))))

(defn get-public-key
  [private-key]
  (assert (and (>= private-key 1) (< private-key N)))
  (multiply-point the-point the-curve private-key))



(comment

  (let [new-point (double-point the-point the-curve)
        x (.getAffineX new-point)
        y (.getAffineY new-point)]

    (println x y)
    (assert (and (= x 89565891926547004231252920425935692360644145829622209833684329913297188986597)
                 (= y 12158399299693830322967808612713398636155367887041628176798871954788371653930))))




  (do
    (def p1 (ECPoint. (.toBigInteger 93185804057967261262261252731142102313315485822989382993838201868242406222660)
                      (.toBigInteger 67273255841045230296667953050406241729687737346615373368359663992705073917048)))

    (def p2 (ECPoint. (.toBigInteger 13015059673201947606427149534360196739225963354724490183361456291102944084913)
                      (.toBigInteger 78914235960856903279738472372436436584578029637044168398560955986871519829257)))

    (def p1p2 (add-points p1 p2 the-curve))

    (def x (.getAffineX p1p2))
    (def y (.getAffineY p1p2))

    (assert (= x 107066968394232503735640179170606216767730045795985306887645049885933479587320))
    (assert (= y 90410101247350170241855071336688411905427729950674477203369100042787043844754)))


  (do
    (def mpoint (multiply-point the-point the-curve 80311422861268649470766285442089814410896094309801588279145814515479634419270))

    (def x (.getAffineX mpoint))
    (def y (.getAffineY mpoint))

    (assert (= x 61840256099038076754287242193351274813909580701985848613511531832199855295241))
    (assert (= y 90189816713920330277450622528770787569194417131198530198732192995443182948254))
    )

  )
