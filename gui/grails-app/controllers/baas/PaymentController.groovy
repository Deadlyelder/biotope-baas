package baas

import grails.validation.ValidationException
import org.springframework.http.HttpStatus

class PaymentController {

    PaymentService paymentService
    TestService testService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    /**
     * Ask for payment. Creates new Payment object in DB
     *
     * URL: http://localhost:8080/payment/pay?token=a&service=d
     *
     * @param token
     * @param service
     */
    def pay() {
        String token = params.token
        String service = params.service
        Integer quantity = params.quantity as Integer
        Float price = params.price as Float
         
        log.debug "[pay] token: ${token}, service: ${service}"
        if (!token || !service) {
            log.error 'Can not find token or service in params'
            render status: HttpStatus.BAD_REQUEST
            return
        }
        Payment p = new Payment(token: token, service: service, quantity: quantity, price: price)
        log.debug "Save new payment: ${p.save()}"
        try {
            paymentService.save(p)
        } catch (ValidationException e) {
            log.warn p.errors
            render status: HttpStatus.NOT_FOUND
            return
        }
        render status: HttpStatus.CREATED, text: 'OK'
    }

    /**
     * Verify that the payment was paid or not.
     *
     * URL: http://localhost:8080/payment/verify?token=a&service=d
     *
     * @param token
     * @param service
     * @return code 404 if payment with such token and service do not exists
     *         code 200 and hash if payment was successfully paid
     */
    def verify() {
        String token = params.token
        String service = params.service
        log.debug "[verify] token: ${token}, service: ${service}"
        if (!token || !service) {
            log.error 'Can not find token or service in params'
            render status: HttpStatus.BAD_REQUEST
            return
        }
        Payment payment = Payment.findByTokenAndService(token, service)
        if (!payment) {
            render status: HttpStatus.NOT_FOUND
            return
        }
        log.debug "Found payment: ${payment}"
        payment.verifyCount = payment.verifyCount + 1
        if (!payment || !payment.confirmedByUser) {
            log.error 'Can not find succeed payment'
            render status: HttpStatus.NOT_FOUND
            return
        }
        render status: HttpStatus.OK, text: "${payment.hash}"
    }

    /**
     * Verify Hash: verifies the hash against the services and if it has been paid or not. The call also updates VerifyCount in the table
     *
     * URL: http://localhost:8080/payment/verifyHash?hash=a&service=d
     *
     * @param hash
     * @param service
     * @return code 404 if payment for combination of such hash and/or service do not exists
     *         code 200 if payment is made for the correct services
     */
    def verifyHash() {
        String hash = params.hash
        String service = params.service
        Integer api = payment.apiCall
        log.debug "[verifyHash] hash: ${hash}, service: ${service}"
        Payment payment = Payment.findByHashAndService(hash, service, api)
        if (!payment) {
            render status: HttpStatus.NOT_FOUND
        }
        if (payment.verifyCount == 1){
        render status: HttpStatus.OK, text: "${payment.confirmedByService}"
        payment.verifyCount = payment.verifyCount + 1
        }
        render status: HttpStatus.BAD_REQUEST
    }

    /**
     * Refresh ???
     *
     * URL: http://localhost:8080/payment/refresh?hash=a
     * @param hash
     * @param service
     * @param api
     * @return ???
     */
    def refresh() {
        String hash = params.hash
        String service = params.service
        Integer apiCall = params.api
        log.debug "[refresh] hash: ${hash}, service: ${service}, api: ${apiCall}"
        if (!hash || !service || !apiCall) {
            log.error 'Can not find token or service in params'
            render status: HttpStatus.BAD_REQUEST
            return
        }
        render status: HttpStatus.OK, text: "Nothing to do here"
    }

    /**
     * Display list of new invoices /payments/
     *
     * URL: http://localhost:8080/list
     * @return code 200 and list of new payments
     */
    def tokens() {
        session.balance = session.balance ?: 2500
        def newPayments = Payment.findAllWhere(confirmedByUser: null)
        respond newPayments, model: [paymentCount: paymentService.count()]
    }

    /**
     * Cancel a new invoice
     *
     * @return code 200
     */
    def cancel(Payment payment) {
        log.debug "Payment to cancel ${payment}"
        payment.confirmedByUser = false
        paymentService.save(payment)
        log.debug "Payment canceled ${payment}"
        flash.message = 'Payment canceled. Kindly close the window.'
        redirect(action: 'tokens')
    }

    /**
     * Cancel a new invoice
     *
     * @return code 200 and payment status
     */
    def confirm(Payment payment) {
        session.balance = (session.balance ?: 2500) - payment.price
        log.debug "Payment to confirm: ${payment}"
        payment.confirmedByUser = true
        payment = testService.invoiceToThirdParty(payment)
        paymentService.save(payment)
        log.debug "Payment confirmed: ${payment}"
        payment.verifyCount = payment.verifyCount + 1
        if (payment.confirmedByService) {
            flash.message = "Payment sucessfull with hash: ${payment.hash}. You may close the window now"
        } else {
            flash.message = 'Payment failed.'
        }
        redirect(action: 'tokens')
    }

    // Grails Scaffolding methods
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond paymentService.list(params), model: [paymentCount: paymentService.count()]
    }

    def show(Long id) {
        respond paymentService.get(id)
    }

    def create() {
        respond new Payment(params)
    }

    def save(Payment payment) {
        if (payment == null) {
            notFound()
            return
        }

        try {
            paymentService.save(payment)
        } catch (ValidationException e) {
            respond payment.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'payment.label', default: 'Payment'), payment.id])
                redirect payment
            }
            '*' { respond payment, [status: HttpStatus.CREATED] }
        }
    }

    def edit(Long id) {
        respond paymentService.get(id)
    }

    def update(Payment payment) {
        if (payment == null) {
            notFound()
            return
        }

        try {
            paymentService.save(payment)
        } catch (ValidationException e) {
            respond payment.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'payment.label', default: 'Payment'), payment.id])
                redirect payment
            }
            '*' { respond payment, [status: HttpStatus.OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        paymentService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'payment.label', default: 'Payment'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'payment.label', default: 'Payment'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
