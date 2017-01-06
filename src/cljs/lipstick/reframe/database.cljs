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
            :data {:tags {}
                   :paths {"/pet" {:get {:parameters [{:in "path"
                                                       :name "id"}]
                                         :responses {"405" {:description ""
                                                            :schema {}}}}}}}}
           {:id 2
            #_(...)}]

   ; State of UI elements
   :ui {:selected-spec-id nil #_1

        :spec
        ;grouped around UI data
        {1 {:tags {"name" {:collapsed false
                           :paths {["GET" "/pet/{id}"]
                                   {:collapsed true
                                    :parameters {[:query "petId"] {:value "asasd"}}
                                    :calls [{:parameters [#_"snapshot of :parameters above"]
                                             :response {#_"edn-parsed reponse"}}]}}}

                   ; paths outside tags store their state here
                   nil {:paths {["GET" "/pet/{id}"]
                                {:collapsed true}}}}}}}})