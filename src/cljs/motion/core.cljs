(ns motion.core
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
        path ["M" (m.round xs) (m.round ys)
              "A" r r 0 direction 0 (m.round xe) (m.round ye)]]
    (clojure.string/join " " path)))

(defn g-trans [x y]
  {:transform (str "translate(" x "," y ")")})

(def default-style {:fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"})

;; -------------------------
;; Components

(defn component-svg-filter-glow []
  [:filter {:id "glowfilter" :x 0 :y 0 :width "200%" :height "200%"
            :dangerouslySetInnerHTML
            {:__html "<feGaussianBlur in='SourceGraphic' stdDeviation='5'/>
                      <feMerge>
                        <feMergeNode/><feMergeNode in='SourceGraphic'/>
                      </feMerge>"}}])

(defn component-svg-pattern-hatch []
  [:pattern {:id "hatch" :width 7 :height 7 :patternTransform "rotate(45 0 0)" :patternUnits "userSpaceOnUse"}
   [:line {:x1 0 :y1 0 :x2 0 :y2 7 :style {:stroke "#41A4E6" :stroke-width 2}}]])

(defn component-svg-path-1 [x y]
  (let [over (atom false)]
    (fn []
      [:g (merge (g-trans x y) {:on-mouse-over (fn [ev] (reset! over true) nil) :on-mouse-out (fn [ev] (reset! over false) nil)})
       [:path {:d (js/roundPathCorners "M -100 -100 L 100 -100 L 120 -80 L 200 -80 L 200 0 L 0 0 L -20 -20 L -100 -20 Z" 5 false) :fill "url(#hatch)" :stroke (if @over "#E6A441" "#41A4E6") :stroke-width "2px" :stroke-linecap "round"}]])))

(defn component-svg-hexagon [x y r & {:keys [style] :or {style default-style}}]
  (let [seg (/ m.PI 3)
        coords (map #(let [a (+ (* % seg) (/ seg 2))] (str (m.round (* r (m.sin a))) " " (m.round (* r (m.cos a))))) (range 6))]
  [:g (g-trans x y)
   [:path (merge {:d (js/roundPathCorners (str "M " (clojure.string/join " L " coords) " Z") 5 false)} style)]]))

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

(defn component-svg-arc [x y r as ae t]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width t :d (svg-arc 0 0 r as ae)}]])

(defn component-svg-arc-thing [t x y]
  (let [p (* m.PI 2 (/ (mod @t 100) 100))]
    [:g (g-trans x y)
     [:path {:fill "none" :stroke "#41A4E6" :stroke-width "5" :d (svg-arc 0 0 51 (- p 1) p)}]
     [:circle {:cx 0 :cy 0 :r 53 :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))

(defn component-svg-hex-thing [x y]
  (let [over (atom false)]
    (fn []
      [:g {:on-mouse-over (fn [ev] (reset! over true) nil) :on-mouse-out (fn [ev] (reset! over false) nil)}
       (component-svg-hexagon x y 40 :style (assoc default-style :fill "url(#hatch)"))
       (if @over
         [:g
          (component-svg-arc x y 78 (* m.PI .75) (* m.PI 1.25) 2)
          (component-svg-arc x y 78 (* m.PI -0.25) (* m.PI 0.25) 2)
          (component-svg-arc x y 70 (* m.PI .75) (* m.PI 1.25) 5)
          (component-svg-arc x y 70 (* m.PI -0.25) (* m.PI 0.25) 5)])])))

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
      [:defs
        (component-svg-filter-glow)
        (component-svg-pattern-hatch)]
      (component-svg-top (* ow 2))
      [:g (merge (g-trans ow oh) (comment {:filter "url(#glowfilter)"}))
       [component-svg-path-1 0 -200]
       (component-svg-circle-test t 300 0)
       (component-svg-circle-test-2 t -200 150)
       (component-svg-arc-thing t -100 0)
       [component-svg-hex-thing -400 200]
       (component-svg-hexagon -350 0 40)
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
