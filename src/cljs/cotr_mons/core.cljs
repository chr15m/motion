(ns cotr-mons.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! timeout] :as async])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; -------------------------
;; Functions

(def m js/Math)

(defn pol2crt [cx cy r a]
  [(+ cx (* r (m.cos a)))
   (+ cy (* r (m.sin a)))])

(defn svg-arc [cx cy r as ae]
  (let [[xs ys] (pol2crt cx cy r ae)
        [xe ye] (pol2crt cx cy r as)
        direction (if (<= (- ae as) m.PI) 0 1)
        path ["M" xs ys
              "A" r r 0 direction 0 xe ye]]
    (clojure.string/join " " path)))

;; -------------------------
;; Components

(defn component-svg-path-1 []
  [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "100px" :left "100px" :position "absolute"}}
   [:path {:d (js/roundPathCorners "M 0 50 L 20 20 L 40 40 L 100 30 L 120 40 L 150 5 L 150 50 L 100 100 Z 0 50" 5 false) :fill "none" :stroke "#41A4E6" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-path-2 []
  [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "500px" :left "100px" :position "absolute"}}
   [:path {:d (js/roundPathCorners "M 0 185 L 48 185 L 98 135 L 150 135" 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
   [:path {:d (js/roundPathCorners "M 0 190 L 50 190 L 100 140 L 150 140" 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-circle-test [t]
  (let [t-scale 0.3]
    [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "300px" :left "400px" :position "absolute"}}
     [:circle {:cx 100 :cy 100 :r (+ 35 (* (m.sin (* (+ @t 100) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]    
     [:circle {:cx 100 :cy 100 :r (+ 40 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "4px"}]
     [:circle {:cx 100 :cy 100 :r (+ 45 (* (m.sin (* (+ @t 200) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))

(defn component-svg-arc [t]
  (let [p (* m.PI 2 (/ (mod @t 100) 100))]
  [:svg {:x 0 :y 0 :width 200 :height 200 :style {:left "600px" :top "200px" :position "absolute"}}
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "5" :d (svg-arc 100 100 50 (m.max 0 (- p 1)) p)}]
   [:circle {:cx 100 :cy 100 :r 53 :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))

(defn component-svg-x [x y]
  [:svg {:x 0 :y 0 :width 15 :height 15 :style {:left (str x "px") :top (str y "px") :position "absolute"}}
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 0 L 15 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 15 0 L 0 15 Z"}]])

(defn component-game [entities t]
  [:div
   [:div {:style {:text-align "center" :top "50px" :font-size "20px" :padding "0px"}} "mons"]
   (component-svg-path-1)
   (component-svg-path-2)
   (component-svg-circle-test t)
   (component-svg-arc t)
   (component-svg-x 590 530)
   (component-svg-x 640 520)
   (component-svg-x 600 500)])

;; -------------------------
;; Initialize app

(defn mount-root []
  (let [entities (atom [])
        t (atom 0)]
    (reagent/render [component-game entities t] (.getElementById js/document "app"))
    (go-loop []
             (<! (timeout 25))
             (swap! t inc)
             (recur))))

(defn init! []
  (mount-root))
