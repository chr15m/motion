(ns motion.utils
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [cljs.core.async :refer [<! close! timeout chan] :as async])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; -------------------------
;; Functions

(def m js/Math)
(def tau (* m.PI 2))

(defn now []
  (.getTime (js/Date.)))

(defn pol2crt [cx cy r a]
  [(+ cx (* r (m.cos a)))
   (+ cy (* r (m.sin a)))])

(defn hexagon [r pointy?]
  (let [seg (/ m.PI 3)]
    (map
      #(let [a (+ (* % seg) (if pointy? 0 (/ seg 2)))]
         (str
           (m.round (* r (m.sin a)))
           " "
           (m.round (* r (m.cos a)))))
      (range 6))))

(defn hex-pos [size c r]
  [(* size 2 (/ (float 3) 4) c)
   (+ (* (mod c 2) (/ (m.sqrt 3) 2) size) (* r size (/ (m.sqrt 3) 2) 2))])

(defn svg-arc [cx cy r as ae]
  (let [[xs ys] (pol2crt cx cy r (+ (mod ae tau) tau))
        [xe ye] (pol2crt cx cy r (+ (mod as tau) tau))
        direction (if (<= (- ae as) m.PI) 0 1)
        path ["M" (m.round xs) (m.round ys)
              "A" r r 0 direction 0 (m.round xe) (m.round ye)]]
    (clojure.string/join " " path)))

(defn svg-path [points & [closed?]]
  (clojure.string/join " "
    (apply concat
           ["M"]
           (concat
             (interpose '("L") (partition 2 points))
             (if closed? "Z" nil)))))

(defn segment-length [[x1 y1 x2 y2]]
  (m.sqrt (+ (m.pow (- x2 x1) 2) (m.pow (- y2 y1) 2))))

(defn partial-path [amount points]
  (cond (< (count points) 4) points
        (= amount 1) points
        (= amount 0) (subvec points 0 2)
        :else (let [segment-lengths (map (fn [segment] [(segment-length segment) segment]) (partition 4 2 points))
                    total-length (reduce + (map first segment-lengths))
                    sub-length (* amount total-length)]
                (loop [found (subvec points 0 2) remaining segment-lengths length 0]
                  (if (count remaining)
                    (let [next-segment (first remaining)
                          segment-length (first next-segment)
                          segment (vec (last next-segment))
                          next-length (+ length segment-length)]
                      (if (<= next-length sub-length)
                        (recur
                          (concat found (subvec segment 2))
                          (rest remaining)
                          next-length)
                        (let [amount-through-segment (/ (- sub-length length) (- next-length length))
                              [x1 y1 x2 y2] segment]
                          (concat found [(+ x1 (* (- x2 x1) amount-through-segment))
                                         (+ y1 (* (- y2 y1) amount-through-segment))]))))
                    found)))))

(defn timeline [duration & {:keys [atoms interval] :or {atoms {:time (atom 0) :go (atom true)} interval 16}}]
  (let [c (chan)]
    (reset! (atoms :time) 0)
    (reset! (atoms :go) true)
    (go-loop [l (now)]
             (<! (timeout interval))
             (let [old-time (deref (atoms :time))
                   l-next (now)]
               (if (deref (atoms :go))
                 (if (< old-time duration)
                   (do
                     (swap! (atoms :time) + (- l-next l))
                     (recur l-next))
                   (do
                     (reset! (atoms :time) duration)
                     (reset! (atoms :go) false)
                     (close! c))))))
    [(atoms :time) (atoms :go) c]))

(defn g-trans [x y]
  {:transform (str "translate(" x "," y ")")})

(defn make-link [l]
  (str (get (string/split js/document.location.href "/v") 0) l))

