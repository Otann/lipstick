(ns lipstick.rfnext.spec-ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log]
            [lipstick.rfnext.source-selector :as selector]
            [lipstick.rfnext.sources :as sources]))


(rf/reg-sub-raw ::spec
  (fn [db _]
    (let [idx (rf/subscribe [::selector/selected])
          spec (rf/subscribe [::sources/content] [idx])]
      (reaction {:idx @idx
                 :spec @spec}))))

;(rf/reg-event-db ::toggle-tag
;  (fn [db [_ tag-name]]))
;
;(rf/reg-event-db ::toggle-path
;  (fn [db [_ tag-name path]]))
;
;(rf/reg-event-db ::param-changed
;  (fn [db [_ tag-name path param value]]))