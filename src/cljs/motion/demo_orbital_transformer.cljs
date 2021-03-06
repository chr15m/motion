(ns motion.demo-orbital-transformer
  (:require [reagent.core :as reagent :refer [atom]]
            [motion.utils :refer [timeline]]
            [motion.styles :refer [colors]]
            [motion.components :refer [component-svg-arc]]))

(def m js/Math)

(defn handle-orbiter-click [unfolder ev]
  (if (= (deref (unfolder :time)) 0)
    (timeline 4000 :atoms unfolder)
    (do
      (reset! (unfolder :go) false)
      (reset! (unfolder :time) 0))))

(defn component-orbiter [style]
  (let [[tl] (timeline js/Infinity)
        unfolder {:time (atom 0) :go (atom false)}
        distance 200]
    (fn []
      (let [t (/ @tl 3000)
            t2 (/ @tl 500)
            unfolded (deref (unfolder :time))
            style-alert (merge style (if (> unfolded 1800) {:stroke (colors :red)} {}))]
        [:g
         [:text {:x 0 :y 100} (deref (unfolder :go))]
         [:circle {:cx 0 :cy 0 :r distance}]
         [:g {:transform (str "translate(" (* (m.cos t) distance) "," (* (m.sin t) distance) ")")}
          [:circle {:cx 0 :cy 0 :r 30 :fill (colors :background) :stroke-width "2px" :on-click (partial handle-orbiter-click unfolder)}]
          (if (= unfolded 0)
            [:circle {:cx (+ (* (m.cos t2) 80)) :cy (+ (* (m.sin t2) 80)) :r 10 :fill (colors :background)}])
          ; unfolder
          (if (> unfolded 1800)
            [:path {:d (str "M 21 -21 L 70 -70" (if (> unfolded 2000) "L 200 -70" "")) :stroke-width "2px"}])
          (when (> unfolded 0)
            [:g (if (> unfolded 900) {:transform (str "rotate(" (m.min 90 (/ (- unfolded 900) 10)) ")")})
             (component-svg-arc style-alert 0 0 70 (* m.PI .75) (* m.PI 1.25) 5)
             (component-svg-arc style-alert 0 0 70 (* m.PI -0.25) (* m.PI 0.25) 5)])
          (when (> unfolded 300)
            [:g
             (component-svg-arc style-alert 0 0 78 (* m.PI .75) (* m.PI 1.25) 2)
             (component-svg-arc style-alert 0 0 78 (* m.PI -0.25) (* m.PI 0.25) 2)]) 
          (when (> unfolded 600)
            [:g
             (component-svg-arc style-alert 0 0 85 (* m.PI .75) (* m.PI 1.25) 2)
             (component-svg-arc style-alert 0 0 85 (* m.PI -0.25) (* m.PI 0.25) 2)])
          (when (and (> unfolded 2200) (or (= (mod (int (/ unfolded 100)) 2) 1) (> unfolded 3800)))
            [:g
             [:path {:d (str "M 73 -78 L 197 -78") :stroke-width "5px" :stroke (colors :red)}]
             [:path {:d (str "M 73 -88 L 197 -88") :stroke-width "5px" :stroke (colors :yellow)}]
             [:path {:d (str "M 73 -98 L 197 -98") :stroke-width "5px" :stroke (colors :green)}]
             [:path {:d (str "M 73 -108 L 197 -108") :stroke-width "5px"}]])]]))))
