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

(defn component-svg-example []
  [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "100px" :left "100px" :position "absolute"}}
   [:polyline {:points "0 50 20 20 40 40 100 30 120 40 154 5 154 52 100 100 0 50" :fill "none" :stroke "#41A4E6" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-circle-test [t]
  (let [t-scale 0.3]
    [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "300px" :left "400px" :position "absolute"}}
     [:circle {:cx 100 :cy 100 :r (+ 35 (* (m.sin (* (+ @t 100) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]    
     [:circle {:cx 100 :cy 100 :r (+ 40 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "4px"}]
     [:circle {:cx 100 :cy 100 :r (+ 45 (* (m.sin (* (+ @t 200) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))

(defn component-svg-arc [t]
  (let [p (* m.PI 2 (/ (mod @t 100) 100))]
  [:svg {:x 0 :y 0 :width 200 :height 200 :style {:top "200px" :left "600px" :position "absolute"}}
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "5" :d (svg-arc 100 100 50 (m.max 0 (- p 1)) p)}]
   [:circle {:cx 100 :cy 100 :r 53 :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))

(defn component-svg-x []
  [:svg {:x 0 :y 0 :width 25 :height 25 :style {:top "500px" :left "700px" :position "absolute"}}
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 0 L 25 25 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 25 0 L 0 25 Z"}]])

(defn component-game [entities t]
  [:div
   [:div {:style {:text-align "center" :top "50px" :font-size "20px" :padding "0px"}} "cotr mons"]
   (component-svg-example)
   (component-svg-circle-test t)
   (component-svg-arc t)  
   (component-svg-x)])

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
