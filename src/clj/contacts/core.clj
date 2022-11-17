(ns contacts.core
  (:require [org.httpkit.server :refer [run-server]]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-negotiate-middleware
                                                     format-request-middleware
                                                     format-response-middleware]]
            [muuntaja.core :as m]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(def app 
  (ring/ring-handler 
   (ring/router 
    [["/api" {:get (fn [_] {:status 200 :body {:hello "world"}})}]]
    {:data {:muuntaja m/instance
            :middleware [format-negotiate-middleware
                         format-response-middleware
                         exception-middleware
                         format-request-middleware]}})
   (ring/routes 
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler {:not-found (constantly {:status 404 :body "Route not found"})}))))

(defn -main []
  (println "Server started on port 4000")
  (reset! server (run-server app {:port 4000})))

(defn restart-server []
  (stop-server)
  (-main))

(comment
  (restart-server)
  @server
  (-main)
  
  (app {:request-method :get :uri "/api"})
  )