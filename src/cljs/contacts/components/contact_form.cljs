(ns contacts.components.contact-form
  (:require [ajax.core :refer [PUT POST]]
            [helix.core :refer [defnc $ <>]]
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
   (d/label {:for label
             :class '[font-bold]} 
            (make-label-str label))
   (d/input {:id label
             :class '[shadow border rounded w-full py-2 px-3 mb-3]
             :value value
             :on-change on-change})))

(defnc contact-edit [{:keys [contact]}]
  (let [[state set-state] (hooks/use-state contact)
        [app-state actions] (use-app-state)
        selected (:selected app-state)
        {:keys [add-contact update-contact]} actions]
    (d/form {:on-submit (fn [e]
                          (.preventDefault e)
                          (if selected
                            (PUT (str "http://localhost:4000/api/contacts/" (:id selected))
                              (let [{:keys [first_name last_name email]} state]
                                {:params {:first-name first_name
                                          :last-name last_name
                                          :email email}
                                 :format :json
                                 :handler #(update-contact (first (:contact %)))}))
                            (POST "http://localhost:4000/api/contacts"
                              (let [{:keys [first_name last_name email]} state]
                                {:params {:first-name first_name
                                          :last-name last_name
                                          :email email}
                                 :format :json
                                 :handler #(add-contact (first %))}))))}
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
      contact-form-fields)
     (d/button {:type "submit"
                :class '[bg-teal-500 py-2 px-4 w-full text-white]} 
               "Submit"))))

(defnc contact-form []
  (let [[edit set-edit] (hooks/use-state false)
        [state actions] (use-app-state)
        contact (:selected state)
        new-contact (:new-contact actions)]
    (hooks/use-effect
     [contact]
     (if (not contact)
       (set-edit true)
       (set-edit false)))
    (d/div
     (d/div {:class '[mb-2 flex justify-between]}
            (d/button {:class '[bg-teal-500 text-white py-1 px-4 rounded]
                       :on-click #(new-contact)} "New contact")
            (d/button {:class '[bg-teal-500 text-white py-1 px-4 rounded]
                       :on-click #(set-edit (not edit))} "Edit contact"))
     (if edit
       ($ contact-edit {:contact contact})
       ($ contact-display {:contact contact})))))