(ns cotr-mons.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! timeout] :as async])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; -------------------------
;; Functions

(def m js/Math)
(def tau (* m.PI 2))

(defn pol2crt [cx cy r a]
  [(+ cx (* r (m.cos a)))
   (+ cy (* r (m.sin a)))])

(defn svg-arc [cx cy r as ae]
  (let [[xs ys] (pol2crt cx cy r (+ (mod ae tau) tau))
        [xe ye] (pol2crt cx cy r (+ (mod as tau) tau))
        direction (if (<= (- ae as) m.PI) 0 1)
        path ["M" xs ys
              "A" r r 0 direction 0 xe ye]]
    (clojure.string/join " " path)))

(defn g-trans [x y]
  {:transform (str "translate(" x "," y ")")})

;; -------------------------
;; Components

(defn component-svg-path-1 [x y]
  [:g (g-trans x y)
   [:path {:d (js/roundPathCorners "M 0 50 L 20 20 L 40 40 L 100 30 L 120 40 L 150 5 L 150 50 L 100 100 Z 0 50" 5 false) :fill "none" :stroke "#41A4E6" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-top [ow]
  [:g
   [:path {:d (js/roundPathCorners (str "M 0 45 L 48 45 L 98 5 L " ow " 5") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
   [:path {:d (js/roundPathCorners (str "M 0 50 L 50 50 L 100 10 L " ow " 10") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-circle-test [t x y]
  (let [t-scale 0.3]
    [:g (g-trans x y)
     [:circle {:cx 0 :cy 0 :r (+ 35 (* (m.sin (* (+ @t 100) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "4px"}]
     [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (* (+ @t 200) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))

(defn component-svg-circle-test-2 [t x y]
  (let [t-scale 0.1]
    [:g (g-trans x y)
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ @t 100) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ @t 200) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ @t 300) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))

(defn component-svg-arc [t x y]
  (let [p (* m.PI 2 (/ (mod @t 100) 100))]
    [:g (g-trans x y)
     [:path {:fill "none" :stroke "#41A4E6" :stroke-width "5" :d (svg-arc 0 0 50 (- p 1) p)}]
     [:circle {:cx 0 :cy 0 :r 53 :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))

(defn component-svg-x [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 0 L 15 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 15 0 L 0 15 Z"}]])

(defn component-svg-+ [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 7.5 0 L 7.5 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 7.5 L 15 7.5 Z"}]])

(defn component-game [entities size t]
  (let [[ow oh] (map #(/ % 2) @size)]
    [:div
     [:div {:style {:top "10px" :left "10px" :position "absolute" :font-size "20px" :padding "0px"}} "ctr"]
     [:svg {:x 0 :y 0 :width "100%" :height "100%" :style {:top "0px" :left "0px" :position "absolute"}}
      (component-svg-top (* ow 2))
      [:g (g-trans ow oh)
       (component-svg-path-1 0 -200)
       (component-svg-circle-test t 300 0)
       (component-svg-circle-test-2 t -200 150)
       (component-svg-arc t -100 0)
       (component-svg-x 190 130)
       (component-svg-x 240 220)
       (component-svg-x 220 240)
       (component-svg-+ -200 -200)
       (component-svg-+ -230 -230)
       (component-svg-+ -190 -250)]]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (let [entities (atom [])
        t (atom 0)
        size (atom [(.-innerWidth js/window) (.-innerHeight js/window)])]
    (reagent/render [component-game entities size t] (.getElementById js/document "app"))
    (go-loop []
             (<! (timeout 25))
             (swap! t inc)
             (recur))))

(defn init! []
  (mount-root))
