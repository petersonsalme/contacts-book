(ns contacts.core
  (:require [ajax.core :refer [GET]]
            [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]))

(defnc nav []
  (d/nav {:class '[py-4 shadow]}
   (d/div {:class '[container]}
    (d/h2 {:class '[text-xl]} "Contact book"))))

(defnc app []
  (d/div 
   ($ nav)))

(defn ^:export init []
  (let [root (rdom/createRoot (js/document.getElementById "app"))]
    (.render root ($ app))))

(comment
  (GET "http://localhost:4000/api/contacts"
    {:handler (fn [resp]
                (.log js/console resp))})
  )
