(ns lipstick.components.schema-view
  (:require [reagent.core :as r]
            [goog.string :as gstring]
            [lipstick.utils :refer [with-keys]]))


(def ellipsis (gstring/unescapeEntities "&hellip;"))


(defn collapsible []
  (let [collapsed (r/atom false)]
    (fn [open-label close-label & children]
      [:div.tree-view
       [:div.tree-view_item
        {:on-click #(swap! collapsed not)}
        [:div.tree-view_arrow
         {:class (when @collapsed "tree-view_arrow-collapsed")}]
        [:span.open-label open-label]
        (when @collapsed [:span.close-label ellipsis close-label])
        ]
       [:div.tree-view_children
        {:class (when @collapsed "tree-view_children-collapsed")}
        (with-keys children)
        [:div close-label]]])))


(defn field [value]
  (let [{:keys [schema optional]} value
        field-name (:name value)
        opt-label (when optional "(Optional) ")
        opt-class (when optional "optional")]
    (case (:type schema)

      :primitive [:div.field [:span.field-label {:class opt-class}
                              [:span.field-name field-name]
                              [:span ": " opt-label (:name schema)]]]

      :object [collapsible
               [:span.field-label {:class opt-class}
                [:span.field-name field-name] ": " opt-label [:span.schema-name (:name schema)] " {"]
               [:span.field-label {:class opt-class} "}"]
               (->> schema :fields (map #(do [field %])) with-keys)]

      :array [collapsible
              [:span.field-label {:class opt-class}
               [:span.field-name field-name] ": " opt-label "Array[" [:span.schema-name (-> schema :items :name)] "{"]
              [:span.field-label {:class opt-class} "}]"]
              (->> schema :items :fields (map #(do [field %])) with-keys)]

      :enum [collapsible
             [:span.field-label {:class opt-class}
              [:span.field-name field-name] ": " opt-label [:span.schema-name (:name schema)] " ("]
             [:span.field-label {:class opt-class} ")"]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.field [:span.field-label {:class opt-class}
                   [:span.field-name field-name]
                   [:span ": " opt-label (name schema)]]])))

(defn schema [schema]
  (let [schema-name (:name schema)]
    (case (:type schema)
      :primitive [:div.schema
                  [:span.schema-name schema-name]]

      :object [collapsible
               [:span [:span.schema-name schema-name] " {"]
               [:span "}"]
               (->> schema :fields (map #(do [field %])) with-keys)]

      :array [collapsible
              [:span [:span.schema-name schema-name] " [" [:span.schema-name (-> schema :items :name)] "{"]
              [:span "}]"]
              (->> schema :items :fields (map #(do [field %])) with-keys)]

      :enum [collapsible
             [:span [:span.schema-name schema-name] " ("]
             [:span ")"]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.schema
       [:span.schema-name (name schema)]])))