(ns lipstick.components.schema
  "This namespace is for rendering schemas in form of collapsible RTF representation.
  Main component is [schema swagger-def]

  Here are some examples:
  + Article {
    title: string
    body: string
    tags: array[string]
  }

  + Tags [{
    tag_name: string
    count: number
  }]

  + Person {
    name: string
    gender: string Gender(
              ^
              | schema type
      male
      female
    )
  }
  "
  (:require [goog.string :as gstring]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys]]))

(def ellipsis
  "A constant to use to indicate collapsed state"
  (gstring/unescapeEntities "&hellip;"))


(def type-brackets
  "Open & close symbols for types of structures"
  {:object ["{" "}"]
   :array ["[" "]"]
   :enum ["(" ")"]})


(defn schema-name
  "Used in the field description:
  gender: string Gender(male, female)
          ^^^^^^^^^^^^^ - this is schema-type"
  [schema]
  (let [name (:name schema)
        schema-type (if (= :enum (:type schema))
                      (:item-type schema)
                      (:type schema))]
    (if name
      [:span schema-type " " [:span.schema-name name]]
      (or schema-type (str schema)))))

(defn deref-schema
  "Resoves actual schema from $ref objects"
  [schema swag-root]
  (if (= :reference (:type schema))
    (get swag-root (:name schema))
    schema))

(defn brackets
  "Provides open & close sequences for complex schemas.
  Like `parents: Parents array[object Person{ ... }]`
                         ^^^^^^^^^^^^^^^^^^^^     ^^"
  [{type :type :as schema} swag-root]
  (let [[open close] (type-brackets type)]
    (if-not (= type :array)
      [open close]
      (let [derefed (deref-schema (:item-schema schema) swag-root)
            [child-open child-close] (brackets derefed swag-root)]
        [[:span open (schema-name derefed) child-open]
         [:span child-close close]]))))


(defn field-labels
  "Provides open and close labes that suits for for collapsible component.
  Like `parents: Optional array[object Person{ ... }]
        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^     ^^"
  [field-name optional schema swag-root]
  (let [[open close] (brackets schema swag-root)]
    (if-not schema
      [[:span.field-name field-name] nil]
      [[:span.field-label {:class (when optional "optional")}
        [:span.field-name field-name] ": "
        (when optional "(Optional) ")
        (schema-name schema)
        open]
       close])))


(defn field-children
  "Recursively renders everything that should go inside collapsible.
  Returns nil, if there is no children, so regular label could be used instead."
  [schema swag-root]
  (case (:type schema)
    :object (->> schema
                 :properties
                 (map (fn [[k v]] [k v])))

    :enum (->> schema
               :values
               (map #(do [% nil])))

    :array (field-children (-> schema :item-schema) swag-root)

    :reference (field-children (get swag-root (:name schema)) swag-root)

    nil))

(defn field
  "Renders field of the schema either as
  a collapsible or as a regular label"
  [field-name properties swag-root]
  (let [schema (deref-schema (:schema properties) swag-root)
        [main-label tail-label] (field-labels field-name
                                              (:optional properties)
                                              schema
                                              swag-root)
        children (field-children schema swag-root)]
    (if (seq children)
      [collapsible {:collapsed true
                    :ellipsis ellipsis
                    :tail tail-label}
       main-label
       (->> children
            (map (fn [[k v]] [field k v swag-root]))
            (with-keys))]

      [:div.field main-label tail-label])))

(defn schema
  "Renders schema definition"
  [schema swag-root]
  (let [schema-name (:name schema)]
    (case (:type schema)
      :object [collapsible {:class "schema"
                            :collapsed false
                            :ellipsis ellipsis
                            :tail [:span "}"]}
               [:span [:span.schema-name schema-name] " {"]
               (->> schema
                    :properties
                    (map (fn [[k v]] [field k v swag-root])) with-keys)]

      :array [collapsible {:collapsed false
                           :ellipsis ellipsis
                           :tail [:span "}]"]}
              [:span [:span.schema-name schema-name] " [" [:span.schema-name (-> schema :item-schema :name)] "{"]
              (->> schema :item-schema :properties (map (fn [[k v]] [field k v swag-root])) with-keys)]

      :enum [collapsible {:collapsed false
                          :ellipsis ellipsis
                          :tail [:span ")"]}
             [:span [:span.schema-name schema-name] " ("]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.schema
       [:span.schema-name (or (-> schema :name)
                              (str schema))]])))