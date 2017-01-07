(ns lipstick.components.spec
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [markdown.core :refer [md->html]]
            [reagent.ratom :as r :include-macros true]
            [lipstick.tools.utils :refer [with-keys deref-json containsv?]]
            [lipstick.tools.path-client :refer [make-client]]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.components.schema :refer [schema]]
            [lipstick.components.forms :as forms]
            [re-frame.core :as rf]
            [lipstick.rfnext.spec-ui :as spec-ui]
            [taoensso.timbre :as log]))

(defn markdown->div
  "Renders markdown string as reagent-compatible text"
  [data]
  [:div.markdown-body
   {:dangerouslySetInnerHTML
    {:__html (-> data md->html)}}])


(def location-icons
  "Symbolic replacements for common locations"
  {"path"     "/../"
   "formData" "form"
   "query"    "?&="
   "body"     "{..}"})


(defn responses
  "Renders list of responses"
  [responses full-spec]
  [:div.responses
   [:div.title "Responses"]
   [:table
    [:tbody
     (-> (for [[code data] responses]
           [:tr.response
            [:td.status.tag [:span.label {:class (str "code" (-> code name first) "xx")} code]]
            [:td.description (:description data)]
            [:td.format [schema nil (:schema data) full-spec true]]])
         (doall)
         (with-keys))]]])


(defn parameter [spec-id tag-name {:keys [name in required description] :as parameter} full-spec]
  [:tr.parameter
   [:td.location.tag
    [:span.label.tooltipped.tooltipped-n
     {:aria-label (str "located in: " in)}
     (or (location-icons in) in)]]
   [:td.is-required
    [:span.tooltipped.tooltipped-n
     {:aria-label (if required "required" "optional")
      :class (if required "required" "optional")} "●"]]
   [:td.name name]
   [:td.format
    (when-let [schema-data (:schema parameter)]
      [schema "body" schema-data full-spec true])
    (if-let [type (:type parameter)]
      [:code type])]
   #_(if state
       [:td.data
        [forms/atom-input state
         {:placeholder (if-let [type (:type parameter)] type "value")}]])
   [:td.description description]])


(defn parameters
  "Parameters table for path"
  [spec-id tag-name params-data full-spec]
  [:div.parameters
   [:div.title "Parameters"]
   [:table
    [:tbody
     (for [param-data params-data]
       ^{:key (:name param-data)}
       [parameter spec-id tag-name param-data full-spec])]]])


(defn path
  "Component that renders endpoint documentation"
  [spec-id tag-name path-name method path-spec full-spec]
  (let [collapsed (rf/subscribe [:ui-path-collapsed spec-id tag-name method path-name])
        client (make-client method path-name path-spec full-spec)
        [params resps state callback] client]
    (fn []
      [collapsible {:collapsed @collapsed
                    :class "path"
                    :arrow-class "path-arrow"
                    :on-toggle #(rf/dispatch [:ui-toggle-path spec-id tag-name method path-name])}
       [:span.path-title
        [:code.method {:class method} method] " "
        [:span.path-name
         {:class (if (:deprecated path-spec) "deprecated")}
         (subs (str path-name) 1)]
        (when-let [summary (:summary path-spec)] [:span.summary " " summary])]
       [:div.content
        ;[:div.summary (:summary path-spec)]
        (if (:deprecated path-spec) [:p [:span.deprecated "This path is deprecated"]])
        [:div.description (:description path-spec)]
        (if (not-empty params)
          [parameters spec-id tag-name params full-spec])
        [:div.try-out
         [:button.btn.btn-sm {:disabled (not (= :ready @state))
                              :on-click callback} "Call enpoint"]
         (if (not (= :ready @state))
           [:span.explain "Fill necessary parameters to call this endpoint"])]
        (if @resps
          [responses @resps full-spec])]])))


(defn flatten-paths
  "Turns nested path-method structure to plain vector.
  Allows to pass predicate to filter out certain paths"
  ([all-paths] (flatten-paths all-paths (constantly true)))
  ([all-paths predicate]
   (->> (for [[path-name methods] all-paths]
          (for [[method-name path-spec] methods]
            (if (predicate path-spec)
              {:method method-name
               :name path-name
               :spec path-spec})))
        (flatten)
        (filter #(not= nil %)))))


(defn paths
  "Renders list of paths, like - GET /pet/findByStatus"
  [spec-id tag-name paths-data full-spec]
  [:div.paths
   (doall (for [{:keys [name method spec]} paths-data]
            ^{:key (str name method)}
            [path spec-id tag-name name method spec full-spec]))])


(defn path-tag
  "Renders multiple paths groupped by a tag"
  [spec-id tag-data all-paths full-spec]
  (let [tag-name  (:name tag-data)
        collapsed (rf/subscribe [:ui-tag-collapsed spec-id tag-name])]
    (fn []
      (let [paths-data (flatten-paths all-paths #(containsv? (:tags %) tag-name))]
        [collapsible {:collapsed @collapsed
                      :arrow-open "–"
                      :class "tag"
                      :arrow-class "tag-label-arrow"
                      :on-toggle #(rf/dispatch [:ui-toggle-tag spec-id tag-name])}
         [:span.tag-label
          [:span.name tag-name]
          (when-let [description (:description tag-data)]
            [:span.description ": " description])]
         [paths spec-id tag-name paths-data full-spec]]))))


(defn spec-meta
  "Renders metadata for the spec"
  [info]
  [:div.meta
   [:ul
    (when-let [version (:version info)]
      [:li [:span.label "Version: "] [:code version]])
    (when-let [contact (:contact info)]
      (for [[name value] contact]
        ^{:key name}
        [:li
         [:span.label "Contact: "]
         (if (= name :email)
           [:a {:href (str "mailto:" value)} value]
           [:span value])]))
    (when-let [license (:license info)]
      [:li [:span.label "License: "] [:a {:href (:url license)} (:name license)]])
    (when-let [tos (:termsOfService info)]
      [:li [:span.label [:a {:href tos} "Terms of Service"]]])]])


(defn swagger-spec
  "Renders full swagger spec"
  [spec-id {:keys [info tags] paths-data :paths :as spec-data}]
  [:div.spec
   [:h1.spec-title (:title info)]
   [:div.description (markdown->div (:description info))]
   [spec-meta info]
   (if (empty? tags)
     [:div.no-tags
      [paths spec-id nil (flatten-paths paths-data) spec-data]]
     [:div.tags
      (doall (for [tag-data tags]
               ^{:key (:name tag-data)}
               [path-tag spec-id tag-data paths-data spec-data]))
      ; Append paths that has no tags assigned
      (let [paths-data (flatten-paths paths-data #(empty? (:tags %)))]
        (if (not-empty paths-data)
          [path-tag spec-id {:name "Without tags"} paths-data spec-data]))])])


(defn spec []
  (let [spec (rf/subscribe [::spec-ui/spec])]
    (fn []
      (log/debug "Spec:" @spec)
      (if @spec
        [swagger-spec (:idx @spec) (:spec @spec)]
        [:p "Spec is loading"]))))

