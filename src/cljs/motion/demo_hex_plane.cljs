(ns motion.demo-hex-plane
  (:require [reagent.core :as reagent :refer [atom]]
            [motion.utils :refer [g-trans hex-pos]]
            [motion.demo-curved-path :refer [component-svg-hexagon component-svg-rounded-hexagon]]))

(defn component-svg-hex-plane [style-1 style-2]
  (let [selected (atom nil)
        size 50]
    (fn []
      [:g
       (doall
         (for [c (range -4 5) r (range -3 3)]
           (let [[x y] (hex-pos size c r)
                 selected? (= @selected [c r])]
             [(if selected? component-svg-rounded-hexagon component-svg-hexagon)
              (assoc (if selected? style-1 style-2)
                     :key (str "hex-" c "-" r)
                     :on-mouse-over (fn [ev] (reset! selected [c r]) nil)
                     :on-mouse-out (fn [ev] (reset! selected nil) nil)
                     :on-click (fn [ev] (reset! selected [c r]) nil))
              x y (* size 0.95)])))])))
