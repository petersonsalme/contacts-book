(ns contacts.components.contact-form
  (:require [helix.core :refer [defnc $ <>]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [clojure.string :as s]
            [contacts.state :refer [use-app-state]]))

(def contact-form-fields
  ["first_name" "last_name" "email"])

(defn make-label-str [s]
  (str (-> s
           (s/replace #"_" " ")
           (s/capitalize))
       ":"))

(defnc contact-display-item [{:keys [label value]}]
  (d/p (d/strong label) value))

(defnc contact-display [{:keys [contact]}]
  (<>
   (map-indexed 
    (fn [i v] ($ contact-display-item {:key i
                                       :label (make-label-str v)
                                       :value (get contact (keyword v))})) 
    contact-form-fields)))

(defnc contact-edit-item [{:keys [label value on-change]}]
  (d/div
   (d/label {:for label} (make-label-str label))
   (d/input {:id label 
             :value value
             :on-change on-change})))

(defnc contact-edit [{:keys [contact]}]
  (let [[state set-state] (hooks/use-state contact)]
    (d/form
     (map-indexed
      (fn [i v] ($ contact-edit-item {:key i
                                      :label v
                                      :value (get state (keyword v)) 
                                      :on-change #(set-state 
                                                   (assoc state 
                                                          (keyword v)
                                                          (.. %
                                                              -target
                                                              -value)))}))
      contact-form-fields))))

(defnc contact-form []
  (let [[edit set-edit] (hooks/use-state false)
        [state _] (use-app-state)
        contact (:selected state)]
    (d/div
     (d/h1 "Contact form")
     (d/button {:on-click #(set-edit (not edit))} "toogle")
     (if edit 
       ($ contact-edit {:contact contact})
       ($ contact-display {:contact contact})))))