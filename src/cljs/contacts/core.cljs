(ns contacts.core
  (:require [ajax.core :refer [GET]]
            [helix.core :refer [defnc $ <> provider]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [contacts.state :refer [app-state initial-state app-reducer use-app-state]]
            [contacts.components.nav :refer [nav]]
            [contacts.components.contact-list :refer [contact-list]]
            [contacts.components.contact-form :refer [contact-form]]))

(defnc app []
  (let [[state actions] (use-app-state)]
    (hooks/use-effect
     :once
     (GET "http://localhost:4000/api/contacts" {:handler (:init actions)}))
    (js/console.log (:init actions))
    (if state
      (<>
       ($ nav)
       (d/div {:class '[container pt-4]}
              ($ contact-list)
              ($ contact-form)))
      (d/p "Loading..."))))

(defnc provided-app []
  (provider
   {:context app-state
    :value (hooks/use-reducer app-reducer initial-state)}
   ($ app)))

(defn ^:export ^:dev/after-load init []
  (let [root (rdom/createRoot (js/document.getElementById "app"))]
    (.render root ($ provided-app))))
