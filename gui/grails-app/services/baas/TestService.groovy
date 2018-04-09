package baas

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.apache.commons.lang.RandomStringUtils

class TestService {
  
    static transactional = false

    Payment invoiceToThirdParty(Payment payment) {
        // makePostRequest(token: payment.token)
        // Mock 3rd service:
        if (payment.token.size() < 2) {
            payment.confirmedByService = false
            return payment 
        }
        if (payment?.token[0..1] != "ln") {
            payment.confirmedByService = false
            return payment 
        }
        payment.confirmedByService = true
        payment.hash = RandomStringUtils.random(24, true, true)
        payment
    }

    def makePostRequest(Map payload) {
        String url = 'http://httpbin.org/get'
        log.debug "payload: ${payload}"
        RestBuilder rest = new RestBuilder()
        RestResponse restResponse = rest.post(url) {
            json (payload)
        }
        Integer responseCode = restResponse.getStatus()
        log.debug "URL: ${url}. CODE: ${responseCode}. JSON: ${restResponse?.json?.toString(2) }"
        restResponse
    }
}
