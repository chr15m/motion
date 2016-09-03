(ns motion.demo-hex-popout
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [<! put! close! timeout chan] :as async]
            [motion.utils :refer [g-trans svg-arc hexagon hex-pos timeline]]
            [motion.styles :refer [colors]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def m js/Math)

(defn component-hexagon [style [x y] r]
  [:g (merge style (g-trans x y))
   [:path {:d (str "M " (clojure.string/join " L " (hexagon r false)) " Z")}]])

(defn offset [size pos r c]
  (map-indexed (fn [i e] (+ e (get pos i))) (hex-pos size r c)))

(defn component-hex-popout [event-chan style-1 style-2 wh]
  (let [selected (atom nil)
        pos (atom [0 0])
        tl {:time (atom 0) :go (atom false)}
        size 50
        hex-layout [[0 0 "#383838"] [0 1 "url(#hatch)"] [1 -1 (colors :blue)] [-1 1 (colors :blue)] [-2 -1 "url(#hatch)"]]]
    (go-loop []
             (let [[e args] (<! event-chan)]
               (when (= e "click")
                 (if (deref (tl :go))
                   (do
                     (swap! (tl :go) not)
                     (reset! (tl :time) 0))
                   (do
                     (reset! pos args)
                     (timeline js/Infinity :atoms tl))))
               (recur)))
    (fn []
      (let [p (partial offset size @pos)
            t (deref (tl :time))]
        [:g {:transform (str "translate(" (get @pos 0) "," (get @pos 1) ")")}
         (when (> t 0)
           [:g
            (map-indexed (fn [i h] (if (> t (* 150 i))
                                     [component-hexagon {:key i :fill (get h 2)} (hex-pos size (get h 0) (get h 1)) (* size 0.95)]))
                         hex-layout)
            (when (or (and (> t 500) (= (mod (int (/ t 100)) 2) 0)) (> t 1000))
              [:g {:transform "translate(50,50)"}
               [:path {:d (js/roundPathCorners (str "M 0 160 L 50 160 L 100 110 L 100 60") 5 false) :stroke-width "2px" :fill "none"}]
               [:path {:d (js/roundPathCorners (str "M 0 165 L 52 165 L 105 112 L 105 60") 5 false) :stroke-width "2px" :fill "none"}]])
            (when (and (> t 800) (= (mod (int (/ t 300)) 2) 0))
              [:circle {:cx 130 :cy 212 :r 5 :fill (colors :blue) :fill-opacity 1 :stroke "none"}])
            [:g
             (let [[x y] (hex-pos size -1 0)
                   fade (/ (m.max (- 100 t) 0) 100)]
               [:path {:d (clojure.string/join " " ["M" (+ (* (* (get @wh 0) -1) fade) x) y "L" x y]) :stroke-width "20px" :stroke-linecap "square" :stroke-opacity (+ (* fade 0.5) 0.5)}])]
            (when (or (and (> t 800) (= (mod (int (/ t 100)) 2) 0)) (> t 1000))
              (let [p (* m.PI 2 (/ (mod t 1000) 1000))
                    p2 (* m.PI 2 (/ (mod t 3221) 3221))
                    [x y] (hex-pos size -2 -1)]
                [:g {:fill "none" :stroke-linecap "square"}
                 [:path {:d (svg-arc x y 80 (* m.PI 0.666) (* m.PI 2)) :stroke-width "10px" :stroke-opacity 0.5}]
                 [:path {:d (svg-arc x y 70 (m.min p p2) (m.max p p2)) :stroke-width "2px"}]
                 [:path {:d (svg-arc x y 60 (- p 1) p) :stroke-width "5px"}]]))])]))))

