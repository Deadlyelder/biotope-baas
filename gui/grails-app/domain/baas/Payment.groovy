package baas

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class Payment {

    String token
    Boolean confirmedByUser
    Boolean confirmedByService
    String hash
    String service //magic line
    Integer quantity
    Float price
    Integer verifyCount = 0
    Integer backEndCount = 0
    Boolean apiCall

    static constraints = {
        token nullable: true, blank: true
        confirmedByUser nullable: true, blank: true
        confirmedByService nullable: true, blank: true
        quantity nullable: true, blank: true
        price nullable: true, blank: true
        hash nullable: true, blank: true
        verifyCount nullable: false, blank: false, min: 0
        backEndCount nullable: false, blank: false, min: 0
        apiCall nullable: true, blank: true
        quantity nullable: true, blank: true
        price nullable: true, blank: true
    }

    static mapping = {
        verifyCount defaultValue: 0
        backEndCount defaultValue: 0
        service sqlType: 'char', length: 3788
    }
}