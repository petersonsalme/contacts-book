(ns contacts.components.contact-list
  (:require [helix.core :refer [defnc <> $]]
            [helix.dom :as d]))

(defnc contact-list-item [{:keys [contact]}]
  (d/li
   (d/div
    (d/p (str (:first_name contact) " " (:last_name contact))))))

(defn -as-item [i contact]
  ($ contact-list-item {:key i
                        :contact contact}))

(defnc contact-list [{:keys [contacts]}]
  (<>
   (d/ul 
    (map-indexed -as-item contacts))))
