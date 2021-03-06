;; Copyright (C) 2014-2015,2018 Stichting Akvo (Akvo Foundation)
;;
;; This file is part of Akvo FLOW.
;;
;; Akvo FLOW is free software: you can redistribute it and modify it under the terms of
;; the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
;; either version 3 of the License or any later version.
;;
;; Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
;; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
;; See the GNU Affero General Public License included below for more details.
;;
;; The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.

(ns org.akvo.flow.dashboard.users.user-details
  (:require [clojure.string :as str]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.projects.store :as projects-store]
            [org.akvo.flow.dashboard.user-auth.store :as user-auth-store]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.ajax-helpers :refer (default-ajax-config)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]
            [ajax.core :refer (url-request-format GET POST PUT DELETE)])
  (:require-macros [org.akvo.flow.dashboard.t :refer (t>)]))

(defn panel-header-section [{:keys [user close!]} owner]
  (om/component
   (html
    [:div.row.panelHeader
     [:div.col-xs-9.text-left.panelTitle
      [:h4
       (b/icon :pencil) " " (t> _editing) " " [:span.usrNm (get user "userName")]]]
     [:div.col-xs-3.text-right
      (b/btn-primary {:on-click #(close!)} :circle-arrow-left (t> _go_back))]])))

(defn target-value [event]
  (-> event .-target .-value))

(defn update-input! [owner key]
  (fn [event]
    (om/set-state! owner key (target-value event))))

(defn super-admin? [user]
  (let [permission-list (aget user "permissionList")]
    (assert (integer? permission-list))
    (zero? permission-list)))

(defn admin? [user]
  (let [permission-list (aget user "permissionList")]
    (assert (integer? permission-list))
    (= permission-list 10)))

(defn admin-str? [user]
  (= (get user "permissionList") "10"))

(defn user-edit-section [{:keys [on-save user]} owner]
  (reify
    om/IInitState
    (init-state [this] user)

    om/IWillReceiveProps
    (will-receive-props [this {:keys [user]}]
      (om/set-state! owner user))

    om/IRenderState
    (render-state [this {:strs [userName emailAddress permissionList] :as state}]
      (html
       [:div.userEditSection.topMargin
        [:h2 (t> _user_info) ":"]
        [:form
         [:div.form-group
          [:label.control-label.text-left {:for "username"} (t> _name)]
          [:input.form-control {:value userName
                                :placeholder (t> _enter_full_name)
                                :on-change (update-input! owner "userName")}]]
         [:div.form-group
          [:label.control-label.text-left {:for "email"} (t> _email)]
          [:input.form-control {:value emailAddress
                                :placeholder (t> _email_placeholder)
                                :on-change (update-input! owner "emailAddress")}]]
         [:div.form-group
          [:label
           [:input {:type "checkbox" :value permissionList :checked (admin-str? state)
                    :on-change #(condp = (target-value %)
                                  "10" (om/set-state! owner "permissionList" "20")
                                  "20" (om/set-state! owner "permissionList" "10"))}]
           " " (t> _administrator)]]
         [:div.form-group
          (b/btn-primary {:class (when (= state user) "disabled")
                          :on-click #(do (.preventDefault %)
                                         (on-save state))}
                         :floppy-disk (t> _save_user_info))]]]))))


(defn actions [user-auth owner]
  (reify
    om/IInitState
    (init-state [this]
      {:confirm-delete? false})
    om/IRenderState
    (render-state [this {:keys [confirm-delete?]}]
      (html
       (if confirm-delete?
        [:span
         [:strong (t> _delete) "?"]
         (b/btn-link {:on-click #(do (om/set-state! owner :confirm-delete? false)
                                     (dispatch :user-auth/delete user-auth))}
                     (t> _yes))
         " / "
         (b/btn-link {:on-click #(om/set-state! owner :confirm-delete? false)} (t> _no))]
        (b/btn-link {:on-click #(om/set-state! owner :confirm-delete? true)} :remove (t> _delete)))))))


(defn role-label [{:strs [name]} owner]
  (om/component
   (html
    [:span name])))

(defn current-user []
  (-> js/window
      (aget "parent")
      (aget "FLOW")
      (aget "currentUser")))

(defn survey-or-folder [s]
  (let [name (get s "name")
        type (get s "projectType")]
    (if (= type "PROJECT_FOLDER")
      [:span (b/fa-icon :folder) " " name]
      [:span (b/fa-icon :pencil-square-o) " " name])))

(defn sort-folders-and-surveys [s]
  (let [{folders "PROJECT_FOLDER" surveys "PROJECT"} (group-by #(get % "projectType") s)]
    (concat (sort-by #(get % "name") folders)
            (sort-by #(get % "name") surveys))))

(defn roles-and-permissions [{:keys [user users-store roles-store projects-store user-auth-store]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:selected-role nil
       :selected-folders []})

    om/IWillMount
    (will-mount [this]
      (dispatch :roles/fetch nil)
      (dispatch :projects/fetch nil))

    om/IRenderState
    (render-state [this {:keys [selected-role selected-folders]}]
      (html [:div.userRolesPerm.well.topMargin
             [:h2 (t> _roles_and_permissions) ":"]
             [:div.form-inline.text-left.paddingTop.roleEditSelect {:role "name"}
              [:div.form-group
               (om/build b/dropdown
                         {:placeholder (t> _select_a_role)
                          :selected selected-role
                          :data (store/get-roles roles-store)
                          :label-fn #(get % "name")
                          :on-select #(om/set-state! owner :selected-role %)})]
              (for [{:keys [selected-folder idx]} (map-indexed (fn [idx sf]
                                                                 {:selected-folder sf
                                                                  :idx idx})
                                                               selected-folders)]
                (let [parent-id (get selected-folder "parentId")]
                  [:div.form-group
                   (om/build b/dropdown
                             {:data (sort-folders-and-surveys
                                     (projects-store/get-projects projects-store parent-id))
                              :selected selected-folder
                              :label-fn survey-or-folder
                              :on-select #(om/set-state! owner :selected-folders
                                                         (conj (subvec selected-folders 0 idx) %))})]))
              (when (or (empty? selected-folders)
                        (= (get (projects-store/get-by-id projects-store
                                                          (get (peek selected-folders) "keyId"))
                                "projectType")
                           "PROJECT_FOLDER"))
                [:div.form-group
                 (let [current-user (current-user)
                       projects (if (and (empty? selected-folders)
                                         (or (super-admin? current-user)
                                             (and (admin? current-user)
                                                  (let [current-user-id (store/get-user-id-by-email users-store
                                                                                                    (aget current-user "email"))
                                                        object-paths (into #{} (map #(get % "objectPath")
                                                                                    (user-auth-store/get-by-user-id user-auth-store
                                                                                                                    current-user-id)))]
                                                    (contains? object-paths "/")))))
                                  (cons {"name" (t> _all_folders) "keyId" 0 "projectType" "PROJECT_FOLDER"}
                                        (projects-store/get-projects projects-store 0))
                                  (projects-store/get-projects projects-store (or (get (peek selected-folders) "keyId") 0)))]
                   (om/build b/dropdown
                             {:placeholder (t> _select_a_folder_or_survey)
                              :data (sort-folders-and-surveys projects)
                              :loading? (projects-store/loading? projects-store)
                              :label-fn survey-or-folder
                              :on-select #(om/set-state! owner :selected-folders
                                                       (conj selected-folders %))}))])
              [:div.form-group
               (b/btn-primary {:class (if (or (nil? selected-role)
                                              (empty? selected-folders))
                                        "disabled")
                               :on-click (fn [evt]
                                           (.preventDefault evt)
                                           (om/set-state! owner {:selected-role nil :selected-folders []})
                                           (dispatch :user-auth/create
                                                     {:user (get user "keyId")
                                                      :role (get selected-role "keyId")
                                                      :object-path (if (zero? (get (first selected-folders) "keyId"))
                                                                     "/"
                                                                     (str "/"
                                                                          (->> selected-folders
                                                                               (map #(get % "name"))
                                                                               (str/join "/"))))
                                                      :object-id (or (get (last selected-folders) "keyId") 0)}))}
                              :plus (t> _add))]]
             (om/build grid
                       {:data (when-let [user-id (get user "keyId")]
                                (user-auth-store/get-by-user-id user-auth-store user-id))
                        :columns [{:title (t> _role)
                                   :cell-fn (fn [user-auth]
                                              (let [role-id (get user-auth "roleId")
                                                    role (store/get-role roles-store role-id)
                                                    name (get role "name")]
                                                name))}
                                  {:title (t> _resource)
                                   :cell-fn #(get % "objectPath")}
                                  {:title (t> _actions)
                                   :class "text-center"
                                   :component actions}]})]))))

(defn user-details [{:keys [close! user users-store projects-store roles-store user-auth-store set-current-user!]} owner]
  (reify
    om/IRender
    (render [this]
      (html
        [:div
         (om/build panel-header-section {:user user
                                         :close! close!})
         (om/build user-edit-section {:user    user
                                      :on-save #(if (integer? (get % "keyId"))
                                                  (dispatch :edit-user %)
                                                  (dispatch :new-user [% (fn [user-id]
                                                                           (set-current-user! (get user-id "keyId")))]))})
         (when (get user "keyId")
           [:div
            (om/build roles-and-permissions {:user user
                                             :users-store users-store
                                             :projects-store projects-store
                                             :roles-store roles-store
                                             :user-auth-store user-auth-store})])]))))
