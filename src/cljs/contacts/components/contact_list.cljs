(ns contacts.components.contact-list
  (:require [ajax.core :refer [DELETE]] 
            [helix.core :refer [defnc <> $]]
            [helix.dom :as d]
            [contacts.state :refer [use-app-state]]))

(defnc contact-list-item [{:keys [contact]}]
  (let [[_ actions] (use-app-state)
        set-selected (:select actions)
        remove-contact (:remove-contact actions)]
  (d/li {:class '[mb-2]}
   (d/div {:class '[flex justify-between]}
          (d/p (str (:first_name contact) " " (:last_name contact)))
          (d/div 
           (d/button {:class '[bg-teal-500 text-white py-1 px-4 rounded focus:bg-teal-300]
                      :on-click #(set-selected contact)} "Select")
           (d/button {:class '[bg-red-500 text-white ml-2 py-1 px-4 rounded focus:bg-red-300]
                      :on-click #(DELETE 
                                   (str "http://localhost:4000/api/contacts/" (:id contact))
                                   {:handler (fn [_] (remove-contact (:id contact)))})} "Delete"))))))

(defnc contact-list []
  (let [[state _] (use-app-state)
        contacts (:contacts state)]
    (<>
     (d/ul
      (map-indexed (fn [i contact]
                     ($ contact-list-item {:key i
                                           :contact contact})) contacts)))))
