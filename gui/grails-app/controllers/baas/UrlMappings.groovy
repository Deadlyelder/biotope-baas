package baas

class UrlMappings {

    static mappings = {
        // "/pay/$token"(controller: "payment", action:'invoice')
        "/list"(controller: "payment", action:'tokens')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "payment", action:'tokens')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
