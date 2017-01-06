(ns lipstick.reframe.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log]))


(rf/reg-sub :active-page
  (fn [db _]
    (:active-page db)))

(rf/reg-sub :config
  (fn [db _]
    (:config db)))

(rf/reg-sub :auth
  (fn [db _]
    (:auth db)))


(rf/reg-sub :specs
  (fn [db _]
    (:specs db)))

(rf/reg-sub :selected-spec
  (fn [db _]
    (let [idx (-> db :ui :selected-spec-id)]
      (get-in db [:specs idx]))))

(rf/reg-sub :ui-state
  (fn [db event]
    (get-in db (into [:ui] (rest event)))))

;(rf/reg-sub :ui/spec
;  (fn [db [_ spec-idx]]
;    (get-in db [:ui :specs spec-idx])))
;
;(rf/reg-sub-raw :ui/spec.tag
;  (fn [_ [_ spec-idx tag-name]]
;    (let [spec (rf/subscribe [:ui/spec spec-idx])]
;      (reaction (get-in @spec [:tags tag-name])))))
;
;(rf/reg-sub-raw :ui/spec.tag.collapsed
;  (fn [_ [_ spec-idx tag-name]]
;    (let [tag (rf/subscribe [:ui/tag spec-idx tag-name])]
;      (reaction (:collapsed @tag)))))
;
;(rf/reg-sub-raw :ui/tag.path
;  (fn [_ [_ spec-idx tag-name method path-name]]
;    (let [path (rf/subscribe [:ui/tag spec-idx tag-name])])))

(rf/reg-sub :ui-tag-collapsed
  (fn [db [_ spec-id tag-name]]
    (get-in db [:ui :spec spec-id :tags tag-name :collapsed] false)))

(rf/reg-sub :ui-path-collapsed
  (fn [db [_ spec-id tag-name method path-name]]
    (get-in db [:ui :spec spec-id :tags tag-name :paths [method path-name] :collapsed] true)))

(rf/reg-sub :ui-parameter-changed
  (fn [db [_ spec-id tag-name method path-name location param-name]]
    (let [params (get-in db [:ui :spec spec-id :tags tag-name :paths [method path-name]])]
      )))