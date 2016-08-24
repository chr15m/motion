 (ns motion.nibblets 
   (:require [motion.utils :refer [g-trans]]))

(defn component-svg-x [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M -7.5 -7.5 L 7.5 7.5 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 7.5 -7.5 L -7.5 7.5 Z"}]])

(defn component-svg-+ [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0.5 -7.5 L 0.5 7.5 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M -7.5 0.5 L 7.5 0.5 Z"}]])

