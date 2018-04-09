<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'payment.label', default: 'Payment')}"/>
    <g:set var="title" value="Income Payments"/>
    <title>
        ${title}
    </title>
</head>

<body>
<a href="#list-payment" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

<div id="list-payment" class="content scaffold-list" role="main">
    <h1 style="font-weight:bold">
        ${title}
    </h1>
    <h1>
        Wallet amount: ${session.balance} Satoshi
    </h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <div class="row align-items-center">
        <ol>
            <g:each in="${paymentList}" var="item">
                <li>
                    <g:form class="form-inline">
                        <g:hiddenField name="id" value="${item.id}"/>
                        <g:hiddenField name="verifyCount" value="${item.verifyCount}"/>
                        <g:hiddenField name="backEndCount" value="${item.backEndCount}"/>
                        <g:hiddenField name="service" value="${item.service}"/>
                        <g:textField name="token" value="${item.token}"/>
                        <g:actionSubmit resource="${item}" method="PUT" action="confirm" value="pay"
                                        class="btn btn-success"/>
                        <g:actionSubmit resource="${item}" method="PUT" action="cancel" value="cancel"
                                        class="btn btn-danger"/>
                        ${session.balance - item.price}
                    </g:form>
                </li>
            </g:each>
        </ol>
    </div>
</div>
</body>

</html>