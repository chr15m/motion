(ns motion.demo-circles
  (:require [cljs.core.async :refer [<! close! timeout chan] :as async]
            [motion.utils :refer [timeline g-trans svg-arc]]
            [motion.demo-nibblets :refer [component-svg-x]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def m js/Math)

(defn component-svg-circle-test [style x y]
  (let [[tl] (timeline js/Infinity)]
    (fn []
      (let [t (/ @tl 500)]
        [:g (merge style (g-trans x y))
         [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (+ t 1)) 10))}]
         [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (+ t 1.5)) 10))}]
         [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (+ t 2)) 10))}]]))))

(defn component-svg-circle-test-2 [style x y]
  (let [t-scale 0.1
        [tl] (timeline js/Infinity)]
    (fn []
      (let [t (/ @tl 30)]
        [:g (merge style (g-trans x y))
         [:circle {:cx 0 :cy 0 :r 30 :stroke-width "2px"}]
         [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.tanh (* (m.sin (* (+ t 1) t-scale)) 2)) 10))}]
         [:circle {:cx 0 :cy 0 :r (+ 37 (* (m.tanh (* (m.sin (* (+ t 10) t-scale)) 2)) 7))}]]))))

(defn component-svg-circle-test-3 [style x y]
  (let [duration 2000
        [t g c] (timeline duration)]
    (go-loop [[t g c] [t g c]]
      (<! c)
      (recur (timeline 2000 :atoms {:time t :go g})))
    (fn []
      (if @g
        [:g (g-trans x y)
          (component-svg-x style 0 0)
          [:circle (merge style {:cx 0 :cy 0 :r (+ (/ @t 20) 15) :stroke-opacity (- 1 (/ @t duration)) :stroke-width "2px"})]]))))

(defn component-svg-arc-thing [style x y]
  (let [[t] (timeline js/Infinity)]
    (fn []
      (let [p (* m.PI 2 (/ (mod @t 2000) 2000))]
        [:g (merge style (g-trans x y))
         [:path {:d (svg-arc 0 0 51 (- p 1) p) :stroke-width "5px"}]
         [:circle {:cx 0 :cy 0 :r 57 :stroke-width "3px"}]     
         [:circle {:cx 0 :cy 0 :r 53 :stroke-width "1px"}]]))))
