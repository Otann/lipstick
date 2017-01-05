(ns lipstick.reframe.database)


(def default-config
  {:files [{:name "Service Spec"
            :src "swagger.yaml"}]})

(def default-db
  {:name "re-frame"
   :active-page :home-page
   :config nil
   :auth nil
   :specs []
   :ui {:selected-spec-id nil}})

(def db-in-state
  {:name "lipstick"
   :active-page :home-page

   ; content of /lipstick.yaml
   :config {:files [{:name "Petstore"
                     :src "/swagger.yaml"}]
            :auth {:oauth {:auth-url "http://"
                           :redirect_url "http://"
                           :client_id "stups_mask-docs_..."
                           :realm "/employees"
                           :scope "uid"}}}
   ; token information
   :auth {:scope "uid"
          :token_type "Bearer"
          :expires_in 3599
          :access_token "0e0c8910-..."}

   ; processed specifications
   :specs [{:id 1
            :name "Petstore"
            :src "/swagger.yaml"
            :loading? false
            :data {#_(...)}}
           {:id 2
            #_(...)}]

   ; State of UI elements
   :ui {:selected-spec-id nil #_1
        :paths []
        :tags [{:name ""
                :collapsed false
                :paths [{:method "GET"
                         :path "/pet/{id}"
                         :summary ""
                         :description ""
                         :collapsed true
                         :parameters [{:in :query
                                       :name "petId"
                                       :required false
                                       :schema {#_"derefed schema"}
                                       :value ""}]
                         :responses [:code "200"
                                     :description "good one"
                                     :schema {#_"derefed schema"}]
                         :calls [{:parameters [#_"snapshot of :parameters above"]
                                  :response {#_"edn-parsed reponse"}}]}]}]}})