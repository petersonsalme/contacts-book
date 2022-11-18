(ns contacts.core
  (:require [ajax.core :refer [GET]]
            [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [contacts.components.nav :refer [nav]]
            [contacts.components.contact-list :refer [contact-list]]
            [contacts.components.contact-form :refer [contact-form]]))

(defnc app []
  (let [[state set-state] (hooks/use-state nil)]
    (hooks/use-effect
     :once
     (GET "http://localhost:4000/api/contacts" {:handler (fn [resp] (set-state resp))}))
    (<>
     ($ nav)
     (d/div {:class '[container pt-4]} 
            ($ contact-list {:contacts state})
            ($ contact-form {:contact (first state)})))))

(defn ^:export ^:dev/after-load init []
  (let [root (rdom/createRoot (js/document.getElementById "app"))]
    (.render root ($ app))))
