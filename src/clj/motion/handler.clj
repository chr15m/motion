(ns motion.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [motion.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:div {:class "infinitelives-spinner infinitelives-spinner-vertical-center"}]
   [:div {:id "overlay"}]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (= (env :dev) true) "css/spinner.css" "css/spinner.min.css"))
   (include-css (if (= (env :dev) true) "css/site.css" "css/site.min.css"))])

(def loading-page
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "js/rounding.js")
     (include-js "js/app.js")]))

(defn index-html []
  "output the HTML as a string"
  (print (apply str loading-page)))

(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
