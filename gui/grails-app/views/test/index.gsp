<!doctype html>
<html>

<head>
    <meta name="layout" content="main" />
    <title>Welcome to Grails</title>
</head>

<body>
    <content tag="nav">
        <div id="content" role="main">
            <section class="row colset-2-its">
                <h1>
                    <g:if test="${token}">
                        ${token}
                        <g:form class="form-inline">
                            <g:textField name="token" value="${token}" />
                            <g:actionSubmit action="next" value="OK" class="btn btn-success" />
                            <g:actionSubmit action="cancel" value="cancel" class="btn btn-danger" />
                            <g:link action="test" target="_blank">${fieldValue(bean: userInstance, field: "name")}</g:link>
                        </g:form>
                    </g:if>
                </h1>
            </section>
        </div>

</body>

</html>