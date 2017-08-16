(ns clojure-99-bottles.handler
  (:use ring.util.response)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn link [text href]
  (str "<a href='" href "'>" text "</a>"))

(defroutes app-routes
  (GET "/" [] 
    (redirect "/99"))
  (GET "/:i" [i]
    (let [ix (read-string i)]
      (str i " bottles of beer on the wall " 
        (if (= ix 0)
          (link "Start over" "/99")
          (link "take one down." 
            (dec 
              (read-string i)))))))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
