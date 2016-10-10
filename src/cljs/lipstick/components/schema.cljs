(ns lipstick.components.schema
  "This namespace is for rendering schemas in form of collapsible RTF representation.
  Main component is [schema name schema-def full-spec]

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
      male
      female
    )
  }
  "
  (:require [goog.string :as gstring]
            [clojure.string :as str]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys]]))

(def ellipsis
  "A constant to use to indicate collapsed state"
  (gstring/unescapeEntities "&hellip;"))


(defn bracket-symbols
  "Open & close symbols for types of structures"
  [schema]
  (if (:enum schema)
    ["(" ")"]
    (case (:type schema)
      "object" ["{" "}"]
      "array" ["[" "]"]
      nil)))


(defn deref-json
  "Resoves actual content from $ref objects"
  [schema swag-root]
  (if-let [ref (:$ref schema)]
    (let [parts (str/split ref "/")
          name (last parts)
          path (rest parts)]
      [name (get-in swag-root (map keyword path))])
    [nil schema]))

(defn brackets
  "Provides open & close sequences for complex schemas.
  Like `parents: Parents array[object Person{ ... }]`
                              ^^^^^^^^^^^^^^^     ^^"
  [{type :type :as schema} swag-root]
  (let [[open close] (bracket-symbols schema)]
    (if-not (= type "array")
      [open close]
      (let [[child-name child-schema] (deref-json (:items schema) swag-root)
            [child-open child-close] (brackets child-schema swag-root)]
        [[:span open (:type child-schema) (when child-name [:span " " [:span.schema-name child-name]]) child-open]
         [:span child-close close]]))))


(defn field-labels
  "Provides open and close labes that suits for for collapsible component.
  Like `parents: Optional array[object Person{ ... }]
        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^     ^^"
  [field-name is-required schema-name schema swag-root]
  (if-not schema
    [[:span.field-name field-name] nil]
    (let [[open close] (brackets schema swag-root)]
      [[:span.field-label {:class (if is-required "required" "optional")}
        [:span.field-name field-name] ": "
        (when is-required [:span.star.tooltipped " * " [:span.tip "required"]])
        (when schema-name [:span [:span.schema-name schema-name] " "])
        [:span (:type schema)]
        open]
       [:span {:class (if is-required "required" "optional")} close]])))


(defn schema-children
  "Recursively renders everything that should go inside collapsible.
  Returns list of [name data] pairs or nil, if there is no children,
  so regular label could be used instead of collapsible."
  [schema-raw swag-root]
  (let [[_ schema] (deref-json schema-raw swag-root)]
    (if (:enum schema)
      (->> schema :enum (map #(do [% nil])))
      (case (:type schema)
        "object" (:properties schema)
        "array"  (schema-children (:items schema) swag-root)
        nil))))

(defn field
  "Renders field of the schema either as
  a collapsible or as a regular label"
  [name is-required schema-raw swag-root]
  (let [[schema-name schema] (deref-json schema-raw swag-root)
        [main-label tail-label] (field-labels name
                                              is-required
                                              schema-name
                                              schema
                                              swag-root)
        child-required (->> schema :required (map keyword) set)
        children (schema-children schema swag-root)]
    (if (seq children)
      [collapsible {:collapsed true
                    :ellipsis ellipsis
                    :tail tail-label}
       main-label
       (doall (for [[name child-schema] children]
                ^{:key name}
                [field name (child-required name) child-schema swag-root]))]

      [:div.field main-label tail-label])))

(defn schema
  "Renders schema definition (no $ref here)"
  [schema-name-opt schema-raw root & collapsed]
  (if schema-raw
    (let [[deref-name schema-data] (deref-json schema-raw root)
          children (schema-children schema-data root)]
      (if (not-empty children)
        (let [
              schema-name (or schema-name-opt deref-name)
              is-required (->> schema-data :required (map keyword) set)
              [open close] (brackets schema-data root)
              ]
          [collapsible {:class "schema"
                        :collapsed (or collapsed false)
                        :ellipsis ellipsis
                        :tail close}
           [:span [:span.schema-name schema-name] " " (:type schema-data) open]
           (doall (for [[field-name data] children]
                    ^{:key field-name}
                    [field field-name (is-required field-name) data root]))])
        [:span.schema (:type schema-raw)]))))