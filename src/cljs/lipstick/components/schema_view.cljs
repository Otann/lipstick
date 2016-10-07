(ns lipstick.components.schema-view
  (:require [reagent.core :as r]
            [goog.string :as gstring]
            [lipstick.utils :refer [with-keys]]))


(def ellipsis (gstring/unescapeEntities "&hellip;"))


(defn collapsible []
  (let [collapsed (r/atom false)]
    (fn [open-label close-label & children]
      ; todo: consider using .no-arrow
      [:div.tree-view
       [:div.tree-view_item
        {:on-click #(swap! collapsed not)}
        [:div.tree-view_arrow
         {:class (when @collapsed "tree-view_arrow-collapsed")}]
        [:span.open-label open-label]
        (when @collapsed [:span.close-label ellipsis close-label])]
       [:div.tree-view_children
        {:class (when @collapsed "tree-view_children-collapsed")}
        [:div.tree-view_children-content (with-keys children)]
        [:div close-label]]])))


(defn field [field-name value]
  (let [{:keys [schema optional]} value
        opt-label (when optional "(Optional) ")
        opt-class (when optional "optional")]
    (case (:type schema)

      :object [collapsible
               [:span.field-label {:class opt-class}
                [:span.field-name field-name] ": " opt-label
                [:span.schema-name (:name schema)] " {"]
               [:span.field-label {:class opt-class} "}"]
               (->> schema :properties (map (fn [[k v]] [field k v])) with-keys)]

      :array [collapsible
              [:span.field-label {:class opt-class}
               [:span.field-name field-name] ": " opt-label
               "Array[" [:span.schema-name (-> schema :item-schema :name)] "{"]
              [:span.field-label {:class opt-class} "}]"]
              (->> schema :item-schema :properties (map (fn [[k v]] [field k v])) with-keys)]

      :enum [collapsible
             [:span.field-label {:class opt-class}
              [:span.field-name field-name] ": " opt-label
              (:item-schema schema) " " [:span.schema-name (:name schema)]  "("]
             [:span.field-label {:class opt-class} ")"]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.field [:span.field-label {:class opt-class}
                   [:span.field-name field-name]
                   [:span ": " opt-label (or (-> schema :name)
                                             (str schema))]]])))

(defn schema [schema]
  (let [schema-name (:name schema)]
    (case (:type schema)
      :object [collapsible
               [:span [:span.schema-name schema-name] " {"]
               [:span "}"]
               (->> schema :properties (map (fn [[k v]] [field k v])) with-keys)]

      :array [collapsible
              [:span [:span.schema-name schema-name] " [" [:span.schema-name (-> schema :item-schema :name)] "{"]
              [:span "}]"]
              (->> schema :item-schema :properties (map (fn [[k v]] [field k v])) with-keys)]

      :enum [collapsible
             [:span [:span.schema-name schema-name] " ("]
             [:span ")"]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.schema
       [:span.schema-name (or (-> schema :name)
                              (str schema))]])))