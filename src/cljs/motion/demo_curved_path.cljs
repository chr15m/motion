(ns motion.demo-curved-path
  (:require [motion.utils :refer [g-trans svg-arc]]
            [motion.components :refer [component-svg-arc]]))

(def default-style {:fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"})

(def m js/Math)

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

