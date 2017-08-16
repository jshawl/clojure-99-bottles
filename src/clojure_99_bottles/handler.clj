(ns clojure-99-bottles.handler
  (:use ring.util.response)
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn link [text href]
  (str "<a href='" href "'>" text "</a>"))

(defn take-one-down [i req & [ext]]
  (let [ix (read-string i)]
    (let [nextix (if (= ix 0) 99 (dec ix))]
      {:count (str i " bottles of beer on the wall ") 
        :action (if (= ix 0) "Start over" "Take one down")
        :next (str (name (:scheme req)) "://" (:server-name req) ":" (:server-port req) "/" nextix ext)})))
    
(defroutes app-routes
  (GET "/" [] 
    (redirect "/99"))
  (GET "/:i.json" request
    {:body (take-one-down (:i (:params request)) request ".json")})
  (GET "/:i" request
    (let [d (take-one-down (:i (:params request)) request)]
      (str 
        (:count d) (link 
          (:action d) (:next d)))))
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (handler #'app-routes) {:port port :join? false})))
