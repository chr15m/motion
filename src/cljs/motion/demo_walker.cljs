(ns motion.demo-walker
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [put! chan timeout <!] :as async]
            [motion.utils :refer [timeline svg-path partial-path svg-arc hex-pos]]
            [motion.demo-curved-path :refer [component-svg-hexagon component-svg-rounded-hexagon]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(def m js/Math)

(defn arms-1 []
  [:g
   [:path {:d "M -7.5 -59.5 L -15.5 -25.5"}]
   [:path {:d "M 7.5 -59.5 L 15.5 -25.5"}]])

(defn arms-2 []     
  [:g
   [:path {:d "M -7.5 -59.5 L -5.5 -25.5"}]
   [:path {:d "M 7.5 -59.5 L 5.5 -25.5"}]])

(defn component-walker [style-1 style-2 position timeline]
    (let [helmet-path (str "M 15.5 -60.5 L -15.5 -60.5"
                         " "
                         (js/roundPathCorners "M -15.5 -60.5 L -15.5 -95.5 L 15.5 -95.5 L 15.5 -60.5" 15 false))]
      (fn []
        (let [frame (mod (int (/ @timeline 150)) 2)
              legs-frame frame
              arms-frame frame]
          [:g (merge style-1 {:stroke-width "2px" :transform (str "translate(" (first @position) "," (second @position) ")")})
           ; head
           [:g style-2
            [:path {:d helmet-path}]
            [:path {:d (js/roundPathCorners "M -11.5 -80.5 L -11.5 -91.5 L 0 -91.5" 15 false) :fill "none" :stroke "#383838" :stroke-width "3px"}]]
           ; arms
           (if (= @timeline 0)
             (arms-1)
             (case arms-frame
               0 [arms-1]
               1 [arms-2]))
           ; body
           [:path {:d "M -7.5 -30.5 L -7.5 -60.5 L 7.5 -60.5 L 7.5 -30.5"
                   :fill "url(#hatch)"}]
           ; legs
           (if (= @timeline 0)
             [:path {:d "M -7.5 0 L -7.5 -30.5 L 7.5 -30.5 L 7.5 0"}]
             (case legs-frame
               0 [:path {:d "M -5.5 0 L -7.5 -30.5 L 7.5 -30.5 L 5.5 0"}]
               1 [:path {:d "M -10.5 0 L -7.5 -30.5 L 7.5 -30.5 L 10.5 0"}]))]))))

(defn component-walker-hex-map [style-1 style-2 style-3 click-channel]
  (print "click-pre" click-channel)
  (let [selected (atom nil)
        size 50]
    (fn []
      [:g {:stroke "none"}
       (doall
         (for [c (range -4 5) r (range -3 3)]
           (let [[x y] (hex-pos size c r)
                 selected? (= @selected [c r])]
             [(if selected? component-svg-rounded-hexagon component-svg-hexagon)
              (assoc (if selected? style-3 style-2)
                     :key (str "hex-" c "-" r)
                     :on-mouse-over (fn [ev] (reset! selected [c r]) nil)
                     :on-mouse-out (fn [ev] (reset! selected nil) nil)
                     :on-click (fn [ev] (print "putting" [x y]) (put! click-channel [x y]) (reset! selected [c r]) nil))
              x y (* size 0.95)])))])))

(defn component-walker-demo-world [style-1 style-2]
  (let [timeline-walk {:time (atom 0) :go (atom false)}
        position (atom [0 0])
        destination (atom [0 0])
        click-chan (chan)
        speed 2]
    (go-loop []
             (reset! destination (<! click-chan))
             (recur))
    (go-loop []
             (<! (timeout 16))
             (let [[x2 y2] @destination
                   [x1 y1] @position
                   distance (m.sqrt (+ (m.pow (- x2 x1) 2) (m.pow (- y2 y1) 2)))
                   [xn yn] [(/ (- x2 x1) distance) (/ (- y2 y1) distance)]
                   walking? (timeline-walk :go)]
               (if (> distance speed)
                 (do
                   (if (not @walking?)
                     (timeline js/Infinity :atoms timeline-walk))
                   (swap! position (fn [[xo yo]] [(+ xo (* xn speed)) (+ yo (* yn speed))])))
                 (if @walking?
                   (reset! (timeline-walk :go) false)
                   (reset! (timeline-walk :time) 0))))
             (recur))
    (fn []
      [:g 
       [component-walker-hex-map style-1 style-2 (assoc style-2 :fill "url(#hatch)") click-chan]
       [component-walker style-1 style-2 position (timeline-walk :time)]])))
