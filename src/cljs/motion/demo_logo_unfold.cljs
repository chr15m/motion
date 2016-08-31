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

(defn component-logo-unfold-2 [style-1 style-2 size x y]
  (let [unfolder {:time (atom 0) :go (atom false)}]
    (fn []
      (let [u (deref (unfolder :time))
            p (m.min (/ u 1000) 1)
            w (/ (get @size 0) 2)]
        [:g
         (if (= u 0) [:g {:on-click (partial handle-planet-click unfolder)}
                      [:circle {:cx 0 :cy 0 :r 50 :fill (if (> u 0) "url(#hatch)" "#383838") :stroke-width "2px"}]])
         (when (> u 0)
           [:g {:stroke "#3A586C"}
            [:path {:d (js/roundPathCorners (svg-path (partial-path p [ 35  25   65  55  w 55])) 5 false) :stroke-width "5px"}]
            [:path {:d (js/roundPathCorners (svg-path (partial-path p [ 25  35   58  69  w 69])) 5 false) :stroke-width "5px"}]
            [:path {:d (js/roundPathCorners (svg-path (partial-path p [ -35  -25   -65  -55  (* -1 w) -55])) 5 false) :stroke-width "5px"}]
            [:path {:d (js/roundPathCorners (svg-path (partial-path p [ -25  -35   -58  -69  (* -1 w) -69])) 5 false) :stroke-width "5px"}]])
         (when (> u 0)
           [:g (merge style-2 {:stroke "none" :transform "translate(0,-30)" :fill "#3A586C" :fill-opacity p})
            [:g
             (shapes :wreath)]
            [:g {:transform "scale(-1,1)"}
             (shapes :wreath)]])
         (when (> u 0)
           [:g 
            [:g {:stroke-width 2 :transform "scale(1.33,1.33),translate(-75,75),rotate(45)" :fill "#383838" :fill-opacity p :stroke-opacity p}
                      (shapes :rocket)]])]))))
