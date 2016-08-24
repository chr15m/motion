(ns motion.demo-orbital-transformer
  (:require [motion.utils :refer [timeline]]))

(def m js/Math)

(defn handle-orbiter-click [ev]
  (print "clicked!"))

(defn component-orbiter [style]
  (let [[tl] (timeline js/Infinity)
        distance 200]
    (fn []
      (let [t (/ @tl 3000)
            t2 (/ @tl 500)]
        [:g
         [:circle {:cx 0 :cy 0 :r distance}]
         [:g {:transform (str "translate(" (* (m.cos t) distance) "," (* (m.sin t) distance) ")")}
          [:circle {:cx 0 :cy 0 :r 30 :fill "#383838" :stroke-width "2px" :on-click (partial handle-orbiter-click)}]
          [:circle {:cx (+ (* (m.cos t2) 80)) :cy (+ (* (m.sin t2) 80)) :r 10 :fill "#383838"}]]]))))
