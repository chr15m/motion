 (ns motion.nibblets 
   (:require [motion.utils :refer [g-trans]]))

(defn component-svg-x [style x y]
  [:g (g-trans x y)
   [:path (merge style {:d "M -7.5 -7.5 L 7.5 7.5 Z"})]
   [:path (merge style {:d "M 7.5 -7.5 L -7.5 7.5 Z"})]])

(defn component-svg-+ [style x y]
  [:g (g-trans x y)
   [:path (merge style {:d "M 0.5 -7.5 L 0.5 7.5 Z"})]
   [:path (merge style {:d "M -7.5 0.5 L 7.5 0.5 Z"})]])

