(ns cotr-mons.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! timeout] :as async])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; -------------------------
;; Functions

(def m js/Math)

;; -------------------------
;; Components

(defn component-svg-example []
  [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "100px" :left "100px" :position "absolute"}}
   [:polyline {:points "0 50 20 20 40 40 100 30 120 40 154 5 154 52 100 100 0 50" :fill "none" :stroke "#41A4E6" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-circle-test [t]
  (let [t-scale 0.3]
    [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "300px" :left "400px" :position "absolute"}}
     [:circle {:cx 100 :cy 100 :r (+ 35 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]    
     [:circle {:cx 100 :cy 100 :r (+ 40 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "4px" :stroke-linecap "round"}]
     [:circle {:cx 100 :cy 100 :r (+ 45 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))

(defn component-game [entities t]
  [:div
   [:div {:style {:text-align "center" :top "50px" :font-size "20px" :padding "0px"}} "cotr mons"]
   (component-svg-example)
   (component-svg-circle-test t)])

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
