 (ns motion.demo-nibblets 
   (:require [motion.utils :refer [g-trans]]))

(defn component-svg-x [style x y]
  [:g (merge style (g-trans x y))
   [:path {:d "M -7.5 -7.5 L 7.5 7.5 Z"}]
   [:path {:d "M 7.5 -7.5 L -7.5 7.5 Z"}]])

(defn component-svg-+ [style x y]
  [:g (merge style (g-trans x y))
   [:path {:d "M 0.5 -7.5 L 0.5 7.5 Z"}]
   [:path {:d "M -7.5 0.5 L 7.5 0.5 Z"}]])

(defn component-svg-o [style x y]
  [:g (merge style (g-trans x y))
   [:circle {:cx 0 :cy 0 :r 7.5}]])
