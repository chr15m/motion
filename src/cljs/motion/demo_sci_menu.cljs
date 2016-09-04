(ns motion.demo-sci-menu
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [<! put! close! timeout chan] :as async]
            [motion.utils :refer [g-trans svg-arc svg-path partial-path timeline]]
            [motion.styles :refer [colors]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def m js/Math)

(defn component-sci-menu [event-chan size]
  (let [pos (atom [0 0])
        tl {:time (atom 0) :go (atom false)}
        graph (atom nil)]
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
      (let [t (deref (tl :time))]
        (when (> t 0)
          [:g {:transform (str "translate(" (get @pos 0) "," (get @pos 1) ")") :stroke-width 2}
           [:path {:d "M -15 5 L -15 15 L 15 15 L 15 5"}]
           [:path {:d "M -15 19 L 15 19"}]
           [:path {:d "M -15 23 L 15 23"}]
           [:path {:d "M 0 27 L 15 27"}]
           (when (= (mod (int (/ t 300)) 2) 0)
             [:rect {:x -7.5 :y -5 :width 15 :height 7.5 :rx 3 :ry 3 :fill (colors :orange) :stroke (colors :orange)}])
           [:path {:d (js/roundPathCorners (svg-path (partial-path (m.min 1 (/ t 500)) [ 0 -15   0 -30   40 -70   180 -70])) 5 false)}]
           (when (or (and (> t 500) (and (= (mod (int (/ t 100)) 2) 0))) (> t 1000))
             [:path {:d "M 45 -75 L 180 -75"}])
           [:g {:fill "url(#hatch)"}
            (doall (for [g (range 3)]
                     (when (> t (+ 1000 (* g 100)))
                       [:rect {:x (+ 45 (* g (+ 35 15))) :y -115 :width 35 :height 35 :rx 3 :ry 3 :stroke-width 2 :stroke "#fff" :key g :on-mouse-over #(reset! graph g) :on-mouse-out #(reset! graph nil)}])))]
           [:g {:transform "translate(0,-20)"}
            (when @graph
              [:path {:d "M 45 -100 L 180 -100"}])
            (case @graph
              0 [:path {:d (js/roundPathCorners (svg-path (apply concat (concat [[45 -105]] (doall (for [r (range 8)] [(+ 60 (* r 15)) (- (* (m.max 0 (* (m.sin (/ t (m.max (mod (* r 14543) 1000) 500))) 60)) -1) 105)])) [[180 -105]]))) 5 false)
                        :fill "url(#hatch)"
                        :stroke-width 1}]
              1 [:g (doall (for [r (range 9)] (let [h (m.max 0 (* (m.sin (/ t (m.max (mod (* r 14543) 1000) 100))) 60))]
                                                 [:rect {:key r :x (+ 47 (* r 15)) :y (- -105 h) :width 10 :height h
                                                         :fill (get [(colors :green) (colors :yellow) (colors :red)] (mod (* r 5) 3))
                                                         :stroke "none"}])))]
              2 [:g [:path {:d (svg-arc 112.5 -160 40 (* (/ t 1000) m.PI) (+ (* (/ t 1000) m.PI) (* m.PI 0.25))) :stroke (colors :orange) :stroke-width "4px" :stroke-opacity 0.5}]
                 [:circle {:cx 112.5 :cy -160 :r 50 :stroke (colors :blue)}]
                 [:circle {:cx 112.5 :cy -160 :r (* (/ (mod t 1000) 1000) 30) :stroke-opacity (- 1 (/ (mod t 1000) 1000)) :stroke (colors :red)}]]
              nil [:g])
            ]])))))
