(ns diceware-generator.server
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku"})

(defroutes routes
  (GET "/" []
     (slurp (io/resource "public/index.html")))
  (GET "*" [filepath]
    (slurp (io/resource (str "public/" filepath))))
  (ANY "*" []
    (route/not-found (slurp (io/resource "404.html")))))

(defn handler [request]
  (-> (response "Hello world")
      (content-type "text/plain")
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)))

(def application (wrap-defaults routes site-defaults))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    #_(run-jetty (site #'app) {:port port :join? false})
    (run-jetty #'application
               {:port port
                :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
