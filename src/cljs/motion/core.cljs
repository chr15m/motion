(ns motion.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! close! timeout chan] :as async]
              [clojure.string :as string]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [motion.utils :refer [svg-arc timeline g-trans]])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def default-style {:fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"})

(def m js/Math)

;; -------------------------
;; Components

(defn component-svg-filter-glow []
  [:filter {:id "glowfilter" :x 0 :y 0 :width "200%" :height "200%"
            :dangerouslySetInnerHTML
            {:__html "<feGaussianBlur in='SourceGraphic' stdDeviation='5'/>
                      <feMerge>
                        <feMergeNode/><feMergeNode in='SourceGraphic'/>
                      </feMerge>"}}])

(defn component-svg-pattern-hatch []
  [:pattern {:id "hatch" :width 7 :height 7 :patternTransform "rotate(45 0 0)" :patternUnits "userSpaceOnUse"}
   [:line {:x1 0 :y1 0 :x2 0 :y2 7 :style {:stroke "#41A4E6" :stroke-width 2}}]])

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

(defn component-svg-top [ow]
  [:g
   [:path {:d (js/roundPathCorners (str "M 0 45 L 48 45 L 98 5 L " ow " 5") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
   [:path {:d (js/roundPathCorners (str "M 0 50 L 50 50 L 100 10 L " ow " 10") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-circle-test [x y]
  (let [[tl] (timeline js/Infinity)]
    (fn []
      (let [t (/ @tl 500)]
        [:g (g-trans x y)
         [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (+ t 1)) 10)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
         [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (+ t 1.5)) 10)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
         [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (+ t 2)) 10)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))))

(defn component-svg-circle-test-2 [x y]
  (let [t-scale 0.1
        [tl] (timeline js/Infinity)]
    (fn []
      (let [t (/ @tl 10)]
        [:g (g-trans x y)
         [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ t 100) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
         [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ t 200) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
         [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ t 300) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))))

(defn component-svg-circle-test-3 [x y]
  (let [duration 2000
        [t g c] (timeline duration)]
    (go-loop [[t g c] [t g c]]
      (<! c)
      (recur (timeline 2000 :atoms {:time t :go g})))
    (fn []
      (if @g
        [:g (g-trans x y)
         [:circle {:cx 0 :cy 0 :r (+ (/ @t 20) 3) :fill "none" :stroke "#41A4E6" :stroke-opacity (- 1 (/ @t duration)) :stroke-width "2px"}]]))))

(defn component-svg-arc [x y r as ae th]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width th :d (svg-arc 0 0 r as ae)}]])

(defn component-svg-arc-thing [x y]
  (let [[t] (timeline js/Infinity)]
    (fn []
      (let [p (* m.PI 2 (/ (mod @t 2000) 2000))]
        [:g (g-trans x y)
         [:path {:fill "none" :stroke "#41A4E6" :stroke-width "5" :d (svg-arc 0 0 51 (- p 1) p)}]
         [:circle {:cx 0 :cy 0 :r 57 :fill "none" :stroke "#41A4E6" :stroke-width "3px" :stroke-linecap "round"}]     
         [:circle {:cx 0 :cy 0 :r 53 :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))))

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

(defn component-svg-x [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 0 L 15 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 15 0 L 0 15 Z"}]])

(defn component-svg-+ [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 7.5 0 L 7.5 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 7.5 L 15 7.5 Z"}]])

(defn component-demo-nibblets [size]
  (fn []
    [:g
       (component-svg-x 90 30)
       (component-svg-x 140 20)
       (component-svg-x 120 40)
       (component-svg-+ -100 -100)
       (component-svg-+ -130 -130)
       (component-svg-+ -90 -150)]))

(defn component-demo-circles [size]
  (fn []
    [:g
     [component-svg-circle-test 150 -100]
     [component-svg-circle-test-2 -200 50]
     [component-svg-circle-test-3 0 100]
     [component-svg-arc-thing -100 -100]]))

(defn component-demo-curved-path [size]
  (fn []
    [:g
     [:defs
      (component-svg-filter-glow)
      (component-svg-pattern-hatch)]
     [:g
      [component-svg-path-1 0 -100]
      [component-svg-hex-thing -80 0]
      [component-svg-hexagon 180 0 40]
      [component-svg-hexagon 80 0 40]
      [:g {:transform "translate(0,100)"}
       [:path {:d (js/roundPathCorners (str "M -100 45 L 48 45 L 98 5 L 200 5") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
       [:path {:d (js/roundPathCorners (str "M -100 50 L 50 50 L 100 10 L 200 10") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]]]]))

(def demos {"curved path" component-demo-curved-path
            "circles" component-demo-circles
            "nibblets" component-demo-nibblets})

(defn component-svg-main [size demo-name]
  (fn []
    (let [[ow oh] (map #(int (/ % 2)) @size)]
      [:div
       [:svg {:x 0 :y 0 :width "100%" :height "100%" :style {:top "0px" :left "0px" :position "absolute"}}

        (component-svg-top (* ow 2))

        [:g (g-trans ow oh)
         [(demos demo-name) size]]]

       [:div {:style {:top "10px" :left "10px" :position "absolute" :font-size "20px" :padding "0px"}}
        [:a {:href "/"} "<-"]]])))

;; -------------------------
;; Views

(defn component-page-contents []
  [:div#contents [:h2 "demos"]
   [:ul
    (doall (for [[d f] demos]
             [:li {:key d} [:a {:href (str "/v?" d)} d]]))]])

(defn component-page-viewer []
  (let [demo-name (js/unescape (get (string/split js/document.location.href "?") 1))
        size (atom [(.-innerWidth js/window) (.-innerHeight js/window)])]
    (fn []
      (if (get demos demo-name)
        [component-svg-main size demo-name]
        [:div#contents "Demo not found."]))))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'component-page-contents))

(secretary/defroute "/v" []
  (session/put! :current-page #'component-page-viewer))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
