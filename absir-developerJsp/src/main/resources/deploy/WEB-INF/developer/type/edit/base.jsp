<%@ include file="../../common/edit.jsp"%>
<%
	request.setAttribute("base.jsp", true);
	element = (Element) nodes.get(0);
	input = element.getElementsByTag("input").get(0);
	request.setAttribute("element", element);
	request.setAttribute("input", input);

	if (!"search".equals(Scenario.get(request))) {
		if (field.getEditable() == JeEditable.LOCKED) {
	input.attr("readonly", "readonly");
		}

		if (!field.isNullable()) {
	input.addClass("required");
		}

		Object validatorClass = field.getMetas().get("validatorClass");
		if (validatorClass != null) {
	input.addClass(validatorClass.toString());
		}

		Object validators = field.getMetas().get("validators");
		if (validators != null && validators instanceof Map) {
	for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) validators).entrySet()) {
		input.attr(entry.getKey().toString(), entry.getValue().toString());
	}
		}
	}
%>