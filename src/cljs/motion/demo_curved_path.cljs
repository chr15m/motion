(ns motion.demo-curved-path
  (:require [reagent.core :as reagent :refer [atom]]
            [motion.utils :refer [g-trans svg-arc]]
            [motion.components :refer [component-svg-arc]]))

(def m js/Math)

(defn component-svg-path-1 [style x y]
  (let [over (atom false)]
    (fn []
      [:g (merge style (g-trans x y) {:on-mouse-over (fn [ev] (reset! over true) nil) :on-mouse-out (fn [ev] (reset! over false) nil)})
       [:path {:d (js/roundPathCorners "M -100 -100 L 100 -100 L 120 -80 L 200 -80 L 200 0 L 0 0 L -20 -20 L -100 -20 Z" 5 false) :fill "url(#hatch)" :stroke (if @over "#E6A441" "#41A4E6") :stroke-width "2px"}]])))

(defn component-svg-hexagon [style x y r]
  (let [seg (/ m.PI 3)
        coords (map #(let [a (+ (* % seg) (/ seg 2))] (str (m.round (* r (m.sin a))) " " (m.round (* r (m.cos a))))) (range 6))]
  [:g (merge style (g-trans x y))
   [:path {:d (js/roundPathCorners (str "M " (clojure.string/join " L " coords) " Z") 5 false)}]]))

(defn component-svg-hex-thing [style x y]
  (let [over (atom false)]
    (fn []
      [:g (merge style {:on-mouse-over (fn [ev] (reset! over true) nil) :on-mouse-out (fn [ev] (reset! over false) nil)})
       (component-svg-hexagon {:fill "url(#hatch)"} x y 40)
       (if @over
         [:g
          (component-svg-arc style x y 78 (* m.PI .75) (* m.PI 1.25) 2)
          (component-svg-arc style x y 78 (* m.PI -0.25) (* m.PI 0.25) 2)
          (component-svg-arc style x y 70 (* m.PI .75) (* m.PI 1.25) 5)
          (component-svg-arc style x y 70 (* m.PI -0.25) (* m.PI 0.25) 5)])])))

(defn component-svg-twolines [style]
  [:g (merge style {:transform "translate(0,100)"})
   [:path {:d (js/roundPathCorners (str "M -200 45 L 48 45 L 98 5 L 200 5") 5 false) :stroke-width "2px"}]
   [:path {:d (js/roundPathCorners (str "M -200 50 L 50 50 L 100 10 L 200 10") 5 false) :stroke-width "2px"}]])
