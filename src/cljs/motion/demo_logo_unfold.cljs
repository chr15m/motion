(ns motion.demo-logo-unfold
  (:require [reagent.core :as reagent :refer [atom]]
            [motion.shapes :refer [shapes]]
            [motion.utils :refer [timeline svg-path partial-path svg-arc]]))

(def m js/Math)

(defn handle-planet-click [unfolder ev]
  (if (= (deref (unfolder :time)) 0)
    (timeline 2000 :atoms unfolder)
    (do
      (reset! (unfolder :go) false)
      (reset! (unfolder :time) 0))))

(defn component-logo-unfold [style-1 style-2 x y]
  (let [unfolder {:time (atom 0) :go (atom false)}]
    (fn []
      (let [u (deref (unfolder :time)) 
            t (m.min (/ u 1000) 1)]
        [:g (merge style-1 {:transform (str "translate(" x "," y ")")})
         [:g
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [ 53  43   107  96    107  193])) 5 false) :stroke-width "2px"}]
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [ 50  50   100  100   100  193])) 5 false) :stroke-width "2px"}]
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [-53  43  -107  96   -107  193])) 5 false) :stroke-width "2px"}]
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [-50  50  -100  100  -100  193])) 5 false) :stroke-width "2px"}]]
         (if (or (= (mod (int (/ u 100)) 2) 1) (> u 1000))
           [:g (merge style-2 {:stroke "none"})
            [:g
             (shapes :wreath)]
            [:g {:transform "scale(-1,1)"}
             (shapes :wreath)]])
         [:g {:on-click (partial handle-planet-click unfolder)}
          [:circle {:cx 0 :cy 0 :r 80 :fill "#383838" :stroke-width "2px"}]
          (if (> u 200)
            [:g {:transform "rotate(60),scale(0.3,1)"}
             [:path {:d (svg-arc 0 0 100 (* m.PI -0.359 2) (* m.PI 2 0.359)) :stroke-width "3px" :stroke-opacity (/ u 2000)}]])]
         [:g (merge style-2 {:fill-opacity (/ u 2000) :stroke "none"})
          [:text {:y 150 :text-anchor "middle"} "veritate"]
          [:text {:y 200 :text-anchor "middle"} "ad astra"]]]))))

