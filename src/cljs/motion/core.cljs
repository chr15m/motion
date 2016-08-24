(ns motion.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! close! timeout chan] :as async]
              [clojure.string :as string]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [motion.utils :refer [svg-arc timeline g-trans]]
              [motion.demos :as demos])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def m js/Math)

;; -------------------------
;; Components

(defn component-svg-top [ow]
  (let [style {:fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
    [:g
     [:path (merge style {:d (js/roundPathCorners (str "M 0 45 L 48 45 L 98 5 L " ow " 5") 5 false)})]
     [:path (merge style {:d (js/roundPathCorners (str "M 0 50 L 50 50 L 100 10 L " ow " 10") 5 false)})]]))

(defn component-svg-main [size demo-name]
  (fn []
    (let [[ow oh] (map #(int (/ % 2)) @size)]
      [:div
       [:svg {:x 0 :y 0 :width "100%" :height "100%" :style {:top "0px" :left "0px" :position "absolute"}}

        (component-svg-top (* ow 2))

       [:g (g-trans ow oh)
         [(demos/demos demo-name) size]]]

       [:div {:style {:top "10px" :left "18px" :position "absolute" :font-size "20px" :padding "0px"}}
        [:a {:href "/"} "<-"]]])))

;; -------------------------
;; Views

(defn component-page-contents []
  [:div#contents [:h2 "demos"]
   [:ul
    (doall (for [[d f] demos/demos]
             [:li {:key d} [:a {:href (str "/v?" d)} d]]))]])

(defn component-page-viewer []
  (let [demo-name (js/unescape (get (string/split js/document.location.href "?") 1))
        size (atom [(.-innerWidth js/window) (.-innerHeight js/window)])]
    (fn []
      (if (get demos/demos demo-name)
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
